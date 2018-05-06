package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.UnexpectedTokenError
import exceptions.WrongTokenTypeError
import hclTestFramework.lexer.buildTokenSequence
import org.junit.jupiter.api.Assertions
import parser.AstNode
import parser.ParserWithoutBuiltins

class TupleTests
{

    @org.junit.jupiter.api.Test
    fun parseTupleTypeWithoutAssignment() {
        assertThat(
                buildTokenSequence {
                    tuple.squareStart.number.`,`.text.squareEnd.identifier("myTuple").newLine
                },
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
                buildTokenSequence {
                    tuple.squareStart.number.`,`.text.squareEnd.identifier("myTuple").`=`.`(`.number(5.0).`,`.text("someText").`)`.newLine
                },
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
                buildTokenSequence {
                    tuple.squareStart.number.squareEnd.identifier("myTuple").`=`.`(`.number(5.0).`,`.`)`.newLine
                },
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
        val lexer = DummyLexer(buildTokenSequence {
            tuple.squareStart.number.`,`.text.squareEnd.identifier("myTuple").`=`.`(`.number(5.0).text("someText").`)`.newLine
        })
        Assertions.assertThrows(WrongTokenTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @org.junit.jupiter.api.Test
    fun failsOnTupleAssignmentFailsOnTooManySeparators() {
        val lexer = DummyLexer(buildTokenSequence {
            tuple.squareStart.number.`,`.text.squareEnd.identifier("myTuple").`=`.`(`.number(5.0).`,`.`,`.text("someText").`)`.newLine
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

}