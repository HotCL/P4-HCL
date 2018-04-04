package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import lexer.ILexer
import lexer.PositionalToken
import lexer.Token
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertThrows
import parser.Parser
import parser.TreeNode
import kotlin.coroutines.experimental.buildSequence
import kotlin.test.assertEquals

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

    @org.junit.jupiter.api.Test
    fun testParserFuncType() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Type.Text())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.EndOfLine())
        })
        val ast = Parser().generateAbstractSyntaxTree(lexer)
        assertThat(ast.children.size, equalTo(1))
        assertTrue(ast.children[0] is TreeNode.Command.Declaration)
        val declaration = ast.children[0] as TreeNode.Command.Declaration
        assertTrue(declaration.type is TreeNode.Type.Func)
        assertThat(declaration.identifier, equalTo(TreeNode.Command.Expression.Value.Identifier("myFunc")))
        assertThat((declaration.type as TreeNode.Type.Func).paramTypes.size, equalTo(1))
        assertTrue((declaration.type as TreeNode.Type.Func).paramTypes[0] is TreeNode.Type.Number)
        assertTrue((declaration.type as TreeNode.Type.Func).returnType is TreeNode.Type.Text)
    }

    @org.junit.jupiter.api.Test
    fun testParserFuncTypeNotEnoughSeparators() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.Type.Text())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.EndOfLine())
        })
        //val ast = Parser().generateAbstractSyntaxTree(lexer)
        val exception = assertThrows(Exception::class.java) { Parser().generateAbstractSyntaxTree(lexer) }
        assertEquals("Make this an expected token type T1 but found token type T2", exception.message)
    }

    @org.junit.jupiter.api.Test
    fun testParserFuncTypeImplicit() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func())
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.EndOfLine())
        })
        assertThrows(NotImplementedError::class.java) { Parser().generateAbstractSyntaxTree(lexer) }
    }

    @org.junit.jupiter.api.Test
    fun testParserFuncTypeTooManySeparators() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Type.Text())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.EndOfLine())
        })
        //val ast = Parser().generateAbstractSyntaxTree(lexer)
        val exception = assertThrows(Exception::class.java) { Parser().generateAbstractSyntaxTree(lexer) }
        assertEquals("Make this an expected token type T1 but found token type T2", exception.message)
    }

    class DummyLexer(val tokens: Sequence<Token>): ILexer {
        override fun getTokenSequence() = buildSequence {
            tokens.forEach { yield(PositionalToken(it, -1, -1)) }
        }

        override fun inputLine(lineNumber: Int) = "Dummy lexer does not implement input line"
    }
}