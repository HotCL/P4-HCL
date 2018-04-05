package parserTests

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import lexer.ILexer
import lexer.PositionalToken
import lexer.Token
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertThrows
import parser.AbstractSyntaxTree
import parser.Parser
import parser.TreeNode
import sun.reflect.generics.tree.Tree
import kotlin.coroutines.experimental.buildSequence
import kotlin.test.assertEquals

class ParserTests {
    private fun matchesAstChildren(vararg expectedAstChildren: TreeNode.Command): Matcher<List<Token>> =
        object : Matcher<List<Token>> {
            override fun invoke(actual: List<Token>): MatchResult {
                val actualAst = Parser(DummyLexer(buildSequence{ yieldAll(actual) })).generateAbstractSyntaxTree()
                val expectedAst = AbstractSyntaxTree(expectedAstChildren.toMutableList())
                return if (actualAst.toString() == expectedAst.toString()) MatchResult.Match
                else MatchResult.Mismatch("Expected AST equal to this:\n${expectedAst}\nBut got this:\n${actualAst}")
            }
            override val description: String get() = "was equal to the expected AST"
            override val negatedDescription: String get() = "was not equal to the expected AST"
        }

    @org.junit.jupiter.api.Test
    fun testParser() {
        assertThat(
            listOf(
                Token.Type.Number(),
                Token.Identifier("myId"),
                Token.SpecialChar.Equals(),
                Token.Literal.Number("5"),
                Token.SpecialChar.EndOfLine()
            ),
            matchesAstChildren(
                TreeNode.Command.Declaration(
                    TreeNode.Type.Number(),
                    TreeNode.Command.Expression.Value.Identifier("myId"),
                    TreeNode.Command.Expression.Value.Literal.Number(5.0)
                )
            )
        )
    }

