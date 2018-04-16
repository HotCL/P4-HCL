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
        flushNewLine(false)
        val command = when (current.token) {
            is Token.Type -> parseDeclaration()
            is Token.Identifier -> {
                if (peek().token == Token.SpecialChar.Equals) {
                    parseAssignment()
                }
                else parseExpression()
            }
            Token.SpecialChar.BlockStart -> parsePotentialFunctionCall(null)
            is Token.Literal, //Fallthrough
            Token.SpecialChar.SquareBracketStart,
            Token.SpecialChar.Colon,
            Token.SpecialChar.ParenthesesStart
                -> parseExpression()
            Token.Return -> parseReturnStatement()
            else -> unexpectedTokenError(current.token)
        }
        flushNewLine(current.token !in listOf(Token.SpecialChar.BlockEnd))
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
        val type = parseType()
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
        val expression = if (tryAccept<Token.SpecialChar.Equals>()) {
            parseExpression()
        } else null

        if (expression == null && (type == AstNode.Type.Func.ImplicitFunc || type == AstNode.Type.Var))
            error("Cannot declare implicit type without expression")
        if (expression != null && (type == AstNode.Type.Func.ImplicitFunc || type == AstNode.Type.Var)) {
            when (enterSymbol(identifier.name, expression.type.type())) {
                EnterSymbolResult.OverloadAlreadyDeclared ->
                    error("Function of same name with these parameters has already been declared!")
                EnterSymbolResult.OverloadDifferentParamNums ->
                    error("Unable to overload with different amount of arguments!")
                EnterSymbolResult.IdentifierAlreadyDeclared ->
                    error("Identifier already declared!")
            }
        }
        else when (enterSymbol(identifier.name, type)) {
            EnterSymbolResult.IdentifierAlreadyDeclared -> error("Identifier already declared!")
        }
        return if (expression != null) {
            if (!retrieveSymbol(identifier.name).handle({ true }, { it == expression.type.type() }, { false }))
                unexpectedTypeError(type.toString(), expression.type.toString())
            else AstNode.Command.Declaration(expression.type.type(), identifier, expression)
        } else AstNode.Command.Declaration(type, identifier, expression)
    }

    private fun parseAssignment(): AstNode.Command.Assignment {
        val identifier = acceptIdentifier()
        accept<Token.SpecialChar.Equals>()
        val expression = parseExpression()
        return if (!retrieveSymbol(identifier.name).handle({ true }, { it == expression.type.type() }, { false }))
            unexpectedTypeError(retrieveSymbol(identifier.name).identifier.toString(), expression.type.toString())
         else AstNode.Command.Assignment(identifier, expression)
    }

    //region Type declarations
    private fun  parseType(implicitAllowed: Boolean = false): AstNode.Type {
        val currentPosToken = current
        moveNext()
        return when (currentPosToken.token) {
            Token.Type.Number -> AstNode.Type.Number
            Token.Type.Text -> AstNode.Type.Text
            Token.Type.Bool -> AstNode.Type.Bool
            Token.Type.Var -> if (implicitAllowed) AstNode.Type.Var
                                 else error("Explicit type not allowed here!")
            Token.Type.Func -> parseFuncType(implicitAllowed)
            Token.Type.Tuple -> parseTupleType()
            Token.Type.List -> parseListType()
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
                else parseType()
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
        } else parseType()

        flushNewLine(false)
        val lambda = parseLambdaBody(parameters)
        if (lambda.type != returnType) error("Error! Declared return type did not match actual return type")
        return AstNode.Command.Expression.LambdaExpression(parameters, returnType, lambda.lambdaBody)
    }

    private fun parseLambdaBody(inputParams: List<AstNode.ParameterDeclaration>): LambdaBodyWithType {
        val commands = mutableListOf<AstNode.Command>()
        flushNewLine(false)
        accept<Token.SpecialChar.BlockStart>()
        openScope()
        inputParams.forEach { enterSymbol(it.identifier.name, it.type) }
        while (current.token != Token.SpecialChar.BlockEnd) {
            commands.add(parseCommand())
        }
        val body = AstNode.Command.Expression.LambdaBody(
                if (commands.size == 1 && commands[0] is AstNode.Command.Expression)
                    listOf(AstNode.Command.Return(commands[0] as AstNode.Command.Expression))
                else
                    commands
        )
        val ret = LambdaBodyWithType(body, body.type.type())
        closeScope()
        flushNewLine(false)
        accept<Token.SpecialChar.BlockEnd>()
        return ret
    }

    private fun parseTupleType(): AstNode.Type.Tuple {
        accept<Token.SpecialChar.SquareBracketStart>()
        val elementTypes = parseTypes { parseType() }
        accept<Token.SpecialChar.SquareBracketEnd>()
        return AstNode.Type.Tuple(elementTypes)
    }

    private fun parseListType(): AstNode.Type.List {
        accept<Token.SpecialChar.SquareBracketStart>()
        val elementType: AstNode.Type = parseType()
        accept<Token.SpecialChar.SquareBracketEnd>()
        return AstNode.Type.List(elementType)
    }
