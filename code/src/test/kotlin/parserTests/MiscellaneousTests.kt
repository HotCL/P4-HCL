package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.ImplicitTypeNotAllowed
import exceptions.UnexpectedTypeError
import lexer.Token
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import parser.Parser
import parser.AstNode
import kotlin.coroutines.experimental.buildSequence

class MiscellaneousTests {

    @Test
    fun canParseNumDeclaration() {
        assertThat(
                listOf(
                        Token.Type.Number,
                        Token.Identifier("myId"),
                        Token.SpecialChar.Equals,
                        Token.Literal.Number(5.0),
                        Token.SpecialChar.EndOfLine
                ),
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
        val lexer = DummyLexer(listOf(
                        Token.Type.Bool,
                        Token.Identifier("myId"),
                        Token.SpecialChar.Equals,
                        Token.Literal.Number(5.0),
                        Token.SpecialChar.EndOfLine
                ))

        Assertions.assertThrows(UnexpectedTypeError::class.java) { Parser(lexer).generateAbstractSyntaxTree() }

    }

    @Test
    fun canParseBool() {
        assertThat(
                listOf(
                        Token.Type.Bool,
                        Token.Identifier("myBool"),
                        Token.SpecialChar.Equals,
                        Token.Literal.Bool(true),
                        Token.SpecialChar.EndOfLine
                ),
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
                listOf(
                        Token.Type.Number,
                        Token.Identifier("myId"),
                        Token.SpecialChar.EndOfLine,
                        Token.Identifier("myId"),
                        Token.SpecialChar.Equals,
                        Token.Literal.Number(5.0),
                        Token.SpecialChar.EndOfLine
                ),
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
                listOf(
                        Token.Type.Var,
                        Token.Identifier("myId"),
                        Token.SpecialChar.Equals,
                        Token.Literal.Number(5.0),
                        Token.SpecialChar.EndOfLine
                ),
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
                listOf(
                        Token.Type.Var,
                        Token.Identifier("myList"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Literal.Number(5.0),
                        Token.SpecialChar.ListSeparator,
                        Token.Literal.Number(7.0),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.SpecialChar.EndOfLine
                ),
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
                listOf(
                        Token.Type.Var,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.Type.Number,
                        Token.Identifier("x"),
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.Colon,
                        Token.Type.Bool,
                        Token.SpecialChar.BlockStart,
                        Token.SpecialChar.BlockEnd,
                        Token.SpecialChar.EndOfLine
                ),
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
                                        listOf()
                                )
                        )
                )
        )
    }


    @Test
    fun canParseNumAssignmentWithIdentifier() {
        assertThat(
                listOf(
                        Token.Type.Number,
                        Token.Identifier("myNumber"),
                        Token.SpecialChar.Equals,
                        Token.Literal.Number(5.0),
                        Token.SpecialChar.EndOfLine,
                        Token.Type.Number,
                        Token.Identifier("myId"),
                        Token.SpecialChar.Equals,
                        Token.Identifier("myNumber"),
                        Token.SpecialChar.EndOfLine
                ),
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
        val lexer = DummyLexer(listOf(
                Token.Type.List,
                Token.SpecialChar.SquareBracketStart,
                Token.Type.Func,
                Token.SpecialChar.SquareBracketEnd,
                Token.Identifier("myFunc"),
                Token.SpecialChar.EndOfLine
        ))
        Assertions.assertThrows(ImplicitTypeNotAllowed::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }

    @Test
    fun failsOnLineStartsWithEquals() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.SpecialChar.Equals)
            yield(Token.SpecialChar.EndOfLine)
        })

        assertThrows(Exception::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }
}