    //region FuncTypeDcl
    @org.junit.jupiter.api.Test
    fun testParserFuncType() {
        assertThat(
                listOf(
                        Token.Type.Func(),
                        Token.SpecialChar.SquareBracketStart(),
                        Token.Type.Number(),
                        Token.SpecialChar.ListSeparator(),
                        Token.Type.Text(),
                        Token.SpecialChar.SquareBracketEnd(),
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.EndOfLine()
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Func(listOf(TreeNode.Type.Number()),TreeNode.Type.Text()),
                                TreeNode.Command.Expression.Value.Identifier("myFunc")
                        )
                )
        )
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
        val exception = assertThrows(Exception::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
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
        val exception = assertThrows(Exception::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
        assertEquals("Make this an expected token type T1 but found token type T2", exception.message)
    }

    @org.junit.jupiter.api.Test
    fun testParserFuncTypeOneType() {
        assertThat(
                listOf(
                        Token.Type.Func(),
                        Token.SpecialChar.SquareBracketStart(),
                        Token.Type.Text(),
                        Token.SpecialChar.SquareBracketEnd(),
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.EndOfLine()
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(TreeNode.Type.Func(listOf(), TreeNode.Type.Text()),
                                TreeNode.Command.Expression.Value.Identifier("myFunc")
                                )

                )
        )
    }

    @org.junit.jupiter.api.Test
    fun testParserFuncTypeImplicit() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func())
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.EndOfLine())
        })
        assertThrows(NotImplementedError::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
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
        val exception = assertThrows(Exception::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
        assertEquals("Make this an expected token type T1 but found token type T2", exception.message)
    }
    //endregion FuncTypeDcl

    //region LambdaExpression
    @org.junit.jupiter.api.Test
    fun testParserFuncDclAndAssignmentTwoParameters() {
        assertThat(
                listOf(
                        Token.Type.Func(),
                                Token.SpecialChar.SquareBracketStart(),
                                Token.Type.Number(),
                                Token.SpecialChar.ListSeparator(),
                                Token.Type.Text(),
                                Token.SpecialChar.ListSeparator(),
                                Token.Type.None(),
                                Token.SpecialChar.SquareBracketEnd(),
                                Token.Identifier("myFunc"),
                                Token.SpecialChar.Equals(),
                                Token.SpecialChar.ParenthesesStart(),
                                Token.Type.Number(),
                                Token.Identifier("myParam1"),
                                Token.SpecialChar.ListSeparator(),
                                Token.Type.Text(),
                                Token.Identifier("myParam2"),
                                Token.SpecialChar.ParenthesesEnd(),
                                Token.SpecialChar.Colon(),
                                Token.Type.None(),
                                Token.SpecialChar.BlockStart(),
                                Token.SpecialChar.BlockEnd(),
                                Token.SpecialChar.EndOfLine()
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(TreeNode.Type.Func(
                                listOf(TreeNode.Type.Number(), TreeNode.Type.Text()),
                                    TreeNode.Type.None()
                                ),
                                TreeNode.Command.Expression.Value.Identifier("myFunc"),
                                TreeNode.Command.Expression.LambdaExpression(
                                        listOf(
                                                TreeNode.Command.Declaration(
                                                        TreeNode.Type.Number(),
                                                        TreeNode.Command.Expression.Value.Identifier("MyParam1")),
                                                TreeNode.Command.Declaration(
                                                        TreeNode.Type.Text(),
                                                        TreeNode.Command.Expression.Value.Identifier("MyParam2")
                                                )),
                                        TreeNode.Type.None(),
                                        listOf<TreeNode.Command.Expression.LambdaExpression>()
                                )

                        )

                )
        )
    }

    @org.junit.jupiter.api.Test
    fun testParserFuncDclAndAssignmentOneParameter() {
        assertThat(
                listOf(
                        Token.Type.Func(),
                        Token.SpecialChar.SquareBracketStart(),
                        Token.Type.Number(),
                        Token.SpecialChar.ListSeparator(),
                        Token.Type.None(),
                        Token.SpecialChar.SquareBracketEnd(),
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals(),
                        Token.SpecialChar.ParenthesesStart(),
                        Token.Type.Number(),
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.ParenthesesEnd(),
                        Token.SpecialChar.Colon(),
                        Token.Type.None(),
                        Token.SpecialChar.BlockStart(),
                        Token.SpecialChar.BlockEnd(),
                        Token.SpecialChar.EndOfLine()
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(TreeNode.Type.Func(
                                listOf(TreeNode.Type.Number()), //InputTypes
                                    TreeNode.Type.None() //ReturnType
                        ),
                                TreeNode.Command.Expression.Value.Identifier("myFunc"),
                                TreeNode.Command.Expression.LambdaExpression(
                                        listOf(
                                                TreeNode.Command.Declaration(
                                                        TreeNode.Type.Number(),
                                                        TreeNode.Command.Expression.Value.Identifier("myParam1")
                                                    )
                                                ),
                                        TreeNode.Type.None(),
                                        listOf<TreeNode.Command.Expression.LambdaExpression>()
                                )

                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun testParserFuncDclAndAssignmentZeroParameters() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.None())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.Equals())
            yield(Token.SpecialChar.ParenthesesStart())
            yield(Token.SpecialChar.ParenthesesEnd())
            yield(Token.SpecialChar.Colon())
            yield(Token.Type.None())
            yield(Token.SpecialChar.BlockStart())
            yield(Token.SpecialChar.BlockEnd())
            yield(Token.SpecialChar.EndOfLine())
        })
        val ast = Parser(lexer).generateAbstractSyntaxTree()
        assertThat(ast.children.size, equalTo(1))
        assertTrue(ast.children[0] is TreeNode.Command.Declaration)

        val declaration = ast.children[0] as TreeNode.Command.Declaration
        assertTrue(declaration.type is TreeNode.Type.Func)
        assertThat(declaration.identifier, equalTo(TreeNode.Command.Expression.Value.Identifier("myFunc")))
        assertThat((declaration.type as TreeNode.Type.Func).paramTypes.size, equalTo(0))
        assertTrue((declaration.type as TreeNode.Type.Func).returnType is TreeNode.Type.None)

        assertThat((declaration.expression as TreeNode.Command.Expression.LambdaExpression).paramDeclarations.size,
                equalTo(0))
        assertTrue((declaration.expression as TreeNode.Command.Expression.LambdaExpression).returnType
                is TreeNode.Type.None)
        assertTrue((declaration.expression as TreeNode.Command.Expression.LambdaExpression).body.isEmpty())
    }
    //endregion LambdaExpression

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
        val ast = Parser(lexer).generateAbstractSyntaxTree()
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
        val ast = Parser(lexer).generateAbstractSyntaxTree()
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
        val ast = Parser(lexer).generateAbstractSyntaxTree()
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
        val exception = assertThrows(Exception::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
        assertEquals("Unrecognized expression", exception.message)
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
        val exception = assertThrows(Exception::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
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
        val ast = Parser(lexer).generateAbstractSyntaxTree()
        assertThat(ast.children.size, equalTo(1))
        assertTrue(ast.children[0] is TreeNode.Command.Declaration)
        val declaration = ast.children[0] as TreeNode.Command.Declaration
        assertTrue(declaration.type is TreeNode.Type.List)
        assertThat(declaration.identifier, equalTo(TreeNode.Command.Expression.Value.Identifier("myList")))
        assertTrue((declaration.type as TreeNode.Type.List).elementType is TreeNode.Type.Number)
    }

    @org.junit.jupiter.api.Test
    fun testParserListDeclaration() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.List())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("MyList"))
            yield(Token.SpecialChar.Equals())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Literal.Number("5.0"))
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Literal.Number("10.0"))
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.SpecialChar.EndOfLine())
        })
        val ast = Parser(lexer).generateAbstractSyntaxTree()
        assertThat(ast.children.size, equalTo(1))
        assertTrue(ast.children[0] is TreeNode.Command.Declaration)
        val declaration = ast.children[0] as TreeNode.Command.Declaration
        assertTrue(declaration.type is TreeNode.Type.List)
        assertThat(declaration.identifier, equalTo(TreeNode.Command.Expression.Value.Identifier("MyList")))
        assertTrue((declaration.type as TreeNode.Type.List).elementType is TreeNode.Type.Number)
        assertThat(declaration.expression as TreeNode.Command.Expression.Value.Literal.List, equalTo(TreeNode.Command.Expression.Value.Literal.List(
                listOf(TreeNode.Command.Expression.Value.Literal.Number(5.0), TreeNode.Command.Expression.Value.Literal.Number(10.0)))))
    }
    @org.junit.jupiter.api.Test
    fun testParserListDeclarationTooFewSeperators() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.List())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("MyList"))
            yield(Token.SpecialChar.Equals())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Literal.Number("5.0"))
            yield(Token.Literal.Number("10.0"))
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.SpecialChar.EndOfLine())
        })
        val exception = assertThrows(Exception::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
        assertEquals("Make this an expected token type T1 but found token type T2", exception.message)

    }
    @org.junit.jupiter.api.Test
    fun testParserListDeclarationTooManySeperators() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.List())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("MyList"))
            yield(Token.SpecialChar.Equals())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Literal.Number("5.0"))
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Literal.Number("10.0"))
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.SpecialChar.EndOfLine())
        })
        val exception = assertThrows(Exception::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
        assertEquals("Make this an expected token type T1 but found token type T2", exception.message)
    }

    //endregion ListTypeDcl

    class DummyLexer(val tokens: Sequence<Token>): ILexer {
        override fun getTokenSequence() = buildSequence {
            tokens.forEach { yield(PositionalToken(it, -1, -1)) }
        }

        override fun inputLine(lineNumber: Int) = "Dummy lexer does not implement input line"
    }


}
