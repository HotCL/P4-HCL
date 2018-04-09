package parser

import exceptions.InitializedFunctionParameterError
import exceptions.NoneAsInputError
import exceptions.UnexpectedTokenError
import exceptions.WrongTokenTypeError
import lexer.ILexer
import lexer.PositionalToken
import lexer.Token
import parser.symboltable.ISymbolTable
import parser.symboltable.SymbolTable
import utils.BufferedLaabStream
import utils.IBufferedLaabStream

class Parser(val lexer: ILexer): IParser, ISymbolTable by SymbolTable(),
                                 IBufferedLaabStream<PositionalToken> by BufferedLaabStream(lexer.getTokenSequence()) {
    override fun generateAbstractSyntaxTree() = AbstractSyntaxTree().apply {
        while (hasNext()) {
            if (current.token !is Token.SpecialChar.EndOfLine) {
                children.add(parseCommand())
            }
        }
    }

    private fun parseCommand(): TreeNode.Command {
        val command: TreeNode.Command
        when (current.token) {
            is Token.Type -> {
                command = parseDeclaration()
                acceptEndOfLines()
                return command
            }
            is Token.Identifier -> {
                when (peek().token) {
                    is Token.SpecialChar.Equals -> {
                        command = parseAssignment()
                        acceptEndOfLines()
                        return command
                    }
                    else -> TODO("Function call without parameters")
                }
            }
            else -> throw Exception("Make this a unexpected token exception once that is implemented...")
        }
    }

    private inline fun<reified T> accept(): T {
        val token = current.token
        val currentLineNumber = current.lineNumber
        val currentIndex = current.lineIndex
        moveNext()
        if (token is T) {
            return token
        } else {
            throw WrongTokenTypeError(currentLineNumber, currentIndex, lexer.inputLine(currentLineNumber), T::class as Token, token)
        }
    }

    private fun acceptEndOfLines() {
        while (current.token is Token.SpecialChar.EndOfLine && hasNext())
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

    private fun parseSingleParameter(): TreeNode.Command.Declaration {
        val type = parseType()
        val identifier = acceptIdentifier()
        if (current.token is Token.SpecialChar.Equals)
            throw InitializedFunctionParameterError(current.lineNumber,
                                                    current.lineIndex,
                                                    lexer.inputLine(current.lineNumber))
        return TreeNode.Command.Declaration(type, identifier, null)
    }

    private fun parseFunctionParameters(): List<TreeNode.Command.Declaration> {
        val parameters = mutableListOf<TreeNode.Command.Declaration>()

        if (current.token is Token.SpecialChar.ParenthesesStart) {
            if (peek().token !is Token.SpecialChar.ParenthesesEnd) {
                while (true) {
                    if (moveNext().token is Token.Type) {
                        if (current.token is Token.Type.None)
                            throw NoneAsInputError(current.lineNumber, current.lineIndex,
                                                   lexer.inputLine(current.lineNumber))
                        parameters.add(parseSingleParameter())
                    } else throw UnexpectedTokenError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                            current.token)
                    if (current.token !is Token.SpecialChar.ListSeparator) break
                }
            }
            else moveNext()
            if (current.token !is Token.SpecialChar.ParenthesesEnd)
                throw WrongTokenTypeError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                        Token.SpecialChar.ParenthesesEnd(), current.token)
        }
        else throw WrongTokenTypeError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                                       Token.SpecialChar.ParenthesesStart(), current.token)
        return parameters
    }

    private fun parseDeclaration(): TreeNode.Command.Declaration {
        val type = parseType()
        val identifier = acceptIdentifier()
        val expression = if (current.token is Token.SpecialChar.Equals) {
            moveNext()
            parseExpression()
        } else null
        return TreeNode.Command.Declaration(type, identifier, expression)
    }

    private fun parseAssignment(): TreeNode.Command.Assignment {
        val identifier = acceptIdentifier()
        accept<Token.SpecialChar.Equals>()
        val expression = parseExpression()
        return TreeNode.Command.Assignment(identifier, expression)
    }

