package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.UnexpectedTypeError
import hclTestFramework.lexer.buildTokenSequence
import hclTestFramework.parser.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.Parser

class PrimitiveTypeTests {
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
    fun canParseTxtDeclaration() {
        assertThat(
                buildTokenSequence {
                    text.identifier("myText").`=`.text("someText").newLine
                },
                matchesAstChildren("myText" declaredAs txt withValue txt("someText"))
        )
    }
    @Test
    fun canParseBoolDeclaration() {
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
    fun canParseTxtAssignment() {
        assertThat(
                buildTokenSequence {
                    text.identifier("myText").newLine.identifier("myText").`=`.text("someText").newLine
                },
                matchesAstChildren(
                        "myText" declaredAs txt,
                        "myText" assignedTo txt("someText")
                )
        )
    }
    @Test
    fun canParseNumAssignmentWithIdentifier() {
        assertThat(
                buildTokenSequence {
                    number.identifier("myNumber").`=`.number(5.0).newLine.number.identifier("myId")
                            .`=`.identifier("myNumber").newLine
                },
                matchesAstChildren(
                        "myNumber" declaredAs num withValue num(5),
                        "myId" declaredAs num withValue "myNumber".asIdentifier(num)
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
    fun failOnWrongType() {
        val lexer = DummyLexer(buildTokenSequence {
            bool.identifier("myId").`=`.number(5.0).newLine
        })
        Assertions.assertThrows(UnexpectedTypeError::class.java) { Parser(lexer).commandSequence().toList() }
    }
}