package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.UndeclaredError
import exceptions.WrongTokenTypeError
import hclTestFramework.lexer.buildTokenSequence
import hclTestFramework.parser.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import parser.Parser

class FunctionCallTests {
    @Test
    fun canParseFunctionCallWithoutParameters() {
        assertThat(
            buildTokenSequence {
                func.squareStart.text.squareEnd.identifier("myFunc").`=`.`(`.`)`.colon.text.`{`.text("HEY").`}`.newLine
                .identifier("myFunc").newLine
            },
            matchesAstChildren(
                    "myFunc" declaredAs func(txt) withValue (lambda() returning txt andBody ret(txt("HEY"))),
                    ("myFunc" returning (txt)).called()
            )
        )
    }

    @Test
    fun canParseFunctionCallWithOverloading() {
        assertThat(
                buildTokenSequence {
                    func.squareStart.number.`,`.text.squareEnd.identifier("toString").`=`.`(`.number
                    .identifier("myParam").`)`.colon.text.`{`.text("HEY").`}`.newLine

                    .func.squareStart.text.`,`.text.squareEnd.identifier("toString").`=`.`(`.text
                    .identifier("myParam").`)`.colon.text.`{`.text("HEY").`}`.newLine

                    .number(5.0).identifier("toString").newLine
                },
                matchesAstChildren(
                        "toString" declaredAs func(txt, num) withValue (
                            lambda() returning txt withArgument ("myParam" asType num) andBody ret(txt("HEY"))
                        ),
                        "toString" declaredAs func(txt, txt) withValue (
                            lambda() returning txt withArgument ("myParam" asType txt) andBody ret(txt("HEY"))
                        ),
                        "toString" returning txt calledWith num(5)
                )
        )
    }

    @Test
    fun canParseFunctionCallOneParameter() {
        assertThat(
            buildTokenSequence {
                func.squareStart.number.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number.identifier("myParam").`)`
                .colon.text.`{`.text("HEY").`}`.newLine

                .number(5.0).identifier("myFunc").newLine
            },
                matchesAstChildren(
                    "myFunc" declaredAs func(txt, num) withValue (
                        lambda() returning txt withArgument ("myParam" asType num) andBody ret(txt("HEY"))
                    ),
                    "myFunc" returning txt calledWith num(5)
                )
        )
    }
    @Test
    fun canParseFunctionCallHighOrder() {
        assertThat(
            buildTokenSequence {
                `var`.identifier("myFunc").`=`.`(`.func.squareStart.text.squareEnd.identifier("myParam").`)`.colon.text
                .`{`.text("HEY").`}`.newLine

                .`var`.identifier("myTextFunc").`=`.`(`.`)`.colon.text.`{`.text("HEY").`}`.newLine

                .colon.identifier("myTextFunc").identifier("myFunc").newLine
            },
                matchesAstChildren(
                    "myFunc" declaredAs func(txt, func(txt)) withValue
                        (lambda() returning txt withArgument ("myParam" asType func(txt)) andBody ret(txt("HEY"))),
                    "myTextFunc" declaredAs func(txt) withValue (lambda() returning txt withBody ret(txt("HEY"))),
                    "myFunc" returning txt calledWith "myTextFunc".asIdentifier(func(txt))
                )
        )
    }
    @Test
    fun failParseFunctionCallHighOrderWithWrongSignature() {

        val lexer = DummyLexer(buildTokenSequence {
            `var`.identifier("myFunc").`=`.`(`.func.squareStart.text.squareEnd.identifier("myParam").`)`.colon.text.`{`
            .text("HEY HEY").`}`.newLine

            .`var`.identifier("myTextFunc").`=`.`(`.text.identifier("myParam").`)`.colon.text.`{`.text("BLA BLA").`}`.newLine

            .colon.identifier("myTextFunc").identifier("myFunc").newLine
        })
        assertThrows(UndeclaredError::class.java) { Parser(lexer).commandSequence().toList() }
    }

    @Test
    fun failsParseFunctionCallNeedsOneArgumentButGetsZero() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number.identifier("myParam").`)`
            .colon.text.`{`.`}`.newLine

