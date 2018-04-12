package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.UnexpectedTokenError
import exceptions.WrongTokenTypeError
import lexer.Token
import org.junit.jupiter.api.Assertions
import parser.Parser
import parser.TreeNode
import sun.reflect.generics.tree.Tree
import kotlin.coroutines.experimental.buildSequence

class CollectionDeclarationTests {

    //region TupleTypeDcl
    @org.junit.jupiter.api.Test
    fun parseTupleTypeWithoutAssignment() {
        assertThat(
                listOf(
                        Token.Type.Tuple,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.ListSeparator,
                        Token.Type.Text,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myTuple"),
                        Token.SpecialChar.EndOfLine
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Tuple(
                                        listOf(
                                                TreeNode.Type.Number,
                                                TreeNode.Type.Text
                                        )
                                ),
                                TreeNode.Command.Expression.Value.Identifier("myTuple")
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun parseTupleWithAssignment() {
        assertThat(
                listOf(
                        Token.Type.Tuple,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.ListSeparator,
                        Token.Type.Text,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myTuple"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.Literal.Number(5.0),
                        Token.SpecialChar.ListSeparator,
                        Token.Literal.Text("someText"),
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.EndOfLine
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Tuple(listOf(
                                        TreeNode.Type.Number,
                                        TreeNode.Type.Text
                                )
                                ),
                                TreeNode.Command.Expression.Value.Identifier("myTuple"),
                                TreeNode.Command.Expression.Value.Literal.Tuple(
                                        listOf(
                                                TreeNode.Command.Expression.Value.Literal.Number(5.0),
                                                TreeNode.Command.Expression.Value.Literal.Text("someText")
                                        )
                                )
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun failsOnNotEnoughSeparators() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Tuple)
            yield(Token.SpecialChar.SquareBracketStart)
            yield(Token.Type.Number)
            yield(Token.SpecialChar.ListSeparator)
            yield(Token.Type.Text)
            yield(Token.SpecialChar.SquareBracketEnd)
            yield(Token.Identifier("myTuple"))
            yield(Token.SpecialChar.Equals)
            yield(Token.SpecialChar.ParenthesesStart)
            yield(Token.Literal.Number(5.0))
            yield(Token.Literal.Text("someText"))
            yield(Token.SpecialChar.ParenthesesEnd)
            yield(Token.SpecialChar.EndOfLine)
        })
        Assertions.assertThrows(WrongTokenTypeError::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }

    @org.junit.jupiter.api.Test
    fun failsOnTupleAssignmentFailsOnTooManySeparators() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Tuple)
            yield(Token.SpecialChar.SquareBracketStart)
            yield(Token.Type.Number)
            yield(Token.SpecialChar.ListSeparator)
            yield(Token.Type.Text)
            yield(Token.SpecialChar.SquareBracketEnd)
            yield(Token.Identifier("myTuple"))
            yield(Token.SpecialChar.Equals)
            yield(Token.SpecialChar.ParenthesesStart)
            yield(Token.Literal.Number(5.0))
            yield(Token.SpecialChar.ListSeparator)
            yield(Token.SpecialChar.ListSeparator)
            yield(Token.Literal.Text("someText"))
            yield(Token.SpecialChar.ParenthesesEnd)
            yield(Token.SpecialChar.EndOfLine)
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }
//endregion TupleTypeDcl

    //region ListTypeDcl
    @org.junit.jupiter.api.Test
    fun parseListType() {
        assertThat(
                listOf(
                        Token.Type.List,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myList"),
                        Token.SpecialChar.EndOfLine
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.List(TreeNode.Type.Number),
                                TreeNode.Command.Expression.Value.Identifier("myList")
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun parseListDeclarationWithAssignment() {
        assertThat(
                listOf(
                        Token.Type.List,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("MyList"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Literal.Number(5.0),
                        Token.SpecialChar.ListSeparator,
                        Token.Literal.Number(10.0),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.SpecialChar.EndOfLine
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.List(TreeNode.Type.Number),
                                TreeNode.Command.Expression.Value.Identifier("MyList"),
                                TreeNode.Command.Expression.Value.Literal.List(
                                        listOf(
                                                TreeNode.Command.Expression.Value.Literal.Number(5.0),
                                                TreeNode.Command.Expression.Value.Literal.Number(10.0)
                                        )
                                )
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun parseListWithLists() {
        assertThat(
                listOf(
                        Token.Type.List,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myNumberList"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Literal.Number(10.0),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.SpecialChar.EndOfLine,
                        Token.Type.List,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.List,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myList"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Identifier("myNumberList"),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.SpecialChar.EndOfLine
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.List(TreeNode.Type.Number),
                                TreeNode.Command.Expression.Value.Identifier("myNumberList"),
                                TreeNode.Command.Expression.Value.Literal.List(
                                        listOf(
                                                TreeNode.Command.Expression.Value.Literal.Number(10.0)
                                        )
                                )
                        ),
                        TreeNode.Command.Declaration(
                                TreeNode.Type.List(TreeNode.Type.List(TreeNode.Type.Number)),
                                TreeNode.Command.Expression.Value.Identifier("myList"),
                                TreeNode.Command.Expression.Value.Literal.List(
                                        listOf(
                                                TreeNode.Command.Expression.Value.Identifier("myNumberList")
                                        )
                                )
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun parseListWithFunc() { //TODO(fix this to be List[none] myList = [myFunc]  where myFunc return 'none')
        assertThat(
                listOf(
                        Token.Type.Func,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.Colon,
                        Token.Type.Number,
                        Token.SpecialChar.BlockStart,
                        Token.SpecialChar.BlockEnd,
                        Token.SpecialChar.EndOfLine,
                        Token.Type.List,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myList"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.SpecialChar.EndOfLine
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Func.ExplicitFunc(listOf(), TreeNode.Type.Number),
                                TreeNode.Command.Expression.Value.Identifier("myFunc"),
                                TreeNode.Command.Expression.LambdaExpression(listOf(), TreeNode.Type.Number, listOf())
                        ),
                        TreeNode.Command.Declaration(
                                TreeNode.Type.List(TreeNode.Type.Number),
                                TreeNode.Command.Expression.Value.Identifier("myList"),
                                TreeNode.Command.Expression.Value.Literal.List(
                                        listOf(
                                                TreeNode.Command.Expression.FunctionCall(
                                                        TreeNode.Command.Expression.Value.Identifier("myFunc"),
                                                        listOf()
                                                        )
                                        )
                                )
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun failsOnListAssignmentWithTooFewSeparators() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.List)
            yield(Token.SpecialChar.SquareBracketStart)
            yield(Token.Type.Number)
            yield(Token.SpecialChar.SquareBracketEnd)
            yield(Token.Identifier("MyList"))
            yield(Token.SpecialChar.Equals)
            yield(Token.SpecialChar.SquareBracketStart)
            yield(Token.Literal.Number(5.0))
            yield(Token.Literal.Number(10.0))
            yield(Token.SpecialChar.SquareBracketEnd)
            yield(Token.SpecialChar.EndOfLine)
        })
        Assertions.assertThrows(WrongTokenTypeError::class.java) { Parser(lexer).generateAbstractSyntaxTree() }

    }

    @org.junit.jupiter.api.Test
    fun failsOnListAssignmentWithTooManySeparators() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.List)
            yield(Token.SpecialChar.SquareBracketStart)
            yield(Token.Type.Number)
            yield(Token.SpecialChar.SquareBracketEnd)
            yield(Token.Identifier("MyList"))
            yield(Token.SpecialChar.Equals)
            yield(Token.SpecialChar.SquareBracketStart)
            yield(Token.Literal.Number(5.0))
            yield(Token.SpecialChar.ListSeparator)
            yield(Token.SpecialChar.ListSeparator)
            yield(Token.Literal.Number(10.0))
            yield(Token.SpecialChar.SquareBracketEnd)
            yield(Token.SpecialChar.EndOfLine)
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }
}
