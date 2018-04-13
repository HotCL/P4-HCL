package parser

import exceptions.*
import parser.typechecker.ITypeChecker
import parser.typechecker.TypeChecker
import lexer.ILexer
import lexer.PositionalToken
import lexer.Token
import utils.BufferedLaabStream
import utils.IBufferedLaabStream

class Parser(val lexer: ILexer): IParser, ITypeChecker by TypeChecker(),
        IBufferedLaabStream<PositionalToken> by BufferedLaabStream(lexer.getTokenSequence()) {
    override fun generateAbstractSyntaxTree() = AbstractSyntaxTree().apply {
        while (hasNext()) {
            if (current.token != Token.SpecialChar.EndOfLine) {
                children.add(parseCommand())
            }
        }
    }

    private fun parseCommand(): TreeNode.Command {
        val command = when (current.token) {
            is Token.Type -> parseDeclaration()
            is Token.Identifier -> {
                if (peek().token == Token.SpecialChar.Equals) {
                    parseAssignment()
                }
                else parseExpression()
            }
            is Token.Literal, //Fallthrough
            Token.SpecialChar.BlockStart,
            Token.SpecialChar.SquareBracketStart,
            Token.SpecialChar.ParenthesesStart
                -> parseExpression()
            Token.Return -> parseReturnStatement()
            else -> throw UnexpectedTokenError(current.lineNumber, current.lineIndex,
                                               lexer.inputLine(current.lineNumber), current.token)
        }
        flushNewLine()
        return command
    }

    private fun parseReturnStatement(): TreeNode.Command.Return {
        accept<Token.Return>()
        val expression = parseExpression()
        return TreeNode.Command.Return(expression)
    }

    private inline fun<reified T: Token> accept(): T {
        val token = current.token
        val currentLineNumber = current.lineNumber
        val currentIndex = current.lineIndex
        moveNext()
        if (token is T) {
            return token
        } else {
            throw WrongTokenTypeError(currentLineNumber, currentIndex,
                    lexer.inputLine(currentLineNumber), T::class.simpleName, token)
        }
    }

    private inline fun<reified T> tryAccept() = (current.token as? T)?.let { moveNext(); true } ?: false

    private fun flushNewLine(requireNewLine: Boolean = true) {
        if (hasNext() && requireNewLine) accept<Token.SpecialChar.EndOfLine>()
        while (current.token == Token.SpecialChar.EndOfLine && hasNext())
            accept<Token.SpecialChar.EndOfLine>()
    }

    private fun acceptIdentifier() =
            TreeNode.Command.Expression.Value.Identifier(accept<Token.Identifier>().value)

    private fun acceptLiteral(): TreeNode.Command.Expression.Value.Literal {
        val litToken = accept<Token.Literal>()
        return when (litToken) {
            is Token.Literal.Number -> TreeNode.Command.Expression.Value.Literal.Number(litToken.value)
            is Token.Literal.Text -> TreeNode.Command.Expression.Value.Literal.Text(litToken.value)
            is Token.Literal.Bool -> TreeNode.Command.Expression.Value.Literal.Bool(litToken.value)
        }
    }

    private fun parseSingleParameter(): TreeNode.ParameterDeclaration {
        val type = parseType()
        val identifier = acceptIdentifier()
        if (current.token == Token.SpecialChar.Equals)
            throw InitializedFunctionParameterError(current.lineNumber,
                    current.lineIndex,
                    lexer.inputLine(current.lineNumber))
        return TreeNode.ParameterDeclaration(type, identifier)
    }

    private fun parseFunctionParameters(): List<TreeNode.ParameterDeclaration> {
        val parameters = mutableListOf<TreeNode.ParameterDeclaration>()

        accept<Token.SpecialChar.ParenthesesStart>()
        while (current.token != Token.SpecialChar.ParenthesesEnd) {
            parameters.add(parseSingleParameter())
            if (!tryAccept<Token.SpecialChar.ListSeparator>()) break
        }
        accept<Token.SpecialChar.ParenthesesEnd>()
        return parameters
    }

    private fun parseDeclaration(): TreeNode.Command.Declaration {
        val type = parseType(implicitAllowed = true)
        val identifier = acceptIdentifier()
        val expression = if (tryAccept<Token.SpecialChar.Equals>()) {
            parseExpression()
        } else null

        if (expression == null && (type == TreeNode.Type.Func.ImplicitFunc || type == TreeNode.Type.Var))
            throw Exception("Cannot declare implicit type without expression")
        if (expression != null && (type == TreeNode.Type.Func.ImplicitFunc || type == TreeNode.Type.Var)) {
            enterSymbol(identifier.name, expression.type)
        }
        else enterSymbol(identifier.name, type)

        return if (expression != null) {
            if (!checkExpressionTypeMatchesSymbolType(expression, identifier.name))
                throw UnexpectedTypeError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                        type.toString(), expression.type.toString())
            else TreeNode.Command.Declaration(expression.type, identifier, expression)
        } else TreeNode.Command.Declaration(type, identifier, expression)
    }

    private fun parseAssignment(): TreeNode.Command.Assignment {
        val identifier = acceptIdentifier()
        accept<Token.SpecialChar.Equals>()
        val expression = parseExpression()
        return if (!checkExpressionTypeMatchesSymbolType(expression, identifier.name))
            throw UnexpectedTypeError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                                      retrieveSymbol(identifier.name).identifier.toString(),
                                      expression.type.toString())
         else TreeNode.Command.Assignment(identifier, expression)
    }

    //region Type declarations
    private fun  parseType(implicitAllowed: Boolean = false): TreeNode.Type {
        val currentPosToken = current
        moveNext()
        return when (currentPosToken.token) {
            is Token.Type.Number -> TreeNode.Type.Number
            is Token.Type.Text -> TreeNode.Type.Text
            is Token.Type.Bool -> TreeNode.Type.Bool
            is Token.Type.Var -> if (implicitAllowed) TreeNode.Type.Var
                                 else throw Exception("Explicit type not allowed here!")
            is Token.Type.Func -> parseFuncType(implicitAllowed)
            is Token.Type.Tuple -> parseTupleType()
            is Token.Type.List -> parseListType()
            else -> throw UnexpectedTokenError(currentPosToken, lexer.inputLine(current.lineNumber))
        }
    }

    private fun parseTypes(parsingMethod: () -> TreeNode.Type): List<TreeNode.Type> {
        val elementTypes = mutableListOf<TreeNode.Type>()

        while (true) {
            elementTypes.add(parsingMethod())
            if (!tryAccept<Token.SpecialChar.ListSeparator>()) return elementTypes
        }
    }



    private fun parseFuncType(implicitAllowed: Boolean): TreeNode.Type.Func {
        return if (current.token == Token.SpecialChar.SquareBracketStart) {
            accept<Token.SpecialChar.SquareBracketStart>()

            val parameters = parseTypes({
                if (peek().token == Token.SpecialChar.SquareBracketEnd && tryAccept<Token.Type.None>())
                    TreeNode.Type.None
                else parseType()
            })
            accept<Token.SpecialChar.SquareBracketEnd>()
            val returnType = parameters.last()


            TreeNode.Type.Func.ExplicitFunc(parameters.dropLast(1), returnType)
        }
        else
        {
            if (implicitAllowed)
                TreeNode.Type.Func.ImplicitFunc
            else
                throw ImplicitTypeNotAllowed(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber))
        }
    }

    private fun parseLambdaDeclaration(): TreeNode.Command.Expression.LambdaExpression {
        val parameters = parseFunctionParameters()
        accept<Token.SpecialChar.Colon>()
        val returnType = if (current.token == Token.Type.None) {
            moveNext()
            TreeNode.Type.None
        } else parseType()

        flushNewLine(false)
        val body = parseLambdaBody()
        return TreeNode.Command.Expression.LambdaExpression(parameters, returnType, body)
    }

    private fun parseLambdaBody(): List<TreeNode.Command> {
        val commands = mutableListOf<TreeNode.Command>()
        accept<Token.SpecialChar.BlockStart>()
        while (current.token != Token.SpecialChar.BlockEnd) {
            commands.add(parseCommand())
        }
        accept<Token.SpecialChar.BlockEnd>()
        return commands
    }

    private fun parseTupleType(): TreeNode.Type.Tuple {
        accept<Token.SpecialChar.SquareBracketStart>()
        val elementTypes = parseTypes { parseType() }
        accept<Token.SpecialChar.SquareBracketEnd>()
        return TreeNode.Type.Tuple(elementTypes)
    }

    private fun parseListType(): TreeNode.Type.List {
        accept<Token.SpecialChar.SquareBracketStart>()
        val elementType: TreeNode.Type = parseType()
        accept<Token.SpecialChar.SquareBracketEnd>()
        return TreeNode.Type.List(elementType)
    }
