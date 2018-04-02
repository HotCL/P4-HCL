package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import lexer.ILexer
import lexer.PositionalToken
import lexer.Token
import org.junit.jupiter.api.Assertions.assertTrue
import parser.Parser
import parser.TreeNode
import kotlin.coroutines.experimental.buildSequence

class ParserTests {
    @org.junit.jupiter.api.Test
    fun testParser() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Number())
            yield(Token.Identifier("myId"))
            yield(Token.SpecialChar.Equals())
            yield(Token.Literal.Number("5"))
            yield(Token.SpecialChar.EndOfLine())
        })
        val ast = Parser().generateAbstractSyntaxTree(lexer)
        assertThat(ast.children.size, equalTo(1))
        assertTrue(ast.children[0] is TreeNode.Command.Declaration)
        val declaration = ast.children[0] as TreeNode.Command.Declaration
        assertTrue(declaration.type is TreeNode.Type.Number)
        assertThat(declaration.identifier, equalTo(TreeNode.Command.Expression.Value.Identifier("myId")))
        assertThat(declaration.expression as TreeNode.Command.Expression.Value.Literal.Number, equalTo(TreeNode.Command.Expression.Value.Literal.Number(5.0)))
    }

    class DummyLexer(val tokens: Sequence<Token>): ILexer {
        override fun getTokenSequence() = buildSequence {
            tokens.forEach { yield(PositionalToken(it, -1, -1)) }
        }

        override fun inputLine(lineNumber: Int) = "Dummy lexer does not implement input line"
    }
}