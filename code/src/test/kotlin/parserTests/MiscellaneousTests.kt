package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.ImplicitTypeNotAllowed
import exceptions.UnexpectedTypeError
import hclTestFramework.lexer.buildTokenSequence
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import parser.AstNode
import parser.ParserWithoutBuiltins

class MiscellaneousTests {

    @Test
    fun canParseNumDeclaration() {
        assertThat(
                buildTokenSequence {
                    number.identifier("myId").`=`.number(5.0).newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Number,
                                AstNode.Command.Expression.Value.Identifier("myId"),
                                AstNode.Command.Expression.Value.Literal.Number(5.0)
                        )
                )
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
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Bool,
                                AstNode.Command.Expression.Value.Identifier("myBool"),
                                AstNode.Command.Expression.Value.Literal.Bool(true)
                        )
                )
        )
    }

    @Test
    fun canParseNumAssignment() {
        assertThat(
                buildTokenSequence {
                    number.identifier("myId").newLine.identifier("myId").`=`.number(5.0).newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Number,
                                AstNode.Command.Expression.Value.Identifier("myId")
                        ),
                        AstNode.Command.Assignment(
                                AstNode.Command.Expression.Value.Identifier("myId"),
                                AstNode.Command.Expression.Value.Literal.Number(5.0)
                        )
                )
        )
    }

    @Test
    fun canParseVarAssignmentNumber() {
        assertThat(
                buildTokenSequence {
                    `var`.identifier("myId").`=`.number(5.0).newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Number,
                                AstNode.Command.Expression.Value.Identifier("myId"),
                                AstNode.Command.Expression.Value.Literal.Number(5.0)
                        )
                )
        )
    }

    @Test
    fun canParseVarAssignmentList() {
        assertThat(
                buildTokenSequence {
                    `var`.identifier("myList").`=`.squareStart.number(5.0).`,`.number(7.0).squareEnd.newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.List(AstNode.Type.Number),
                                AstNode.Command.Expression.Value.Identifier("myList"),
                                AstNode.Command.Expression.Value.Literal.List(
                                        listOf(
                                                AstNode.Command.Expression.Value.Literal.Number(5.0),
                                                AstNode.Command.Expression.Value.Literal.Number(7.0)
                                        )
                                )
                        )
                )
        )
    }

    @Test
    fun canParseVarAssignmentFunction() {
        assertThat(
                buildTokenSequence {
                    `var`.identifier("myFunc").`=`.`(`.number.identifier("x").`)`.colon.bool.`{`.bool(true).blockEnd.newLine
                },
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(
                                                AstNode.Type.Number
                                        ),
                                        AstNode.Type.Bool
                                ),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression(
                                        listOf(
                                                AstNode.ParameterDeclaration(
                                                    AstNode.Type.Number,
                                                    AstNode.Command.Expression.Value.Identifier("x")
                                                )
                                        ),
                                        AstNode.Type.Bool,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Literal.Bool(true)
                                                )
                                        ))
                                )
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
                        AstNode.Command.Declaration(
                                AstNode.Type.Number,
                                AstNode.Command.Expression.Value.Identifier("myNumber"),
                                AstNode.Command.Expression.Value.Literal.Number(5.0)
                        ),
                        AstNode.Command.Declaration(
                                AstNode.Type.Number,
                                AstNode.Command.Expression.Value.Identifier("myId"),
                                AstNode.Command.Expression.Value.Identifier("myNumber")
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun failsOnImplicitFuncAsParameter() {
        val lexer = DummyLexer(buildTokenSequence {
            list.squareStart.func.squareEnd.identifier("myFunc").newLine
        })
        Assertions.assertThrows(ImplicitTypeNotAllowed::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun failsOnLineStartsWithEquals() {
        val lexer = DummyLexer(buildTokenSequence {
            `=`.newLine
        })

        assertThrows(Exception::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }
}