//region Type declarations
    private fun  parseType() = when (current.token) {
        is Token.Type.Number -> TreeNode.Type.Number()
        is Token.Type.Text -> TreeNode.Type.Text()
        is Token.Type.Bool -> TreeNode.Type.Bool()
        is Token.Type.None -> TreeNode.Type.None()
        is Token.Type.Func -> parseFuncType()
        is Token.Type.Tuple -> parseTupleType()
        is Token.Type.List -> parseListType()
        else -> throw UnexpectedTokenError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                                           current.token)
    }.also { moveNext() }

    private fun parseFuncType(): TreeNode.Type.Func {
        val parameters = mutableListOf<TreeNode.Type>()
        if (moveNext().token is Token.SpecialChar.SquareBracketStart) {
            moveNext()
            while (true) {
                if (current.token is Token.Type) {
                    if (current.token is Token.Type.None && peek().token !is Token.SpecialChar.SquareBracketEnd)
                        throw NoneAsInputError(current.lineNumber, current.lineIndex,
                                               lexer.inputLine(current.lineNumber))
                    else parameters.add(parseType())
                }
                else throw UnexpectedTokenError(current.lineNumber, current.lineIndex,
                                                lexer.inputLine(current.lineNumber), current.token)
                if (current.token is Token.SpecialChar.ListSeparator) moveNext()
                else break
            }
            if (current.token !is Token.SpecialChar.SquareBracketEnd)
                throw WrongTokenTypeError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                                          Token.SpecialChar.SquareBracketStart(), current.token)
        }
        else throw NotImplementedError()
        val returnType = parameters.last()
        return TreeNode.Type.Func(parameters.dropLast(1), returnType)
    }

    private fun parseLambdaExpression(): TreeNode.Command.Expression.LambdaExpression {
        val parameters = parseFunctionParameters()
        moveNext()
        accept<Token.SpecialChar.Colon>()
        val returnType = parseType()
        acceptEndOfLines()
        val body = parseLambdaBody()
        return TreeNode.Command.Expression.LambdaExpression(parameters, returnType, body)
    }

    private fun parseLambdaBody(): List<TreeNode.Command> {
        val commands = mutableListOf<TreeNode.Command>()
        accept<Token.SpecialChar.BlockStart>()
        while (current.token !is Token.SpecialChar.BlockEnd) {
            commands.add(parseCommand())
        }
        accept<Token.SpecialChar.BlockEnd>()
        return commands
    }

    private fun parseTupleType(): TreeNode.Type.Tuple {
        val elementTypes = mutableListOf<TreeNode.Type>()
        if (moveNext().token is Token.SpecialChar.SquareBracketStart) {
            moveNext()
            while (true) {
                if (current.token is Token.Type && current.token !is Token.Type.None) elementTypes.add(parseType())
                else
                if (current.token is Token.SpecialChar.ListSeparator) moveNext()
                else break
            }
            if (current.token !is Token.SpecialChar.SquareBracketEnd)
                throw WrongTokenTypeError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                        Token.SpecialChar.SquareBracketEnd(), current.token)
        }
        else throw NotImplementedError()
        return TreeNode.Type.Tuple(elementTypes)
    }

    private fun parseListType(): TreeNode.Type.List {
        val elementType: TreeNode.Type
        if (moveNext().token is Token.SpecialChar.SquareBracketStart) {
            moveNext()
            if (current.token is Token.Type && current.token !is Token.Type.None) elementType = parseType()
            else throw UnexpectedTokenError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                    current.token)
            if (current.token !is Token.SpecialChar.SquareBracketEnd)
                throw WrongTokenTypeError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                        Token.SpecialChar.SquareBracketEnd(), current.token)
        }
        else throw NotImplementedError()
        return TreeNode.Type.List(elementType)
    }
//endregion

//region ExpressionParsing
    private fun parseExpression(): TreeNode.Command.Expression {
        // Be aware that below is not correct for the full implementation. Here we expect that if there is only one token
        // the token will be a literal, but it could also be an identifier.
        when(current.token){
            is Token.SpecialChar.SquareBracketStart -> return parseListDeclaration()
            is Token.SpecialChar.ParenthesesStart -> {
                if (peek().token is Token.Type || peek().token is Token.SpecialChar.ParenthesesEnd){
                    return parseLambdaExpression()
                }
                else {
                    var counter = 1
                    while(true){
                        if(lookAhead(counter).token is Token.SpecialChar.ListSeparator) return parseTupleExpression()
                        else if (lookAhead(counter++).token is Token.SpecialChar.ParenthesesEnd) break
                    }
                }
            }
            is Token.Literal.Text -> {
                if (isLiteral(peek().token)) return acceptLiteral()
                TODO ("make else that parses expression  (like \"hej\" + \"du\")")
            }
            is Token.Literal.Bool -> {
                if (isLiteral(peek().token)) return acceptLiteral()
                TODO ("make else that parses expression  (like 5 < 7)")
            }
            is Token.Literal.Number -> {
                if (isLiteral(peek().token)) return acceptLiteral()
                TODO ("make else that parses expression  (like 5+5)")
            }
        }
        throw Exception("Unrecognized expression")
    }

    private fun isLiteral(token: Token): Boolean{
        if (token is Token.SpecialChar.EndOfLine || token is Token.SpecialChar.ListSeparator
                || token is Token.SpecialChar.ParenthesesEnd) return true
        return false
    }

    private fun parseTupleExpression(): TreeNode.Command.Expression{
        val elements = mutableListOf<TreeNode.Command.Expression>()
        moveNext()
        while (true){
            if (current.token is Token.Literal) elements.add(acceptLiteral())
            else if (current.token is Token.Identifier) elements.add(acceptIdentifier())
            else throw UnexpectedTokenError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                    current.token)
            if (current.token is Token.SpecialChar.ListSeparator) moveNext()
                else break
        }
        if (current.token !is Token.SpecialChar.ParenthesesEnd)
            throw WrongTokenTypeError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                    Token.SpecialChar.ParenthesesEnd(), current.token)
        moveNext()
        return TreeNode.Command.Expression.Value.Literal.Tuple(elements)
    }

    private fun parseListDeclaration(): TreeNode.Command.Expression{
        val elements = mutableListOf<TreeNode.Command.Expression>()
        moveNext()
        while (true) {
            if (current.token is Token.Literal) elements.add(acceptLiteral())
            else if (current.token is Token.Identifier) elements.add(acceptIdentifier())
            else throw UnexpectedTokenError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                    current.token)
            if (current.token is Token.SpecialChar.ListSeparator) moveNext()
                else break
        }
        if (current.token !is Token.SpecialChar.SquareBracketEnd)
            throw WrongTokenTypeError(current.lineNumber, current.lineIndex, lexer.inputLine(current.lineNumber),
                    Token.SpecialChar.SquareBracketEnd(), current.token)
        moveNext()
        return TreeNode.Command.Expression.Value.Literal.List(elements)
    }
    //endregion ExpressionParsing
}
