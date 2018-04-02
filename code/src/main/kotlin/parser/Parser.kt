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
                    is Token.Type -> parseDeclaration(this)
                    is Token.SpecialChar.EndOfLine -> null
                    else -> throw Exception("Make this a unexpected token exception once that is implemented...")
                }.let { if (it != null) this@apply.children.add(it) }
            }
        }
    }

    private inline fun<reified T> accept(stream: BufferedLaabStream<PositionalToken>): T {
        val token = stream.current.token
        stream.moveNext()
        if (token is T) {
            return token
        } else {
            throw Exception("Make this an expected token type T1 but found token type T2")
        }
    }

    private fun acceptIdentifier(stream: BufferedLaabStream<PositionalToken>) =
            TreeNode.Command.Expression.Value.Identifier(accept<Token.Identifier>(stream).value)

    private fun acceptLiteral(stream: BufferedLaabStream<PositionalToken>): TreeNode.Command.Expression.Value.Literal {
        val litToken = accept<Token.Literal>(stream)
        return when (litToken) {
            is Token.Literal.Number -> TreeNode.Command.Expression.Value.Literal.Number(litToken.value)
            else -> throw Exception("Make this an expected literal but found token T instead error")
        }
    }

    private fun parseDeclaration(stream: BufferedLaabStream<PositionalToken>): TreeNode.Command {
        val type = parseType(stream)
        val identifier = acceptIdentifier(stream)
        val expression = if (stream.current.token is Token.SpecialChar.Equals) {
            stream.moveNext()
            parseExpression(stream)
        } else null
        return TreeNode.Command.Declaration(type, identifier, expression)
    }

    private fun parseType(stream: BufferedLaabStream<PositionalToken>) = when (stream.current.token) {
        is Token.Type.Number -> TreeNode.Type.Number()
        else -> throw Exception("Make this an expected type but found token type T")
    }.also { stream.moveNext() }

    private fun parseExpression(stream: BufferedLaabStream<PositionalToken>): TreeNode.Command.Expression {
        // Be aware that below is not correct for the full implementation. Here we expect that if there is only one token
        // the token will be a literal, but it could also be an identifier.
        if (stream.peek().token is Token.SpecialChar.EndOfLine) return acceptLiteral(stream)
        TODO("Make this parse function valid...")
    }
}