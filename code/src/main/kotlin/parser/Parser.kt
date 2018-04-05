package parser

import exceptions.WrongTokenTypeError
import lexer.ILexer
import lexer.PositionalToken
import lexer.Token
import utils.BufferedLaabStream

class Parser(val lexer: ILexer): IParser {
    override fun generateAbstractSyntaxTree() = AbstractSyntaxTree().apply {
        with(BufferedLaabStream(lexer.getTokenSequence())) {
            while (hasNext()) {
                val token = current.token
                when (token) {
                    is Token.Type -> parseDeclaration()
                    is Token.SpecialChar.EndOfLine -> null
                    else -> throw Exception("Make this a unexpected token exception once that is implemented...")
                }.let { if (it != null) this@apply.children.add(it) }
            }
        }
    }
    private inline fun<reified T> BufferedLaabStream<PositionalToken>.accept(): T {
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

    private fun BufferedLaabStream<PositionalToken>.acceptIdentifier() =
            TreeNode.Command.Expression.Value.Identifier(accept<Token.Identifier>().value)

    private fun BufferedLaabStream<PositionalToken>.acceptLiteral(): TreeNode.Command.Expression.Value.Literal {
        val litToken = accept<Token.Literal>()
        return when (litToken) {
            is Token.Literal.Number -> TreeNode.Command.Expression.Value.Literal.Number(litToken.value)
            is Token.Literal.Text -> TreeNode.Command.Expression.Value.Literal.Text(litToken.value)
            is Token.Literal.Bool -> TreeNode.Command.Expression.Value.Literal.Bool(litToken.value)
        }
    }

    private fun BufferedLaabStream<PositionalToken>.parseDeclaration(): TreeNode.Command {
        val type = parseType()
        val identifier = acceptIdentifier()
        val expression = if (current.token is Token.SpecialChar.Equals) {
            moveNext()
            parseExpression()
        } else null
        return TreeNode.Command.Declaration(type, identifier, expression)
    }
//region Type declarations
    private fun BufferedLaabStream<PositionalToken>.parseType() = when (current.token) {
        is Token.Type.Number -> TreeNode.Type.Number()
        is Token.Type.Text -> TreeNode.Type.Text()
        is Token.Type.Bool -> TreeNode.Type.Bool()
        is Token.Type.None -> TreeNode.Type.None()
        is Token.Type.Func -> parseFuncType()
        is Token.Type.Tuple -> parseTupleType()
        is Token.Type.List -> parseListType()
        else -> throw Exception("Make this an expected type but found token type T")
    }.also { moveNext() }

    private fun BufferedLaabStream<PositionalToken>.parseFuncType(): TreeNode.Type.Func {
        val parameters = mutableListOf<TreeNode.Type>()
        if (moveNext().token is Token.SpecialChar.SquareBracketStart) {
            moveNext()
            while (true) {
                if (current.token is Token.Type) {
                    if (current.token is Token.Type.None && peek().token !is Token.SpecialChar.SquareBracketEnd)
                        throw Exception("Function parameters can't be of type None")
                    else parameters.add(parseType())
                }
                else throw Exception("Make this an expected token type T1 but found token type T2")
                if (current.token is Token.SpecialChar.ListSeparator) moveNext()
                else break
            }
            if (current.token !is Token.SpecialChar.SquareBracketEnd)
                throw Exception("Make this an expected token type T1 but found token type T2")
        }
        else throw NotImplementedError()
        val returnType = parameters.last()
        return TreeNode.Type.Func(parameters.dropLast(1), returnType)
    }

    private fun BufferedLaabStream<PositionalToken>.parseLambdaExpression():
                                                                TreeNode.Command.Expression.LambdaExpression {
        val parameters = mutableListOf<TreeNode.Command.Declaration>()
        val returnType: TreeNode.Type
        val body: List<TreeNode.Command>
        if (current.token is Token.SpecialChar.ParenthesesStart) {
            if (peek().token !is Token.SpecialChar.ParenthesesEnd) {
                while (true) {
                    if (moveNext().token is Token.Type) {
                        if (current.token is Token.Type.None) throw Exception("Function parameters can't be of type None")
                        parameters.add(parseDeclaration() as TreeNode.Command.Declaration)
                    } else throw Exception("Expected type token")
                    if (current.token !is Token.SpecialChar.ListSeparator) break
                }
            }
            else moveNext()
            if (current.token !is Token.SpecialChar.ParenthesesEnd)
                throw Exception("Expected parenthesis end")
            moveNext()
            accept<Token.SpecialChar.Colon>()
            returnType = parseType()
            body = parseLambdaBody()
            return TreeNode.Command.Expression.LambdaExpression(parameters, returnType, body)
        }
        else throw Exception("Make this an expected token type T1 but found token type T2")
    }

    private fun BufferedLaabStream<PositionalToken>.parseLambdaBody(): List<TreeNode.Command> {
        while (current.token !is Token.SpecialChar.BlockEnd)
            accept<Token>()
        moveNext()
        return listOf()
    }

    private fun BufferedLaabStream<PositionalToken>.parseTupleType(): TreeNode.Type.Tuple {
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
                throw Exception("Make this an expected token type T1 but found token type T2")
        }
        else throw NotImplementedError()
        return TreeNode.Type.Tuple(elementTypes)
    }

    private fun BufferedLaabStream<PositionalToken>.parseListType(): TreeNode.Type.List {
        val elementType: TreeNode.Type
        if (moveNext().token is Token.SpecialChar.SquareBracketStart) {
            moveNext()
            if (current.token is Token.Type && current.token !is Token.Type.None) elementType = parseType()
            else throw Exception("Make this an expected token type T1 but found token type T2")
            if (current.token !is Token.SpecialChar.SquareBracketEnd)
                throw Exception("Make this an expected token type T1 but found token type T2")
        }
        else throw NotImplementedError()
        return TreeNode.Type.List(elementType)
    }
//endregion
//region ExpressionParsing
    private fun BufferedLaabStream<PositionalToken>.parseExpression(): TreeNode.Command.Expression {
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
    private fun BufferedLaabStream<PositionalToken>.parseTupleExpression(): TreeNode.Command.Expression{
        val elements = mutableListOf<TreeNode.Command.Expression>()
        moveNext()
        while (true){
            if (current.token is Token.Literal) elements.add(acceptLiteral())
            else if (current.token is Token.Identifier) elements.add(acceptIdentifier())
            else throw Exception("Make this an expected token type T1 but found token type T2")
            if (current.token is Token.SpecialChar.ListSeparator) moveNext()
                else break
        }
        if (current.token !is Token.SpecialChar.ParenthesesEnd)
            throw Exception("Make this an expected token type T1 but found token type T2")
        moveNext()
        return TreeNode.Command.Expression.Value.Literal.Tuple(elements)
    }

    private fun BufferedLaabStream<PositionalToken>.parseListDeclaration(): TreeNode.Command.Expression{
        val elements = mutableListOf<TreeNode.Command.Expression>()
        moveNext()
        while (true) {
            if (current.token is Token.Literal) elements.add(acceptLiteral())
            else if (current.token is Token.Identifier) elements.add(acceptIdentifier())
            else throw Exception("Make this an expected token type T1 but found token type T2")
            if (current.token is Token.SpecialChar.ListSeparator) moveNext()
                else break
        }
        if (current.token !is Token.SpecialChar.SquareBracketEnd)
            throw Exception("Make this an expected token type T1 but found token type T2")
        moveNext()
        return TreeNode.Command.Expression.Value.Literal.List(elements)
    }
    //endregion ExpressionParsing
}