//endregion

    //region ExpressionParsing

    private fun parseExpression() = parsePotentialFunctionCall(if (current.token == Token.SpecialChar.BlockStart) null
                                                               else parseExpressionAtomic())

    private fun getUpcomingIdentifierName(): String {
        var scopeDepth = 0
        var ahead = 0
        while (true) {
            if (hasAhead(ahead)) {
                val token = lookAhead(ahead++).token
                when (token) {
                    Token.SpecialChar.BlockStart -> scopeDepth += 1
                    Token.SpecialChar.BlockEnd -> scopeDepth -= 1
                    is Token.Identifier -> if (scopeDepth == 0) return token.value
                }
            } else error("Expected to find identifier, but did not!")
        }
    }

    private fun getLambdaParameter(func: List<AstNode.Type.Func.ExplicitFunc>, index: Int): AstExpression {
        val funcAcceptingLambda = func.firstOrNull{
            it.paramTypes[index] is AstNode.Type.Func.ExplicitFunc
        } ?: error("No function found that takes a lambda at the current position")
        val lambdaFuncType = funcAcceptingLambda.paramTypes[index] as AstNode.Type.Func.ExplicitFunc
        val lambdaParams = when (lambdaFuncType.paramTypes.size) {
            0 -> emptyList()
            1 -> listOf(AstNode.ParameterDeclaration(lambdaFuncType.paramTypes[0],
                    AstNode.Command.Expression.Value.Identifier("value")))
            else -> {
                lambdaFuncType.paramTypes.mapIndexed { idx, type ->
                    AstNode.ParameterDeclaration(type,
                            AstNode.Command.Expression.Value.Identifier(
                                    "value${if (idx != 0) "${idx + 1}" else ""}"
                            )
                    )
                }
            }
        }
        val lambdaBody = parseLambdaBody(lambdaParams)
        return AstNode.Command.Expression.LambdaExpression(
                lambdaParams,
                lambdaBody.type,
                lambdaBody.lambdaBody)
    }

    private fun parsePotentialFunctionCall(expression: AstExpression? = null): AstExpression =
        when (current.token) {
            is Token.SpecialChar.BlockStart -> {
                val symbol = retrieveSymbol(getUpcomingIdentifierName())
                if (!symbol.isFunctions) error("Identifier has to be function")
                val lambdaParameter = getLambdaParameter(symbol.functions, 0)
                parsePotentialFunctionCall(lambdaParameter)
            }
            is Token.Identifier -> {
                val token = accept<Token.Identifier>()
                val symbol = retrieveSymbol(token.value)
                if (symbol.isFunctions) {
                    val funcDecls = symbol.functions
                    val secondaryArguments = funcDecls.first().paramTypes.drop(1).mapIndexed { index, _ ->
                        if (current.token != Token.SpecialChar.BlockStart) parseExpressionAtomic() else {
                            getLambdaParameter(funcDecls, index + 1)
                        }
                    }
                    val argTypes = listOf(expression!!.type.type()) + secondaryArguments.map { it.type.type() }
                    val declaration = funcDecls.getTypeDeclaration(argTypes)
                    if (declaration == null) undeclaredError(token.value)
                    else AstNode.Command.Expression.FunctionCall(
                            AstNode.Command.Expression.Value.Identifier(token.value),
                            listOf(expression) + secondaryArguments
                    )
                } else expression!!
            }
            else -> expression!!
        }.let { if (expression != it) parsePotentialFunctionCall(it) else expression }


    private fun parseExpressionAtomic(): AstExpression =
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
            Token.SpecialChar.BlockStart -> error("Unable to evaluate lambda body as expression!")
            is Token.Literal.Text,
            is Token.Literal.Bool,
            is Token.Literal.Number -> acceptLiteral()
            is Token.SpecialChar.Colon -> {
                accept<Token.SpecialChar.Colon>()
                val token = accept<Token.Identifier>()
                retrieveSymbol(token.value).handle(
                        { AstIdentifier(token.value) },
                        { wrongTokenTypeError("Function", token) },
                        { undeclaredError(token.value) }
                )
            }
            is Token.Identifier -> {
                val token = accept<Token.Identifier>()
                retrieveSymbol(token.value).handle(
                        {
                            if (it.first().paramTypes.isEmpty()) {
                                AstNode.Command.Expression.FunctionCall(AstIdentifier(token.value), listOf())
                            } else error("Function ${token.value} can not be invoked with 0 arguments")
                        },
                        { AstIdentifier(token.value) },
                        { undeclaredError(token.value) }
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
        ExprResult.NoFuncDeclarationForArgs -> undeclaredError((current.token as? Token.Identifier)?.value ?: "")
        ExprResult.BodyWithMultiReturnTypes -> error("Lambda body with multiple return types")
    }
}
