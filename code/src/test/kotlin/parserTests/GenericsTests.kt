package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.GenericPassedFunctionException
import exceptions.UndeclaredError
import exceptions.UnexpectedTokenError
import exceptions.UnexpectedTypeError
import hclTestFramework.lexer.buildTokenSequence
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
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.GenericType("T")),
                                        AstNode.Type.Text),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.GenericType("T"),
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ),
                                        AstNode.Type.Text,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Literal.Text("yeah")
                                                )
                                        ))
                                )
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
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.GenericType("T")),
                                        AstNode.Type.Text),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.GenericType("T"),
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ),
                                        AstNode.Type.Text,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Literal.Text("generics!")
                                                )
                                        ))
                                )
                        ),
                        AstNode.Command.Expression.FunctionCall(
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                listOf(AstNode.Command.Expression.Value.Literal.Number(5.0)
                                )
                        )
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
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(
                                                AstNode.Type.List(AstNode.Type.GenericType("T")),
                                                AstNode.Type.Tuple(listOf(
                                                        AstNode.Type.Number,
                                                        AstNode.Type.GenericType("T3"))),
                                                AstNode.Type.GenericType("T")
                                        ),
                                        AstNode.Type.GenericType("T")),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.List(AstNode.Type.GenericType("T")),
                                                        AstNode.Command.Expression.Value.Identifier("myParam")
                                                ),
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.Tuple(listOf(
                                                                AstNode.Type.Number,
                                                                AstNode.Type.GenericType("T3"))),
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                ),
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.GenericType("T"),
                                                        AstNode.Command.Expression.Value.Identifier("myT")
                                                )
                                        ),
                                        AstNode.Type.GenericType("T"),
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                    AstNode.Command.Expression.Value.Identifier("myT")
                                                )
                                        ))
                                )
                        ),
                        AstNode.Command.Declaration(
                                AstNode.Type.Number,
                                AstNode.Command.Expression.Value.Identifier("x"),
                                AstNode.Command.Expression.FunctionCall(
                                        AstNode.Command.Expression.Value.Identifier("myFunc"),
                                        listOf(
                                                AstNode.Command.Expression.Value.Literal.List(listOf(
                                                        AstNode.Command.Expression.Value.Literal.Number(1.0),
                                                        AstNode.Command.Expression.Value.Literal.Number(2.0)

                                                )),
                                                AstNode.Command.Expression.Value.Literal.Tuple(listOf(
                                                        AstNode.Command.Expression.Value.Literal.Number(1.0),
                                                        AstNode.Command.Expression.Value.Literal.Text("test")

                                                )),
                                                AstNode.Command.Expression.Value.Literal.Number(9.0)
                                        )
                                )
                        )
                )
        )
    }

    @Test
    fun identifyFunctionTest(){
        assertThat(
                buildTokenSequence {
                    func.squareStart.identifier("T").`,`.identifier("T").squareEnd.identifier("myFunc").`=`.`(`.
                    identifier("T").identifier("myParam1").`)`.colon.identifier("T").`{`.identifier("myParam1").`}`.newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.GenericType("T")),
                                        AstNode.Type.GenericType("T")),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.GenericType("T"),
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ),
                                        AstNode.Type.GenericType("T"),
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ))
                                )
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
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.Func.ExplicitFunc(
                                                listOf(),
                                                AstNode.Type.GenericType("T")
                                        )),
                                        AstNode.Type.GenericType("T")),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.Func.ExplicitFunc(
                                                                listOf(),
                                                                AstNode.Type.GenericType("T")
                                                        ),
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ),
                                        AstNode.Type.GenericType("T"),
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(AstNode.Command.Expression.FunctionCall(
                                                        AstNode.Command.Expression.Value.Identifier("myParam1"),
                                                        listOf()))
                                        ))
                                )
                        ),
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(),
                                        AstNode.Type.Number),
                                AstNode.Command.Expression.Value.Identifier("passFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
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
                                listOf(AstNode.Command.Expression.Value.Identifier("passFunc"))
                        )
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
