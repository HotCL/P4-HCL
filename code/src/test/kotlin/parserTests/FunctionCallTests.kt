package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.UndeclaredError
import exceptions.WrongTokenTypeError
import hclTestFramework.lexer.buildTokenSequence
import hclTestFramework.parser.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import parser.AstNode
import parser.ParserWithoutBuiltins

class FunctionCallTests {
    @Test
    fun canParseFunctionCallWithoutParameters() {
        assertThat(
            buildTokenSequence {
                func.squareStart.text.squareEnd.identifier("myFunc").`=`.`(`.`)`.colon.text.`{`.text("HEY").`}`.newLine.
                identifier("myFunc").newLine
            },
            matchesAstChildren(
                    "myFunc" declaredAs func(txt) withValue (lambda() returning txt andBody ret(txt("HEY"))),
                    "myFunc".called()
            )
        )
    }

    @Test
    fun canParseFunctionCallWithOverloading() {
        assertThat(
                buildTokenSequence {
                    func.squareStart.number.`,`.text.squareEnd.identifier("toString").`=`.`(`.number.
                    identifier("myParam").`)`.colon.text.`{`.text("HEY").`}`.newLine.

                    func.squareStart.text.`,`.text.squareEnd.identifier("toString").`=`.`(`.text.
                    identifier("myParam").`)`.colon.text.`{`.text("HEY").`}`.newLine.

                    number(5.0).identifier("toString").newLine
                },
                matchesAstChildren(
                        "toString" declaredAs func(txt, num) withValue (
                            lambda() returning txt withArgument ("myParam" asType num) andBody ret(txt("HEY"))
                        ),
                        "toString" declaredAs func(txt, txt) withValue (
                            lambda() returning txt withArgument ("myParam" asType txt) andBody ret(txt("HEY"))
                        ),
                        "toString" calledWith num(5)
                )
        )
    }