//endregion

    //region ExpressionParsing
    private fun parsePotentialFunctionCall(expression: TreeNode.Command.Expression): TreeNode.Command.Expression =
        when (current.token) {
            is Token.Identifier -> {
                val token = accept<Token.Identifier>()
                val symbol = retrieveSymbol(token.value)
                if (symbol.isFunctions) {
                    val funcDecls = symbol.functions
                    val secondaryParams = funcDecls.first().paramTypes.drop(1).map { parseExpressionAtomic() }
                    val inferredParams =
                            listOf(expression.type) + secondaryParams.map { it.type }
                    val declaration = funcDecls.getTypeDeclaration(inferredParams)
                    if (declaration == null) throw Exception("Function declaration of function ${token.value}" +
                            " not found with params $inferredParams")
                    else TreeNode.Command.Expression.FunctionCall(
                            TreeNode.Command.Expression.Value.Identifier(token.value),
                            listOf(expression) + secondaryParams
                    )
                } else expression
            }
            else -> expression
        }.let { if (expression != it) parsePotentialFunctionCall(it) else expression }

    private fun parseExpression() = parsePotentialFunctionCall(parseExpressionAtomic())

    private fun parseExpressionAtomic(): TreeNode.Command.Expression =
        when (current.token) {
            Token.SpecialChar.SquareBracketStart -> parseListDeclaration()
            Token.SpecialChar.ParenthesesStart -> {
                if (peek().token is Token.Type || peek().token == Token.SpecialChar.ParenthesesEnd) {
                    parseLambdaDeclaration()
                }
                else {
                    if (upcomingTuple()) parseTupleExpression() else {
                        accept<Token.SpecialChar.ParenthesesStart>()
                        parseExpression().apply { accept<Token.SpecialChar.ParenthesesEnd>() }
                    }
                }
            }
            is Token.Literal.Text,
            is Token.Literal.Bool,
            is Token.Literal.Number -> acceptLiteral()
            is Token.Identifier -> {
                val token = accept<Token.Identifier>()
                retrieveSymbol(token.value).handle(
                        {
                            if (it.first().paramTypes.isEmpty()) {
                                TreeNode.Command.Expression.FunctionCall(
                                        TreeNode.Command.Expression.Value.Identifier(token.value), listOf()
                                )
                            } else throw Exception("Function ${token.value} can not be invoked with 0 arguments")
                        },
                        { TreeNode.Command.Expression.Value.Identifier(token.value) },
                        {
                            throw UndeclaredError(current.lineNumber, current.lineIndex,
                                                  lexer.inputLine(current.lineIndex), token.value)
                        }
                )
            }
            else -> throw UnexpectedTokenError(current, lexer.inputLine(current.lineNumber))
        }

    private fun upcomingTuple(): Boolean {
        var lookAhead = 1
        var depth = 0
        while (hasAhead(lookAhead++)) {
            when (lookAhead(lookAhead).token) {
                Token.SpecialChar.ParenthesesEnd -> if (depth == 0) return false else depth--
                Token.SpecialChar.ParenthesesStart,
                Token.SpecialChar.SquareBracketStart -> depth++
                Token.SpecialChar.SquareBracketEnd -> depth--
                Token.SpecialChar.ListSeparator -> if (depth == 0) return true
            }
        }
        throw Exception("Unclosed parentheses")
    }

    private fun parseTupleExpression() =
        TreeNode.Command.Expression.Value.Literal.Tuple(
            mutableListOf<TreeNode.Command.Expression>().apply {
                accept<Token.SpecialChar.ParenthesesStart>()
                while (current.token != Token.SpecialChar.ParenthesesEnd) {
                    add(parseExpression())
                    if (!tryAccept<Token.SpecialChar.ListSeparator>()) break
                }
                accept<Token.SpecialChar.ParenthesesEnd>()
            }
        )

    private fun parseListDeclaration() =
        TreeNode.Command.Expression.Value.Literal.List(
            mutableListOf<TreeNode.Command.Expression>().apply {
                accept<Token.SpecialChar.SquareBracketStart>()
                while (current.token != Token.SpecialChar.SquareBracketEnd) {
                    add(parseExpression())
                    if (first().type != last().type)
                        throw Exception("Element ${last()} did not match type of first element in list")
                    if (!tryAccept<Token.SpecialChar.ListSeparator>()) break
                }
                accept<Token.SpecialChar.SquareBracketEnd>()
            }
        )

    //endregion ExpressionParsing
}
