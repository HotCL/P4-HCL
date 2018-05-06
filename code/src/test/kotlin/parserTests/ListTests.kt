package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.UnexpectedTokenError
import exceptions.WrongTokenTypeError
import hclTestFramework.lexer.buildTokenSequence
import lexer.Token
import org.junit.jupiter.api.Assertions
import parser.Parser
import parser.AstNode
import parser.ParserWithoutBuiltins
import kotlin.coroutines.experimental.buildSequence

class ListTests {

    @org.junit.jupiter.api.Test
    fun parseListType() {
        assertThat(
            buildTokenSequence {
                list.
                squareStart.
                number.
                squareEnd.
                identifier("myList").
                newLine
            },
            matchesAstChildren(
                    AstNode.Command.Declaration(
                            AstNode.Type.List(AstNode.Type.Number),
                            AstNode.Command.Expression.Value.Identifier("myList")
                    )
            )
        )
    }

    @org.junit.jupiter.api.Test
    fun parseListDeclarationWithAssignment() {
        assertThat(
                buildTokenSequence {
                    list.
                    squareStart.
                    number.
                    squareEnd.
                    identifier("MyList").
                    `=`.
                    squareStart.
                    number(5.0).
                    `,`.
                    number(10.0).
                    squareEnd.
                    newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.List(AstNode.Type.Number),
                                AstNode.Command.Expression.Value.Identifier("MyList"),
                                AstNode.Command.Expression.Value.Literal.List(
                                        listOf(
                                                AstNode.Command.Expression.Value.Literal.Number(5.0),
                                                AstNode.Command.Expression.Value.Literal.Number(10.0)
                                        )
                                )
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun parseListWithLists() {
        assertThat(
                buildTokenSequence {
                    list.
                    squareStart.
                    number.
                    squareEnd.
                    identifier("myNumberList").
                    `=`.
                    squareStart.
                    number(10.0).
                    squareEnd.
                    newLine.
                    list.
                    squareStart.
                    list.
                    squareStart.
                    number.
                    squareEnd.
                    squareEnd.
                    identifier("myList").
                    `=`.
                    squareStart.
                    identifier("myNumberList").
                    squareEnd.
                    newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.List(AstNode.Type.Number),
                                AstNode.Command.Expression.Value.Identifier("myNumberList"),
                                AstNode.Command.Expression.Value.Literal.List(
                                        listOf(
                                                AstNode.Command.Expression.Value.Literal.Number(10.0)
                                        )
                                )
                        ),
                        AstNode.Command.Declaration(
                                AstNode.Type.List(AstNode.Type.List(AstNode.Type.Number)),
                                AstNode.Command.Expression.Value.Identifier("myList"),
                                AstNode.Command.Expression.Value.Literal.List(
                                        listOf(
                                                AstNode.Command.Expression.Value.Identifier("myNumberList")
                                        )
                                )
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun parseListWithFuncCall() {
        assertThat(
                buildTokenSequence {
                    func.
                    squareStart.
                    number.
                    squareEnd.
                    identifier("myFunc").
                    `=`.
                    `(`.
                    `)`.
                    colon.
                    number.
                    `{`.
                    number(5.0).
                    `}`.
                    newLine.
                    list.
                    squareStart.
                    number.
                    squareEnd.
                    identifier("myList").
                    `=`.
                    squareStart.
                    identifier("myFunc").
                    squareEnd.
                    newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(listOf(), AstNode.Type.Number),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression(listOf(), AstNode.Type.Number,
                                        AstNode.Command.Expression.LambdaBody(listOf(AstNode.Command.Return(
                                                AstNode.Command.Expression.Value.Literal.Number(5.0)
                                        )))
                        )),
                        AstNode.Command.Declaration(
                                AstNode.Type.List(AstNode.Type.Number),
                                AstNode.Command.Expression.Value.Identifier("myList"),
                                AstNode.Command.Expression.Value.Literal.List(
                                        listOf(
                                                AstNode.Command.Expression.FunctionCall(
                                                        AstNode.Command.Expression.Value.Identifier("myFunc"),
                                                        listOf()
                                                        )
                                        )
                                )
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun failsOnListAssignmentWithTooFewSeparators() {
        val lexer = DummyLexer(buildTokenSequence {
            list.
            squareStart.
            number.
            squareEnd.
            identifier("MyList").
            `=`.
            squareStart.
            number(5.0).
            number(10.0).
            squareEnd.
            newLine
        })
        Assertions.assertThrows(WrongTokenTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }

    }

    @org.junit.jupiter.api.Test
    fun failsOnListAssignmentWithTooManySeparators() {
        val lexer = DummyLexer(buildTokenSequence {
            list.
            squareStart.
            number.
            squareEnd.
            identifier("MyList").
            `=`.
            squareStart.
            number(5.0).
            `,`.
            `,`.
            number(10.0).
            squareEnd.
            newLine
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }
}
