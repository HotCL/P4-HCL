package parser

import lexer.ILexer
import lexer.PositionalToken
import lexer.Token
import utils.BufferedLaabStream

class Parser: IParser {
    override fun generateAbstractSyntaxTree(lexer: ILexer) = AbstractSyntaxTree().apply {
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
        moveNext()
        if (token is T) {
            return token
        } else {
            throw Exception("Make this an expected token type T1 but found token type T2")
        }
    }

    private fun BufferedLaabStream<PositionalToken>.acceptIdentifier() =
            TreeNode.Command.Expression.Value.Identifier(accept<Token.Identifier>().value)

    private fun BufferedLaabStream<PositionalToken>.acceptLiteral(): TreeNode.Command.Expression.Value.Literal {
        val litToken = accept<Token.Literal>()
        return when (litToken) {
            is Token.Literal.Number -> TreeNode.Command.Expression.Value.Literal.Number(litToken.value)
            else -> throw Exception("Make this an expected literal but found token T instead error")
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

    private fun BufferedLaabStream<PositionalToken>.parseTupleType(): TreeNode.Type.Tuple {
        val elementTypes = mutableListOf<TreeNode.Type>()
        if (moveNext().token is Token.SpecialChar.SquareBracketStart) {
            moveNext()
            while (true) {
                if (current.token is Token.Type && current.token !is Token.Type.None) elementTypes.add(parseType())
                else throw Exception("Make this an expected token type T1 but found token type T2")
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

    private fun BufferedLaabStream<PositionalToken>.parseExpression(): TreeNode.Command.Expression {
        // Be aware that below is not correct for the full implementation. Here we expect that if there is only one token
        // the token will be a literal, but it could also be an identifier.
        if (peek().token is Token.SpecialChar.EndOfLine) return acceptLiteral()
        TODO("Make this parse function valid...")
    }
}