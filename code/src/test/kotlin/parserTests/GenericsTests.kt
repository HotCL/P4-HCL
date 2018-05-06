package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.GenericPassedFunctionException
import exceptions.UndeclaredError
import exceptions.UnexpectedTokenError
import exceptions.UnexpectedTypeError
import hclTestFramework.lexer.buildTokenSequence
import hclTestFramework.parser.*
import lexer.Token
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.AstNode
import parser.Parser
import parser.ParserWithoutBuiltins

class GenericsTests{

    @Test
    fun canDeclareWithGenerics() {
        assertThat(
            buildTokenSequence {
                func.squareStart.identifier("T").`,`.text.squareEnd.identifier("myFunc").`=`.`(`.
                identifier("T").identifier("myParam1").`)`.colon.text.`{`.text("yeah").`}`.newLine
            },
            matchesAstChildren(
                "myFunc" declaredAs func(txt, generic("T")) withValue (
                    lambda() returning txt withArgument ("myParam1" asType generic("T")) andBody ret(txt("yeah"))
                )
            )
        )
    }

    @Test
    fun canCallFunctionGenerics() {
        assertThat(
            buildTokenSequence {
                func.squareStart.identifier("T").`,`.text.squareEnd.identifier("myFunc").`=`.`(`.identifier("T").
                identifier("myParam1").`)`.colon.text.`{`.text("generics!").`}`.newLine.
                number(5.0).identifier("myFunc").newLine
            },
            matchesAstChildren(
                "myFunc" declaredAs func(txt, generic("T")) withValue (
                    lambda() returning txt withArgument ("myParam1" asType generic("T")) andBody ret(txt("generics!"))
                ),
                "myFunc" calledWith num(5)
            )
        )
    }

    @Test
    fun canCallFunctionGenericsNested() {
        assertThat(
            buildTokenSequence {
                func.squareStart.list.squareStart.identifier("T").squareEnd.`,`.tuple.squareStart.number.`,`.
                identifier("T3").squareEnd.`,`.identifier("T").`,`.identifier("T").squareEnd.identifier("myFunc").
                `=`.`(`.list.squareStart.identifier("T").squareEnd.identifier("myParam").`,`.tuple.squareStart.
                number.`,`.identifier("T3").squareEnd.identifier("myParam1").`,`.identifier("T").identifier("myT").
                `)`.colon.identifier("T").`{`.identifier("myT").`}`.newLine.

                number.identifier("x").`=`.squareStart.number(1.0).`,`.number(2.0).squareEnd.identifier("myFunc").
                `(`.number(1.0).`,`.text("test").`)`.number(9.0).newLine
            },
            matchesAstChildren(
                "myFunc" declaredAs func(generic("T"), listOf(list(generic("T")), tpl(num, generic("T3")), generic("T")))
                withValue (lambda() returning generic("T") withArguments listOf(
                        "myParam" asType list(generic("T")),
                        "myParam1" asType tpl(num, generic("T3")),
                        "myT" asType generic("T")
                    ) andBody ret("myT".asIdentifier)
                ),
                "x" declaredAs num withValue ("myFunc" calledWith listOf(list(num(1), num(2)), tpl(num(1), txt("test")), num(9)))
            )
        )
    }

    @Test
    fun testIdentityFunction(){
        assertThat(
            buildTokenSequence {
                func.squareStart.identifier("T").`,`.identifier("T").squareEnd.identifier("myFunc").`=`.`(`.
                identifier("T").identifier("myParam1").`)`.colon.identifier("T").`{`.identifier("myParam1").`}`.newLine
            },
            matchesAstChildren(
                "myFunc" declaredAs func(generic("T"), generic("T")) withValue (lambda()
                    returning generic("T")
                    withArgument ("myParam1" asType generic("T"))
                    andBody ret("myParam1".asIdentifier)
                )
            )
        )
    }

    @Test
    fun failOnCallExpressionWrongType() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.identifier("T").`,`.identifier("T").squareEnd.identifier("myFunc").`=`.`(`.identifier("T").
            identifier("myParam").`)`.colon.identifier("T").`{`.identifier("myParam").`}`.newLine.
            bool.identifier("x").`=`.number(1.0).identifier("myFunc").newLine
        })
        Assertions.assertThrows(UnexpectedTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }


    @Test
    fun failOnPassedFunctionWithGenerics() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.func.squareStart.number.squareEnd.`,`.number.squareEnd.identifier("myFunc").`=`.`(`.func.
            squareStart.number.squareEnd.identifier("myParam").`)`.colon.number.`{`.number(2.0).identifier("passFunc").`}`.newLine.
            func.squareStart.identifier("T").`,`.identifier("T").squareEnd.identifier("passFunc").`=`.`(`.identifier("T").
            identifier("value").`)`.colon.identifier("T").`{`.identifier("value").`}`.newLine.

            number.identifier("x").`=`.colon.identifier("passFunc").identifier("myFunc").newLine
        })

        Assertions.assertThrows(GenericPassedFunctionException::class.java) {
            ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree()
        }

    }

    @Test
    fun failOnCallDifferentTypesInArgs() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.identifier("T").`,`.identifier("T").`,`.identifier("T").squareEnd.identifier("myFunc").`=`.
            `(`.identifier("T").identifier("myParam").`,`.identifier("T").identifier("myParam2").`)`.colon.identifier("T").
            `{`.identifier("myParam").`}`.newLine.

            number.identifier("x").`=`.number(1.0).identifier("myFunc").bool(true).newLine

        })
        Assertions.assertThrows(UndeclaredError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }

    }

    @Test
    fun failOnTypesNotMatchingWithExpression() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.identifier("T").`,`.number.squareEnd.identifier("myFunc").`=`.`(`.identifier("T2").
            identifier("myParam1").`)`.colon.text.`{`.text("haha").`}`.newLine
        })

        Assertions.assertThrows(UnexpectedTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }


    @Test
    fun canPassFunction() {
        assertThat(
                buildTokenSequence {
                    `var`.identifier("myFunc").`=`.`(`.func.squareStart.identifier("T").squareEnd.identifier("myParam1").
                    `)`.colon.identifier("T").`{`.identifier("myParam1").`}`.newLine.

                    `var`.identifier("passFunc").`=`.`(`.`)`.colon.number.`{`.number(5.0).`}`.newLine.
                    colon.identifier("passFunc").identifier("myFunc").newLine
                },
                matchesAstChildren(
                        "myFunc" declaredAs func(generic("T"), func(generic("T"))) withValue (lambda()
                            returning generic("T")
                            withArgument ("myParam1" asType func(generic("T")))
                            andBody ret("myParam1".called())
                        ),
                        "passFunc" declaredAs func(num) withValue (lambda() returning num withBody ret(num(5))),
                        "myFunc" calledWith "passFunc".asIdentifier
                )
        )
    }
    @org.junit.jupiter.api.Test
    fun failGenericInList() {
        val lexer = DummyLexer(buildTokenSequence {
            list.squareStart.identifier("T").squareEnd.identifier("x").newLine
        })

        Assertions.assertThrows(UnexpectedTokenError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }

    }
    @org.junit.jupiter.api.Test
    fun failGenericAsPlainType() {
        val lexer = DummyLexer(buildTokenSequence {
            identifier("T").identifier("x").newLine
        })

        Assertions.assertThrows(UndeclaredError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }

    }
}