    @Test
    fun canParseFunctionCallOneParameter() {
        assertThat(
            buildTokenSequence {
                func.squareStart.number.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number.identifier("myParam").`)`.
                colon.text.`{`.text("HEY").`}`.newLine.

                number(5.0).identifier("myFunc").newLine
            },
                matchesAstChildren(
                    "myFunc" declaredAs func(txt, num) withValue (
                        lambda() returning txt withArgument ("myParam" asType num) andBody ret(txt("HEY"))
                    ),
                    "myFunc" calledWith num(5)
                )
        )
    }
    @Test
    fun canParseFunctionCallHighOrder() {
        assertThat(
            buildTokenSequence {
                `var`.identifier("myFunc").`=`.`(`.func.squareStart.text.squareEnd.identifier("myParam").`)`.colon.text.
                `{`.text("HEY").`}`.newLine.

                `var`.identifier("myTextFunc").`=`.`(`.`)`.colon.text.`{`.text("HEY").`}`.newLine.

                colon.identifier("myTextFunc").identifier("myFunc").newLine
            },
                matchesAstChildren(
                    "myFunc" declaredAs func(txt, func(txt)) withValue
                        (lambda() returning txt withArgument ("myParam" asType func(txt)) andBody ret(txt("HEY"))),
                    "myTextFunc" declaredAs func(txt) withValue (lambda() returning txt withBody ret(txt("HEY"))),
                    "myFunc" calledWith "myTextFunc".asIdentifier
                )
        )
    }
    @Test
    fun failParseFunctionCallHighOrderWithWrongSignature() {

        val lexer = DummyLexer(buildTokenSequence {
            `var`.identifier("myFunc").`=`.`(`.func.squareStart.text.squareEnd.identifier("myParam").`)`.colon.text.`{`.
            text("HEY HEY").`}`.newLine.

            `var`.identifier("myTextFunc").`=`.`(`.text.identifier("myParam").`)`.colon.text.`{`.text("BLA BLA").`}`.newLine.

            colon.identifier("myTextFunc").identifier("myFunc").newLine
        })
        assertThrows(UndeclaredError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun failsParseFunctionCallNeedsOneArgumentButGetsZero() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number.identifier("myParam").`)`.
            colon.text.`{`.`}`.newLine.

            identifier("myFunc").newLine
        })
        //TODO use less generic error
        assertThrows(Exception::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun failsParseFunctionCallNeedsOneArgumentButGetsTwo() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number.identifier("myParam").`)`.
            colon.text.`{`.text("HEY").`}`.newLine.

            number(5.0).identifier("myFunc").number(5.0).newLine
        })

        assertThrows(WrongTokenTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun canParseFunctionCallWithTupleLiteralAsFirstArgument() {
        assertThat(
            buildTokenSequence {
                func.squareStart.tuple.squareStart.number.`,`.text.squareEnd.`,`.text.squareEnd.identifier("myFunc").`=`.
                `(`.tuple.squareStart.number.`,`.text.squareEnd.identifier("myParam").`)`.colon.text.`{`.text("HEY").`}`.newLine.

                `(`.number(5.0).`,`.text("hej").`)`.identifier("myFunc").newLine
            },
                matchesAstChildren(
                    "myFunc" declaredAs func(txt, tpl(num, txt)) withValue (
                        lambda() returning txt withArgument ("myParam" asType tpl(num, txt)) andBody ret(txt("HEY"))
                    ),
                    "myFunc" calledWith tpl(num(5), txt("hej"))
                )
        )
    }

    @Test
    fun failsToParseFunctionCallWith_FunctionCallWithArguments_AsRightHandSideArgumentWithoutParentheses() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.number.`,`.number.squareEnd.identifier("myFunc").`=`.`(`.number.
            identifier("myParam1").`,`.number.identifier("myParam2").`)`.colon.number.`{`.number(5.0).`}`.newLine.

            number(5.0).identifier("myFunc").identifier("myFunc").number(5.0).newLine
        })
        assertThrows(Exception::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun canParseFunctionCallWith_FunctionCallWithoutArguments_AsRightHandSideArgumentWithoutParentheses() {
        assertThat(
            buildTokenSequence {
                func.squareStart.number.`,`.number.`,`.number.squareEnd.identifier("myFunc").`=`.`(`.number.
                identifier("myParam1").`,`.number.identifier("myParam2").`)`.colon.number.`{`.number(5.0).`}`.newLine.

                func.squareStart.number.squareEnd.identifier("myFunc2").`=`.`(`.`)`.colon.number.`{`.number(5.0).`}`.newLine.
                number(5.0).identifier("myFunc").identifier("myFunc2").newLine
            },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.Number, AstNode.Type.Number),
                                        AstNode.Type.Number
                                ),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression(
                                        listOf(
                                                AstNode.ParameterDeclaration(
                                                        AstNode.Type.Number,
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")),
                                                AstNode.ParameterDeclaration(
                                                        AstNode.Type.Number,
                                                        AstNode.Command.Expression.Value.Identifier("myParam2"))
                                        ),
                                        AstNode.Type.Number,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                    AstNode.Command.Expression.Value.Literal.Number(5.0)
                                                )
                                        ))
                                )
                        ),
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(),
                                        AstNode.Type.Number
                                ),
                                AstNode.Command.Expression.Value.Identifier("myFunc2"),
                                AstNode.Command.Expression.LambdaExpression(
                                        listOf(),
                                        AstNode.Type.Number,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Literal.Number(5.0)
                                                )
                                        ))
                                )
                        ),
                        AstNode.Command.Expression.FunctionCall(
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                listOf(
                                        AstNode.Command.Expression.Value.Literal.Number(5.0),
                                        AstNode.Command.Expression.FunctionCall(
                                                AstNode.Command.Expression.Value.Identifier("myFunc2"),
                                                listOf()
                                        )
                                )
                        )
                )
        )
    }

    @Test
    fun failsParseFunctionCallNeedsTwoArgumentButGetsThree() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.number.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number.identifier("myParam1")
            .`,`.number.identifier("myParam2").`)`.colon.text.`{`.text("HEY").`}`.newLine.

            number(5.0).identifier("myFunc").number(5.0).text("hej").newLine
        })
        assertThrows(WrongTokenTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun canParseTwoCallsWithOneParameterEach() {
        assertThat(
            buildTokenSequence {
                func.squareStart.number.`,`.number.squareEnd.identifier("myFunc").`=`.`(`.number.identifier("myParam").
                `)`.colon.number.`{`.number(5.0).`}`.newLine.

                number(5.0).identifier("myFunc").identifier("myFunc").newLine
            },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.Number),
                                        AstNode.Type.Number
                                ),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression(
                                        listOf(
                                                AstNode.ParameterDeclaration(
                                                        AstNode.Type.Number,
                                                        AstNode.Command.Expression.Value.Identifier("myParam"))
                                        ),
                                        AstNode.Type.Number,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Literal.Number(5.0)
                                                )
                                        ))
                                )
                        ),
                        AstNode.Command.Expression.FunctionCall(
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                listOf(
                                        AstNode.Command.Expression.FunctionCall(
                                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                                listOf(AstNode.Command.Expression.Value.Literal.Number(5.0))
                                        )
                                )
                        )
                )
        )
    }

    @Test
    fun canParseFunctionCallTwoParameters() {
        assertThat(
            buildTokenSequence {
                func.squareStart.number.`,`.text.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number.
                identifier("myParam1").`,`.text.identifier("myParam2").`)`.colon.text.`{`.text("HEY").`}`.newLine.

                number(5.0).identifier("myFunc").text("hej").newLine
            },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.Number, AstNode.Type.Text),
                                        AstNode.Type.Text
                                ),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression(
                                        listOf(
                                                AstNode.ParameterDeclaration(
                                                        AstNode.Type.Number,
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")),
                                                AstNode.ParameterDeclaration(
                                                        AstNode.Type.Text,
                                                        AstNode.Command.Expression.Value.Identifier("myParam2"))
                                        ),
                                        AstNode.Type.Text,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Literal.Text("HEY")
                                                )
                                        ))
                                )
                        ),
                        AstNode.Command.Expression.FunctionCall(
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                listOf(AstNode.Command.Expression.Value.Literal.Number(5.0),
                                        AstNode.Command.Expression.Value.Literal.Text("hej")
                                )
                        )
                )
        )
    }

    @Test
    fun canParseFunctionCallFourParameters() {
        assertThat(
            buildTokenSequence {
                func.squareStart.number.`,`.text.`,`.text.`,`.text.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.
                number.identifier("myParam1").`,`.text.identifier("myParam2").`,`.text.identifier("myParam3").`,`.
                text.identifier("myParam4").`)`.colon.text.`{`.text("HEY").`}`.newLine.

                number(5.0).identifier("myFunc").text("hej").text("med").text("dig").newLine
            },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.Number, AstNode.Type.Text, AstNode.Type.Text, AstNode.Type.Text),
                                        AstNode.Type.Text
                                ),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression(
                                        listOf(
                                                AstNode.ParameterDeclaration(
                                                        AstNode.Type.Number,
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")),
                                                AstNode.ParameterDeclaration(
                                                        AstNode.Type.Text,
                                                        AstNode.Command.Expression.Value.Identifier("myParam2")),
                                                AstNode.ParameterDeclaration(
                                                        AstNode.Type.Text,
                                                        AstNode.Command.Expression.Value.Identifier("myParam3")),
                                                AstNode.ParameterDeclaration(
                                                        AstNode.Type.Text,
                                                        AstNode.Command.Expression.Value.Identifier("myParam4"))
                                        ),
                                        AstNode.Type.Text,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Literal.Text("HEY")
                                                )
                                        ))
                                )
                        ),
                        AstNode.Command.Expression.FunctionCall(
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                listOf(AstNode.Command.Expression.Value.Literal.Number(5.0),
                                        AstNode.Command.Expression.Value.Literal.Text("hej"),
                                        AstNode.Command.Expression.Value.Literal.Text("med"),
                                        AstNode.Command.Expression.Value.Literal.Text("dig")
                                )
                        )
                )
        )
    }
}
