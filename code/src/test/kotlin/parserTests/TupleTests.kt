package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.UnexpectedTokenError
import exceptions.WrongTokenTypeError
import lexer.Token
import org.junit.jupiter.api.Assertions
import parser.AstNode
import parser.Parser
import parser.ParserWithoutBuiltins
import kotlin.coroutines.experimental.buildSequence

class TupleTests
{

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
                        AstNode.Command.Declaration(
                                AstNode.Type.Tuple(
                                        listOf(
                                                AstNode.Type.Number,
                                                AstNode.Type.Text
                                        )
                                ),
                                AstNode.Command.Expression.Value.Identifier("myTuple")
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
                        AstNode.Command.Declaration(
                                AstNode.Type.Tuple(listOf(
                                        AstNode.Type.Number,
                                        AstNode.Type.Text
                                )
                                ),
                                AstNode.Command.Expression.Value.Identifier("myTuple"),
                                AstNode.Command.Expression.Value.Literal.Tuple(
                                        listOf(
                                                AstNode.Command.Expression.Value.Literal.Number(5.0),
                                                AstNode.Command.Expression.Value.Literal.Text("someText")
                                        )
                                )
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun parseTupleWithSingleElement() {
        assertThat(
                listOf(
                        Token.Type.Tuple,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myTuple"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.Literal.Number(5.0),
                        Token.SpecialChar.ListSeparator,
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.EndOfLine
                ),
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Tuple(listOf(
                                        AstNode.Type.Number
                                )
                                ),
                                AstNode.Command.Expression.Value.Identifier("myTuple"),
                                AstNode.Command.Expression.Value.Literal.Tuple(
                                        listOf(
                                                AstNode.Command.Expression.Value.Literal.Number(5.0)
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
        Assertions.assertThrows(WrongTokenTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
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
        Assertions.assertThrows(UnexpectedTokenError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

}