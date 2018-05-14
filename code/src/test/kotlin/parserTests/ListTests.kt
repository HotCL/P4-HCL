package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.ImplicitTypeNotAllowed
import exceptions.UnexpectedTokenError
import exceptions.WrongTokenTypeError
import hclTestFramework.lexer.buildTokenSequence
import hclTestFramework.parser.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.ParserWithoutBuiltins

class ListTests {

    @org.junit.jupiter.api.Test
    fun parseListType() {
        assertThat(
            buildTokenSequence {
                list.squareStart.number.squareEnd.identifier("myList").newLine
            },
            matchesAstChildren("myList" declaredAs list(num))
        )
    }

    @org.junit.jupiter.api.Test
    fun parseListDeclarationWithAssignment() {
        assertThat(
            buildTokenSequence {
                list.squareStart.number.squareEnd.identifier("myList").`=`.squareStart.number(5.0).`,`.number(10.0)
                .squareEnd.newLine
            },
            matchesAstChildren("myList" declaredAs list(num) withValue list(num(5), num(10)))
        )
    }

    @org.junit.jupiter.api.Test
    fun parseListWithLists() {
        assertThat(
            buildTokenSequence {
                list.squareStart.number.squareEnd.identifier("myNumberList").`=`.squareStart.number(10.0).squareEnd.newLine
                .list.squareStart.list.squareStart.number.squareEnd.squareEnd.identifier("myList").`=`.squareStart
                .identifier("myNumberList").squareEnd.newLine
            },
            matchesAstChildren(
                "myNumberList" declaredAs list(num) withValue list(num(10)),
                "myList" declaredAs list(list(num)) withValue list("myNumberList".asIdentifier(list(num)))
            )
        )
    }

    @org.junit.jupiter.api.Test
    fun parseListWithFuncCall() {
        assertThat(
            buildTokenSequence {
                func.squareStart.number.squareEnd.identifier("myFunc").`=`.`(`.`)`.colon.number.`{`.number(5.0).`}`.newLine
                .list.squareStart.number.squareEnd.identifier("myList").`=`.squareStart.identifier("myFunc").squareEnd.newLine
            },
            matchesAstChildren(
                "myFunc" declaredAs func(num) withValue (lambda() returning num withBody ret(num(5))),
                "myList" declaredAs list(num) withValue list(("myFunc" returning num).called())
            )
        )
    }

    @org.junit.jupiter.api.Test
    fun failsOnListAssignmentWithTooFewSeparators() {
        val lexer = DummyLexer(buildTokenSequence {
            list.squareStart.number.squareEnd.identifier("MyList").`=`.squareStart.number(5.0).number(10.0).squareEnd.newLine
        })
        Assertions.assertThrows(WrongTokenTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @org.junit.jupiter.api.Test
    fun failsOnListAssignmentWithTooManySeparators() {
        val lexer = DummyLexer(buildTokenSequence {
            list.squareStart.number.squareEnd.identifier("MyList").`=`.squareStart.number(5.0).`,`.`,`.number(10.0).squareEnd.newLine
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
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

    @org.junit.jupiter.api.Test
    fun failsOnImplicitFuncAsParameter() {
        val lexer = DummyLexer(buildTokenSequence {
            list.squareStart.func.squareEnd.identifier("myFunc").newLine
        })
        Assertions.assertThrows(ImplicitTypeNotAllowed::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }
}