            .identifier("myFunc").newLine
        })
        // TODO use less generic error
        assertThrows(Exception::class.java) { Parser(lexer).commandSequence().toList() }
    }

    @Test
    fun failsParseFunctionCallNeedsOneArgumentButGetsTwo() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number.identifier("myParam").`)`
            .colon.text.`{`.text("HEY").`}`.newLine

            .number(5.0).identifier("myFunc").number(5.0).newLine
        })

        assertThrows(WrongTokenTypeError::class.java) { Parser(lexer).commandSequence().toList() }
    }

    @Test
    fun canParseFunctionCallWithTupleLiteralAsFirstArgument() {
        assertThat(
            buildTokenSequence {
                func.squareStart.tuple.squareStart.number.`,`.text.squareEnd.`,`.text.squareEnd.identifier("myFunc").`=`
                .`(`.tuple.squareStart.number.`,`.text.squareEnd.identifier("myParam").`)`.colon.text.`{`.text("HEY").`}`.newLine

                .`(`.number(5.0).`,`.text("hej").`)`.identifier("myFunc").newLine
            },
                matchesAstChildren(
                    "myFunc" declaredAs func(txt, tpl(num, txt)) withValue (
                        lambda() returning txt withArgument ("myParam" asType tpl(num, txt)) andBody ret(txt("HEY"))
                    ),
                    "myFunc" returning txt calledWith tpl(num(5), txt("hej"))
                )
        )
    }

    @Test
    fun failsToParseFunctionCallWith_FunctionCallWithArguments_AsRightHandSideArgumentWithoutParentheses() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.number.`,`.number.squareEnd.identifier("myFunc").`=`.`(`.number
            .identifier("myParam1").`,`.number.identifier("myParam2").`)`.colon.number.`{`.number(5.0).`}`.newLine

            .number(5.0).identifier("myFunc").identifier("myFunc").number(5.0).newLine
        })
        assertThrows(Exception::class.java) { Parser(lexer).commandSequence().toList() }
    }

    @Test
    fun canParseFunctionCallWith_FunctionCallWithoutArguments_AsRightHandSideArgumentWithoutParentheses() {
        assertThat(
            buildTokenSequence {
                func.squareStart.number.`,`.number.`,`.number.squareEnd.identifier("myFunc").`=`.`(`.number
                .identifier("myParam1").`,`.number.identifier("myParam2").`)`.colon.number.`{`.number(5.0).`}`.newLine

                .func.squareStart.number.squareEnd.identifier("myFunc2").`=`.`(`.`)`.colon.number.`{`.number(5.0).`}`.newLine
                .number(5.0).identifier("myFunc").identifier("myFunc2").newLine
            },
                matchesAstChildren(
                    "myFunc" declaredAs func(num, listOf(num, num)) withValue (lambda()
                        returning num
                        withArguments listOf("myParam1" asType num, "myParam2" asType num)
                        withBody ret(num(5))
                    ),
                    "myFunc2" declaredAs func(num) withValue (lambda() returning num withBody ret(num(5))),
                    "myFunc" returning num calledWith listOf(num(5), ("myFunc2" returning num).called())
                )
        )
    }

    @Test
    fun failsParseFunctionCallNeedsTwoArgumentButGetsThree() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.number.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number.identifier("myParam1")
            .`,`.number.identifier("myParam2").`)`.colon.text.`{`.text("HEY").`}`.newLine

            .number(5.0).identifier("myFunc").number(5.0).text("hej").newLine
        })
        assertThrows(WrongTokenTypeError::class.java) { Parser(lexer).commandSequence().toList() }
    }

    @Test
    fun canParseTwoCallsWithOneParameterEach() {
        assertThat(
            buildTokenSequence {
                func.squareStart.number.`,`.number.squareEnd.identifier("myFunc").`=`.`(`.number.identifier("myParam")
                .`)`.colon.number.`{`.number(5.0).`}`.newLine

                .number(5.0).identifier("myFunc").identifier("myFunc").newLine
            },
                matchesAstChildren(
                    "myFunc" declaredAs func(num, num) withValue (
                        lambda()
                            returning num
                            withArgument ("myParam" asType num)
                            withBody ret(num(5))
                    ),
                    "myFunc" returning num calledWith ("myFunc" returning num calledWith num(5))
                )
        )
    }

    @Test
    fun canParseFunctionCallTwoParameters() {
        assertThat(
            buildTokenSequence {
                func.squareStart.number.`,`.text.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number
                .identifier("myParam1").`,`.text.identifier("myParam2").`)`.colon.text.`{`.text("HEY").`}`.newLine

                .number(5.0).identifier("myFunc").text("hej").newLine
            },
                matchesAstChildren(
                    "myFunc" declaredAs func(txt, listOf(num, txt)) withValue (
                        lambda()
                            returning txt
                            withArguments listOf("myParam1" asType num, "myParam2" asType txt)
                            andBody ret(txt("HEY"))
                        ),
                    "myFunc" returning txt calledWith listOf(num(5), txt("hej"))
                )
        )
    }

    @Test
    fun canParseFunctionCallFourParameters() {
        assertThat(
            buildTokenSequence {
                func.squareStart.number.`,`.text.`,`.text.`,`.text.`,`.text.squareEnd.identifier("myFunc").`=`.`(`
                .number.identifier("myParam1").`,`.text.identifier("myParam2").`,`.text.identifier("myParam3").`,`
                .text.identifier("myParam4").`)`.colon.text.`{`.text("HEY").`}`.newLine

                .number(5.0).identifier("myFunc").text("hej").text("med").text("dig").newLine
            },
                matchesAstChildren(
                    "myFunc" declaredAs func(txt, listOf(num, txt, txt, txt)) withValue (
                        lambda()
                            returning txt
                            withArguments listOf(
                                "myParam1" asType num,
                                "myParam2" asType txt,
                                "myParam3" asType txt,
                                "myParam4" asType txt
                            )
                            andBody ret(txt("HEY"))
                    ),
                    "myFunc" returning txt calledWith listOf(num(5), txt("hej"), txt("med"), txt("dig"))
                )
        )
    }
}
