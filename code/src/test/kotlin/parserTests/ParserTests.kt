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

    //region FuncTypeDcl
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
    fun testParserFuncTypeNoTypes() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.EndOfLine())
        })
        //val ast = Parser().generateAbstractSyntaxTree(lexer)
        val exception = assertThrows(Exception::class.java) { Parser().generateAbstractSyntaxTree(lexer) }
        assertEquals("Make this an expected token type T1 but found token type T2", exception.message)
    }

    @org.junit.jupiter.api.Test
    fun testParserFuncTypeOneType() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func())
            yield(Token.SpecialChar.SquareBracketStart())
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
        assertThat((declaration.type as TreeNode.Type.Func).paramTypes.size, equalTo(0))
        assertTrue((declaration.type as TreeNode.Type.Func).returnType is TreeNode.Type.Text)
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
    //endregion FuncTypeDcl

    //region TupleTypeDcl
    @org.junit.jupiter.api.Test
    fun testParserTupleType() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Tuple())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Type.Text())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myTuple"))
            yield(Token.SpecialChar.EndOfLine())
        })
        val ast = Parser().generateAbstractSyntaxTree(lexer)
        assertThat(ast.children.size, equalTo(1))
        assertTrue(ast.children[0] is TreeNode.Command.Declaration)
        val declaration = ast.children[0] as TreeNode.Command.Declaration
        assertTrue(declaration.type is TreeNode.Type.Tuple)
        assertThat(declaration.identifier, equalTo(TreeNode.Command.Expression.Value.Identifier("myTuple")))
        assertThat((declaration.type as TreeNode.Type.Tuple).elementTypes.size, equalTo(2))
        assertTrue((declaration.type as TreeNode.Type.Tuple).elementTypes[0] is TreeNode.Type.Number)
        assertTrue((declaration.type as TreeNode.Type.Tuple).elementTypes[1] is TreeNode.Type.Text)
    }

    @org.junit.jupiter.api.Test
    fun testParserTupleDeclarationSHOULDFAIL() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Tuple())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Type.Text())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myTuple"))
            yield(Token.SpecialChar.Equals())
            yield(Token.SpecialChar.ParenthesesStart())
            yield(Token.Literal.Number("5.0"))
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Literal.Text("hej"))
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Literal.Bool(true))
            yield(Token.SpecialChar.ParenthesesEnd())
            yield(Token.SpecialChar.EndOfLine())
        })
        val ast = Parser().generateAbstractSyntaxTree(lexer)
        assertThat(ast.children.size, equalTo(1))
        assertTrue(ast.children[0] is TreeNode.Command.Declaration)
        val declaration = ast.children[0] as TreeNode.Command.Declaration
        assertTrue(declaration.type is TreeNode.Type.Tuple)
        assertThat(declaration.identifier, equalTo(TreeNode.Command.Expression.Value.Identifier("myTuple")))
        assertThat((declaration.type as TreeNode.Type.Tuple).elementTypes.size, equalTo(2))
        assertTrue((declaration.type as TreeNode.Type.Tuple).elementTypes[0] is TreeNode.Type.Number)
        assertTrue((declaration.type as TreeNode.Type.Tuple).elementTypes[1] is TreeNode.Type.Text)
        assertThat(declaration.expression as TreeNode.Command.Expression.Value.Literal.Tuple, equalTo(TreeNode.Command.Expression.Value.Literal.Tuple(
                listOf(TreeNode.Command.Expression.Value.Literal.Number(5.0), TreeNode.Command.Expression.Value.Literal.Text("hej"),
                        TreeNode.Command.Expression.Value.Literal.Bool(true)))))
    }

    @org.junit.jupiter.api.Test
    fun testParserTupleDeclaration() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Tuple())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Type.Text())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myTuple"))
            yield(Token.SpecialChar.Equals())
            yield(Token.SpecialChar.ParenthesesStart())
            yield(Token.Literal.Number("5.0"))
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Literal.Text("someText"))
            yield(Token.SpecialChar.ParenthesesEnd())
            yield(Token.SpecialChar.EndOfLine())
        })
        val ast = Parser().generateAbstractSyntaxTree(lexer)
        assertThat(ast.children.size, equalTo(1))
        assertTrue(ast.children[0] is TreeNode.Command.Declaration)
        val declaration = ast.children[0] as TreeNode.Command.Declaration
        assertTrue(declaration.type is TreeNode.Type.Tuple)
        assertThat(declaration.identifier, equalTo(TreeNode.Command.Expression.Value.Identifier("myTuple")))
        assertThat((declaration.type as TreeNode.Type.Tuple).elementTypes.size, equalTo(2))
        assertTrue((declaration.type as TreeNode.Type.Tuple).elementTypes[0] is TreeNode.Type.Number)
        assertTrue((declaration.type as TreeNode.Type.Tuple).elementTypes[1] is TreeNode.Type.Text)
        //val expression = declaration.expression as TreeNode.Command.Expression.Value.Literal.Tuple
        assertThat(declaration.expression as TreeNode.Command.Expression.Value.Literal.Tuple, equalTo(TreeNode.Command.Expression.Value.Literal.Tuple(
                listOf(TreeNode.Command.Expression.Value.Literal.Number(5.0), TreeNode.Command.Expression.Value.Literal.Text("someText")))))
    }

    @org.junit.jupiter.api.Test
    fun testParserTupleDeclarationNotEnoughSeparators() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Tuple())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Type.Text())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myTuple"))
            yield(Token.SpecialChar.Equals())
            yield(Token.SpecialChar.ParenthesesStart())
            yield(Token.Literal.Number("5.0"))
            yield(Token.Literal.Text("someText"))
            yield(Token.SpecialChar.ParenthesesEnd())
            yield(Token.SpecialChar.EndOfLine())
        })
        val exception = assertThrows(Exception::class.java) { Parser().generateAbstractSyntaxTree(lexer) }
        assertEquals("Make this an expected token type T1 but found token type T2", exception.message)
    }

    @org.junit.jupiter.api.Test
    fun testParserTupleDeclarationTooManySeparators() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Tuple())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Type.Text())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myTuple"))
            yield(Token.SpecialChar.Equals())
            yield(Token.SpecialChar.ParenthesesStart())
            yield(Token.Literal.Number("5.0"))
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Literal.Text("someText"))
            yield(Token.SpecialChar.ParenthesesEnd())
            yield(Token.SpecialChar.EndOfLine())
        })
        val exception = assertThrows(Exception::class.java) { Parser().generateAbstractSyntaxTree(lexer) }
        assertEquals("Make this an expected token type T1 but found token type T2", exception.message)
    }
    //endregion TupleTypeDcl

    //region ListTypeDcl
    @org.junit.jupiter.api.Test
    fun testParserListType() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.List())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myList"))
            yield(Token.SpecialChar.EndOfLine())
        })
        val ast = Parser().generateAbstractSyntaxTree(lexer)
        assertThat(ast.children.size, equalTo(1))
        assertTrue(ast.children[0] is TreeNode.Command.Declaration)
        val declaration = ast.children[0] as TreeNode.Command.Declaration
        assertTrue(declaration.type is TreeNode.Type.List)
        assertThat(declaration.identifier, equalTo(TreeNode.Command.Expression.Value.Identifier("myList")))
        assertTrue((declaration.type as TreeNode.Type.List).elementType is TreeNode.Type.Number)
    }
    //endregion ListTypeDcl

    class DummyLexer(val tokens: Sequence<Token>): ILexer {
        override fun getTokenSequence() = buildSequence {
            tokens.forEach { yield(PositionalToken(it, -1, -1)) }
        }

        override fun inputLine(lineNumber: Int) = "Dummy lexer does not implement input line"
    }
}