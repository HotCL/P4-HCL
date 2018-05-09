package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.UnexpectedTokenError
import exceptions.WrongTokenTypeError
import hclTestFramework.lexer.buildTokenSequence
import hclTestFramework.parser.*
import org.junit.jupiter.api.Assertions
import parser.ParserWithoutBuiltins

class TupleTests
{
    @org.junit.jupiter.api.Test
    fun parseTupleTypeWithoutAssignment() {
        assertThat(
            buildTokenSequence {
                tuple.squareStart.number.`,`.text.squareEnd.identifier("myTuple").newLine
            },
            matchesAstChildren("myTuple" declaredAs tpl(num, txt))
        )
    }

    @org.junit.jupiter.api.Test
    fun parseTupleWithAssignment() {
        assertThat(
            buildTokenSequence {
                tuple.squareStart.number.`,`.text.squareEnd.identifier("myTuple").`=`.`(`.number(5.0)
                        .`,`.text("someText").`)`.newLine
            },
            matchesAstChildren("myTuple" declaredAs tpl(num, txt) withValue tpl(num(5),
                               txt("someText")))
        )
    }

    @org.junit.jupiter.api.Test
    fun parseTupleWithSingleElement() {
        assertThat(
            buildTokenSequence {
                tuple.squareStart.number.squareEnd.identifier("myTuple").`=`.`(`.number(5.0).`,`.`)`.newLine
            },
            matchesAstChildren("myTuple" declaredAs tpl(num) withValue tpl(num(5)))
        )
    }

    @org.junit.jupiter.api.Test
    fun failsOnNotEnoughSeparators() {
        val lexer = DummyLexer(buildTokenSequence {
            tuple.squareStart.number.`,`.text.squareEnd.identifier("myTuple").`=`.`(`.number(5.0)
                    .text("someText").`)`.newLine
        })
        Assertions.assertThrows(WrongTokenTypeError::class.java)
            { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @org.junit.jupiter.api.Test
    fun failsOnTupleAssignmentFailsOnTooManySeparators() {
        val lexer = DummyLexer(buildTokenSequence {
            tuple.squareStart.number.`,`.text.squareEnd.identifier("myTuple").`=`.`(`.number(5.0)
                    .`,`.`,`.text("someText").`)`.newLine
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java)
            { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

}