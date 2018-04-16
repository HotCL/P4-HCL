package parser

import parser.typechecker.ITypeChecker
import parser.typechecker.TypeChecker
import lexer.ILexer
import lexer.PositionalToken
import lexer.Token
import parser.symboltable.EnterSymbolResult
import parser.typechecker.ExprResult
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

    private fun parseCommand(): AstNode.Command {
        val command = when (current.token) {
            is Token.Type -> parseDeclaration()
            is Token.Identifier -> {
                if (peek().token == Token.SpecialChar.Equals) {
                    parseAssignment()
                }
                else parseExpression()
            }

        // eh wut? in what case is "{ .* }" a singular command ?
            Token.SpecialChar.BlockStart -> AstNode.Command.Expression.LambdaExpression(listOf(), AstNode.Type.None,
                    parseLambdaBody())
            is Token.Literal, //Fallthrough
            Token.SpecialChar.SquareBracketStart,
            Token.SpecialChar.Colon,
            Token.SpecialChar.ParenthesesStart
            -> parseExpression()
            Token.Return -> parseReturnStatement()
            else -> unexpectedTokenError(current.token)
        }
        flushNewLine()
        return command
    }

    private fun parseReturnStatement(): AstNode.Command.Return {
        accept<Token.Return>()
        val expression = parseExpression()
        return AstNode.Command.Return(expression)
    }

    private inline fun<reified T: Token> accept(): T {
        val token = current.token
        moveNext()
        if (token is T) {
            return token
        } else {
            wrongTokenTypeError(T::class.simpleName!!, token)
        }
    }

    private inline fun<reified T> tryAccept() = (current.token as? T)?.let { moveNext(); true } ?: false

    private fun flushNewLine(requireNewLine: Boolean = true) {
        if (hasNext() && requireNewLine) accept<Token.SpecialChar.EndOfLine>()
        while (current.token == Token.SpecialChar.EndOfLine && hasNext())
            accept<Token.SpecialChar.EndOfLine>()
    }

    private fun acceptIdentifier() =
            AstNode.Command.Expression.Value.Identifier(accept<Token.Identifier>().value)

    private fun acceptLiteral(): AstLiteral {
        val litToken = accept<Token.Literal>()
        return when (litToken) {
            is Token.Literal.Number -> AstNode.Command.Expression.Value.Literal.Number(litToken.value)
            is Token.Literal.Text -> AstNode.Command.Expression.Value.Literal.Text(litToken.value)
            is Token.Literal.Bool -> AstNode.Command.Expression.Value.Literal.Bool(litToken.value)
        }
    }

    private fun parseSingleParameter(): AstNode.ParameterDeclaration {
        val type = parseType(genericAllowed = true)
        val identifier = acceptIdentifier()
        if (current.token == Token.SpecialChar.Equals) initializedFunctionParameterError()
        return AstNode.ParameterDeclaration(type, identifier)
    }

    private fun parseFunctionParameters(): List<AstNode.ParameterDeclaration> {
        val parameters = mutableListOf<AstNode.ParameterDeclaration>()

        accept<Token.SpecialChar.ParenthesesStart>()
        while (current.token != Token.SpecialChar.ParenthesesEnd) {
            parameters.add(parseSingleParameter())
            if (!tryAccept<Token.SpecialChar.ListSeparator>()) break
        }
        accept<Token.SpecialChar.ParenthesesEnd>()
        return parameters
    }

    private fun parseDeclaration(): AstNode.Command.Declaration {
        val type = parseType(implicitAllowed = true)
        val identifier = acceptIdentifier()

        if (tryAccept<Token.SpecialChar.Equals>()) {
            val expression = parseExpression()
            if (type != AstNode.Type.Func.ImplicitFunc && type != AstNode.Type.Var && expression.type.type() != type)
                unexpectedTypeError(type.toString(),expression.type.type().toString())

            when (enterSymbol(identifier.name, expression.type.type())) {
                EnterSymbolResult.OverloadAlreadyDeclared ->
                    error("Function of same name with these parameters has already been declared!")
                EnterSymbolResult.OverloadDifferentParamNums ->
                    error("Unable to overload with different amount of arguments!")
                EnterSymbolResult.IdentifierAlreadyDeclared -> error("Identifier already declared!")
            }

            return AstNode.Command.Declaration(expression.type.type(), identifier, expression)
        } else {
            if (type == AstNode.Type.Func.ImplicitFunc || type == AstNode.Type.Var)
                error("Cannot declare implicit type without expression")
            if (type is AstNode.Type.Func)
                error("cannot declare function without body")

            enterSymbol(identifier.name, type).let {
                if(it == EnterSymbolResult.IdentifierAlreadyDeclared)
                    error("Identifier already declared!")
            }

            return AstNode.Command.Declaration(type, identifier, null)
        }
    }

    private fun parseAssignment(): AstNode.Command.Assignment {
        val identifier = acceptIdentifier()
        accept<Token.SpecialChar.Equals>()
        val expression = parseExpression()
        return if (!retrieveSymbol(identifier.name).handle({ true }, { it == expression.type.type() }, { false }))
            unexpectedTypeError(retrieveSymbol(identifier.name).identifier.toString(), expression.type.toString())
        else AstNode.Command.Assignment(identifier, expression)
    }
    private fun parseTypeOrGeneric(){

    }
    //region Type declarations
    private fun  parseType(implicitAllowed: Boolean = false, genericAllowed:Boolean = false): AstNode.Type {
        val currentPosToken = current
        moveNext()
        return when (currentPosToken.token) {
            is Token.Type.Number -> AstNode.Type.Number
            is Token.Type.Text -> AstNode.Type.Text
            is Token.Type.Bool -> AstNode.Type.Bool
            is Token.Type.Var ->
                if (implicitAllowed) AstNode.Type.Var
                else error("Explicit type not allowed here!")
            is Token.Type.Func -> parseFuncType(implicitAllowed)
            is Token.Type.Tuple -> parseTupleType(genericAllowed)
            is Token.Type.List -> parseListType(genericAllowed)
            is Token.Identifier ->
                if(genericAllowed) AstNode.Type.GenericType(currentPosToken.token.value)
                else unexpectedTokenError(currentPosToken.token)
            else -> unexpectedTokenError(currentPosToken.token)
        }
    }

    private fun parseTypes(parsingMethod: () -> AstNode.Type): List<AstNode.Type> {
        val elementTypes = mutableListOf<AstNode.Type>()

        while (true) {
            elementTypes.add(parsingMethod())
            if (!tryAccept<Token.SpecialChar.ListSeparator>()) return elementTypes
        }
    }



    private fun parseFuncType(implicitAllowed: Boolean): AstNode.Type.Func {
        return if (current.token == Token.SpecialChar.SquareBracketStart) {
            accept<Token.SpecialChar.SquareBracketStart>()

            val parameters = parseTypes({
                if (peek().token == Token.SpecialChar.SquareBracketEnd && tryAccept<Token.Type.None>())
                    AstNode.Type.None
                else parseType(genericAllowed = true)
            })
            accept<Token.SpecialChar.SquareBracketEnd>()
            val returnType = parameters.last()


            AstNode.Type.Func.ExplicitFunc(parameters.dropLast(1), returnType)
        }
        else if (implicitAllowed) AstNode.Type.Func.ImplicitFunc else implicitTypeNotAllowedError()
    }

    private fun parseLambdaDeclaration(): AstNode.Command.Expression.LambdaExpression {
        val parameters = parseFunctionParameters()
        accept<Token.SpecialChar.Colon>()
        val returnType = if (current.token == Token.Type.None) {
            moveNext()
            AstNode.Type.None
        } else parseType(genericAllowed = true)

        flushNewLine(false)
        val body = parseLambdaBody()
        return AstNode.Command.Expression.LambdaExpression(parameters, returnType, body)
    }

    private fun parseLambdaBody(): List<AstNode.Command> {
        val commands = mutableListOf<AstNode.Command>()
        accept<Token.SpecialChar.BlockStart>()
        openScope()
        while (current.token != Token.SpecialChar.BlockEnd) {
            commands.add(parseCommand())
        }
        closeScope()
        accept<Token.SpecialChar.BlockEnd>()
        return commands
    }

    private fun parseTupleType(genericAllowed: Boolean): AstNode.Type.Tuple {
        accept<Token.SpecialChar.SquareBracketStart>()
        val elementTypes = parseTypes { parseType(genericAllowed = genericAllowed) }
        accept<Token.SpecialChar.SquareBracketEnd>()
        return AstNode.Type.Tuple(elementTypes)
    }

    private fun parseListType(genericAllowed: Boolean): AstNode.Type.List {
        accept<Token.SpecialChar.SquareBracketStart>()
        val elementType: AstNode.Type = parseType(genericAllowed = genericAllowed)
        accept<Token.SpecialChar.SquareBracketEnd>()
        return AstNode.Type.List(elementType)
    }
