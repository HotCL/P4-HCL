package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.*
import hclTestFramework.lexer.buildTokenSequence
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.AstNode
import parser.ParserWithoutBuiltins


class FunctionDeclarationTests {

    @Test
    fun canDeclareFunction() {
        assertThat(
                buildTokenSequence {
                        func.squareStart.number.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.number.
                        identifier("myParam1").`)`.colon.text.`{`.text("hey").`}`.newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(listOf(AstNode.Type.Number), AstNode.Type.Text),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.Number,
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ),
                                        AstNode.Type.Text,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Literal.Text("hey")
                                                )
                                        ))
                                )
                        )
                )
        )
    }

    @Test
    fun failOnTypesNotMatchingWithExpression() {
        val lexer = DummyLexer(buildTokenSequence {
                func.squareStart.
                number.
                `,`.
                text.
                squareEnd.
                identifier("myFunc").
                `=`.
                `(`.
                text.
                identifier("myParam1").
                `)`.
                colon.
                text.
                `{`.
                text("thomas").
                `}`.
                newLine
        })
        Assertions.assertThrows(UnexpectedTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun failOnReturnTypeNotMatchingWithExpression() {
        val lexer = DummyLexer(buildTokenSequence {
                func.
                squareStart.
                number.
                `,`.
                text.
                squareEnd.
                identifier("myFunc").
                `=`.
                `(`.
                number.
                identifier("myParam1").
                `)`.
                colon.
                number.
                `{`.
                number(3.0).
                `}`.
                newLine
        })
        Assertions.assertThrows(UnexpectedTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun failOnFuncSetToNonFunc() {
        val lexer = DummyLexer(buildTokenSequence {
                func.
                squareStart.
                number.
                `,`.
                text.
                squareEnd.
                identifier("myFunc").
                `=`.
                number(5.0).
                newLine
        })
        Assertions.assertThrows(UnexpectedTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }


    @Test
    fun failOnUndeclaredFunction() {
        val lexer = DummyLexer(buildTokenSequence {
                func.
                squareStart.
                number.
                `,`.
                text.
                squareEnd.
                identifier("toString").
                newLine
        })
        Assertions.assertThrows(Exception::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }


    @org.junit.jupiter.api.Test
    fun failsOnLackingSeperator() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.text.squareEnd.identifier("myFunc").newLine
        })
        Assertions.assertThrows(WrongTokenTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @org.junit.jupiter.api.Test
    fun failsOnEmptyTypeSet() {
        val lexer = DummyLexer(buildTokenSequence {
                func.squareStart.squareEnd.identifier("myFunc")
                //should fail before this token
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @org.junit.jupiter.api.Test
    fun parsesWithSingleTypeParameter() {
        assertThat(
                buildTokenSequence {
                    func.squareStart.text.squareEnd.identifier("myFunc").`=`.`(`.`)`.colon.text.`{`.text("wee").`}`.newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(AstNode.Type.Func.ExplicitFunc(listOf(), AstNode.Type.Text),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf(),
                                        AstNode.Type.Text,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Literal.Text("wee")
                                                )
                                        ))
                                )
                        )

                )
        )
    }
/*
    @Test
    fun failsOnAssignmentInLambdaParameterDefinition(){
        val lexer = DummyLexer(buildTokenSequence {
                Type.List,
                squareStart,
                number,
                squareEnd,
                identifier("ret+2"),
                `=`,
                squareStart,
                number(),
                squareEnd,
                newLine,
                Type.List,
                squareStart,
                Type.List,
                squareStart,
                number,
                squareEnd,
                squareEnd,
                Identifier,
                `=`,
                squareStart,
                Identifier,
                squareEnd,
                newLine
        ))
        Assertions.assertThrows(InitializedFunctionParameterError::class.java,
                { Parser(lexer).generateAbstractSyntaxTree() })

    }*/

    @org.junit.jupiter.api.Test
    fun parsesWithImplicitFuncType() {
        assertThat(buildTokenSequence {
            func.identifier("myFunc").`=`.`(`.`)`.colon.none.`{`.`}`.newLine
        },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(listOf(), AstNode.Type.None),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf(),
                                        AstNode.Type.None,
                                        AstNode.Command.Expression.LambdaBody(listOf())
                                )

                        )

                )
        )
    }

    @Test
    fun failsOnTooManySeparators() {
        val lexer = DummyLexer(buildTokenSequence {
            func.squareStart.number.`,`.`,`.text.squareEnd.identifier("myFunc").newLine
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }
    //endregion FuncTypeDcl

    //region LambdaExpression
    @org.junit.jupiter.api.Test
    fun parsesFunctionWithTwoLambdaParameters() {
        assertThat(
                buildTokenSequence {
                        func.
                        identifier("myFunc").
                        `=`.
                        `(`.
                        number.
                        identifier("myParam1").
                        `,`.
                        text.
                        identifier("myParam2").
                        `)`.
                        colon.
                        none.
                        `{`.
                        `}`.
                        newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(
                                                AstNode.Type.Number,
                                                AstNode.Type.Text
                                        ),
                                        AstNode.Type.None
                                ),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.Number,
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                ),
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.Text,
                                                        AstNode.Command.Expression.Value.Identifier("myParam2")
                                                )
                                        ),
                                        AstNode.Type.None,
                                        AstNode.Command.Expression.LambdaBody(listOf())
                                )

                        )

                )
        )
    }

    @org.junit.jupiter.api.Test
    fun parsesWithFuncParamAndLambdaParam() {
        assertThat(
                buildTokenSequence {
                        func.
                        squareStart.
                        number.
                        `,`.
                        none.
                        squareEnd.
                        identifier("myFunc").
                        `=`.
                        `(`.
                        number.
                        identifier("myParam1").
                        `)`.
                        colon.
                        none.
                        `{`.
                        `}`.
                        newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(AstNode.Type.Func.ExplicitFunc(
                                listOf(AstNode.Type.Number), //InputTypes
                                AstNode.Type.None //ReturnType
                        ),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression(
                                        listOf(
                                                AstNode.ParameterDeclaration(
                                                        AstNode.Type.Number,
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ),
                                        AstNode.Type.None,
                                        AstNode.Command.Expression.LambdaBody(listOf ())
                                )

                        )
                )
        )
    }


    @Test
    fun failsOnAssignmentInLambdaParameterDefinition(){
        val lexer = DummyLexer(buildTokenSequence {
            func.identifier("myFunc").`=`.`(`.number.identifier("myParam1").`=`.number(5.0).`)`.colon.none.newLine
        })
        Assertions.assertThrows(InitializedFunctionParameterError::class.java,
                { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() })

    }

    @Test
    fun canDeclareFunctionWithFunctionAsParameter() {
        assertThat(
                buildTokenSequence {
                    func.squareStart.func.squareStart.text.squareEnd.`,`.text.squareEnd.identifier("myFunc").`=`.`(`.
                    func.squareStart.text.squareEnd.identifier("myParam").`)`.colon.text.`{`.text("HEY").`}`.newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.Func.ExplicitFunc(listOf(),AstNode.Type.Text)),
                                        AstNode.Type.Text
                                ),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression(
                                        listOf (
                                                AstNode.ParameterDeclaration(
                                                        AstNode.Type.Func.ExplicitFunc(listOf(),AstNode.Type.Text),
                                                        AstNode.Command.Expression.Value.Identifier("myParam")
                                                )
                                        ),
                                        AstNode.Type.Text,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                    AstNode.Command.Expression.Value.Literal.Text("HEY"))
                                                )
                                        )
                                )

                        )
                )
        )
    }

    @Test
    fun failsOnOverloadingWithDifferentAmountOfParameters(){
        val lexer = DummyLexer(buildTokenSequence {
                func.
                identifier("myFunc").
                `=`.
                `(`.
                number.
                identifier("myParam1").
                `)`.
                colon.
                none.
                `{`.
                `}`.
                newLine.
                func.
                identifier("myFunc").
                `=`.
                `(`.
                number.
                identifier("myParam1").
                `,`.
                number.
                identifier("myParam1").
                `)`.
                colon.
                none.
                `{`.
                `}`.
                newLine
        })

        Assertions.assertThrows(OverloadWithDifferentAmountOfArgumentsException::class.java,
                { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() })
    }

    @Test
    fun failsOnOverloadingWithSameParameters(){
        val lexer = DummyLexer(buildTokenSequence {
            func.identifier("myFunc").`=`.`(`.number.identifier("myParam1").`,`.number.identifier("myParam1").`)`.colon.
            number.`{`.number(5.0).`}`.newLine.func.identifier("myFunc").`=`.`(`.number.identifier("myParam1").`,`.
            number.identifier("myParam1").`)`.colon.bool.`{`.bool(true).`}`.newLine
        })

        Assertions.assertThrows(AlreadyDeclaredException::class.java,
                { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() })

    }

    @org.junit.jupiter.api.Test
    fun parsesFuncWithBody() {
        assertThat(
                buildTokenSequence {
                    func.identifier("myFunc").`=`.`(`.`)`.colon.none.newLine.`{`.number.identifier("myNum").newLine.
                    text.identifier("myText").newLine.`}`.newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(listOf(), AstNode.Type.None),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression(
                                        listOf(),
                                        AstNode.Type.None,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Declaration(
                                                        AstNode.Type.Number,
                                                        AstNode.Command.Expression.Value.Identifier("myNum"),
                                                        null
                                                ),
                                                AstNode.Command.Declaration(
                                                        AstNode.Type.Text,
                                                        AstNode.Command.Expression.Value.Identifier("myText"),
                                                        null
                                                )
                                        ))

                                )
                        )
                )
        )
    }

}
