package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.ImplicitTypeNotAllowed
import exceptions.UnexpectedTypeError
import hclTestFramework.lexer.buildTokenSequence
import hclTestFramework.parser.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import parser.ParserWithoutBuiltins

class MiscellaneousTests {

    @Test
    fun canParseNumDeclaration() {
        assertThat(
            buildTokenSequence {
                number.identifier("myId").`=`.number(5.0).newLine
            },
            matchesAstChildren("myId" declaredAs num withValue num(5))
        )
    }

    @Test
    fun failOnWrongType() {
        val lexer = DummyLexer(buildTokenSequence {
            bool.identifier("myId").`=`.number(5.0).newLine
        })
        Assertions.assertThrows(UnexpectedTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun canParseBool() {
        assertThat(
            buildTokenSequence {
                bool.identifier("myBool").`=`.bool(true).newLine
            },
            matchesAstChildren("myBool" declaredAs bool withValue bool(true))
        )
    }

    @Test
    fun canParseNumAssignment() {
        assertThat(
            buildTokenSequence {
                number.identifier("myId").newLine.identifier("myId").`=`.number(5.0).newLine
            },
            matchesAstChildren(
                "myId" declaredAs num,
                "myId" assignedTo num(5)
            )
        )
    }

    @Test
    fun canParseVarAssignmentNumber() {
        assertThat(
            buildTokenSequence {
                `var`.identifier("myId").`=`.number(5.0).newLine
            },
            matchesAstChildren("myId" declaredAs num withValue num(5))
        )
    }

    @Test
    fun canParseVarAssignmentList() {
        assertThat(
            buildTokenSequence {
                `var`.identifier("myList").`=`.squareStart.number(5.0).`,`.number(7.0).squareEnd.newLine
            },
            matchesAstChildren("myList" declaredAs list(num) withValue list(num(5), num(7)))
        )
    }

    @Test
    fun canParseVarAssignmentFunction() {
        assertThat(
            buildTokenSequence {
                `var`.identifier("myFunc").`=`.`(`.number.identifier("x").`)`.colon.bool.`{`.bool(true).blockEnd.newLine
            },
            matchesAstChildren(
                "myFunc" declaredAs func(bool, num) withValue (
                    lambda() returning bool withArgument ("x" asType num) withBody ret(bool(true))
                )
            )
        )
    }

    @Test
    fun canParseNumAssignmentWithIdentifier() {
        assertThat(
            buildTokenSequence {
                number.identifier("myNumber").`=`.number(5.0).newLine.number.identifier("myId").`=`.identifier("myNumber").newLine
            },
            matchesAstChildren(
                "myNumber" declaredAs num withValue num(5),
                "myId" declaredAs num withValue "myNumber".asIdentifier(num)
            )
        )
    }

    @org.junit.jupiter.api.Test
    fun failsOnImplicitFuncAsParameter() {
        val lexer = DummyLexer(buildTokenSequence {
            list.squareStart.func.squareEnd.identifier("myFunc").newLine
        })
        assertThrows(ImplicitTypeNotAllowed::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun failsOnLineStartsWithEquals() {
        val lexer = DummyLexer(buildTokenSequence {
            `=`.newLine
        })
        assertThrows(Exception::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }
}