//endregion

    //region ExpressionParsing

    private fun parseExpression() = parsePotentialFunctionCall(parseExpressionAtomic())

    private fun parsePotentialFunctionCall(expression: AstExpression): AstExpression =
            when (current.token) {
                is Token.Identifier -> {
                    val token = accept<Token.Identifier>()
                    val symbol = retrieveSymbol(token.value)
                    if (symbol.isFunctions) {
                        val funcDecls = symbol.functions
                        val secondaryParams = funcDecls.first().paramTypes.drop(1).map { parseExpressionAtomic() }
                        val argTypes =
                                listOf(expression.type.type()) + secondaryParams.map { it.type.type() }
                        val declaration = funcDecls.getTypeDeclaration(argTypes)
                        if (declaration == null) undeclaredError(token.value)
                        else AstNode.Command.Expression.FunctionCall(
                                AstNode.Command.Expression.Value.Identifier(token.value),
                                listOf(expression) + secondaryParams
                        )
                    } else expression
                }
                else -> expression
            }.let { if (expression != it) parsePotentialFunctionCall(it) else expression }


    private fun parseExpressionAtomic(): AstExpression =
            when (current.token) {
                Token.SpecialChar.SquareBracketStart -> parseListDeclaration()
                Token.SpecialChar.ParenthesesStart -> {
                    if (peek().token is Token.Type ||
                            (peek().token is Token.Identifier && hasAhead(2) &&
                                    lookAhead(2).token is Token.Identifier) ||
                            peek().token == Token.SpecialChar.ParenthesesEnd) {
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
                is Token.SpecialChar.Colon -> {
                    accept<Token.SpecialChar.Colon>()
                    val token = accept<Token.Identifier>()

                    retrieveSymbol(token.value).handle(
                            {
                                if(it.any {it.containsGeneric()})
                                    genericPassedFunctionException()
                                AstIdentifier(token.value)

                            },
                            {
                                wrongTokenTypeError("Function", token)
                            },
                            {
                                undeclaredError(token.value)
                            }
                    )

                }
                is Token.Identifier -> {
                    val token = accept<Token.Identifier>()
                    retrieveSymbol(token.value).handle(
                            {
                                if (it.first().paramTypes.isEmpty()) {
                                    AstNode.Command.Expression.FunctionCall(
                                            AstIdentifier(token.value), listOf()
                                    )
                                } else error("Function ${token.value} can not be invoked with 0 arguments")
                            },
                            { AstIdentifier(token.value) },
                            {
                                undeclaredError(token.value)
                            }
                    )
                }
                else -> unexpectedTokenError(current.token)
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
        error("Unclosed parentheses")
    }

    private fun parseTupleExpression() =
            AstNode.Command.Expression.Value.Literal.Tuple(
                    mutableListOf<AstNode.Command.Expression>().apply {
                        accept<Token.SpecialChar.ParenthesesStart>()
                        while (current.token != Token.SpecialChar.ParenthesesEnd) {
                            add(parseExpression())
                            if (!tryAccept<Token.SpecialChar.ListSeparator>()) break
                        }
                        accept<Token.SpecialChar.ParenthesesEnd>()
                    }
            )

    private fun parseListDeclaration() =
            AstNode.Command.Expression.Value.Literal.List(
                    mutableListOf<AstNode.Command.Expression>().apply {
                        accept<Token.SpecialChar.SquareBracketStart>()
                        while (current.token != Token.SpecialChar.SquareBracketEnd) {
                            add(parseExpression())
                            if (first().type != last().type)
                                unexpectedTypeError(first().type.toString(), last().type.toString())
                            if (!tryAccept<Token.SpecialChar.ListSeparator>()) break
                        }
                        accept<Token.SpecialChar.SquareBracketEnd>()
                    }
            )

    //endregion ExpressionParsing

    fun ExprResult.type() = when (this) {
        is ExprResult.Success -> this.type
        ExprResult.NoEmptyOverloading ,
        ExprResult.UndeclaredIdentifier,
        ExprResult.NoFuncDeclarationForArgs -> undeclaredError((current.token as Token.Identifier).value)
    }


    fun AstNode.Type.containsGeneric() : Boolean = when(this) {
        is AstNode.Type.List -> this.elementType.containsGeneric()
        is AstNode.Type.Tuple -> this.elementTypes.any {it.containsGeneric()}
        is AstNode.Type.Func.ExplicitFunc -> this.paramTypes.any {it.containsGeneric()} || this.returnType.containsGeneric()
        is AstNode.Type.GenericType -> true
        else -> false
    }
}
