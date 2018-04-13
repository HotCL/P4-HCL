package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import exceptions.InitializedFunctionParameterError
import exceptions.UnexpectedTokenError
import exceptions.WrongTokenTypeError
import lexer.Token
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.Parser
import parser.AstNode
import kotlin.coroutines.experimental.buildSequence
import kotlin.test.assertEquals


class FunctionDeclarationTests {

    @Test
    fun canDeclareFunction() {
        assertThat(
                listOf(
                        Token.Type.Func,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.ListSeparator,
                        Token.Type.Text,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.EndOfLine
                ),
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(listOf(AstNode.Type.Number), AstNode.Type.Text),
                                AstNode.Command.Expression.Value.Identifier("myFunc")
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun failsOnLackingSeperator() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func)
            yield(Token.SpecialChar.SquareBracketStart)
            yield(Token.Type.Number)
            yield(Token.Type.Text)
            yield(Token.SpecialChar.SquareBracketEnd)
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.EndOfLine)
        })
        Assertions.assertThrows(WrongTokenTypeError::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }

    @org.junit.jupiter.api.Test
    fun failsOnEmptyTypeSet() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func)
            yield(Token.SpecialChar.SquareBracketStart)
            yield(Token.SpecialChar.SquareBracketEnd)
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.EndOfLine)
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }

    @org.junit.jupiter.api.Test
    fun parsesWithSingleTypeParameter() {
        assertThat(
                listOf(
                        Token.Type.Func,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Text,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.EndOfLine
                ),
                matchesAstChildren(
                        AstNode.Command.Declaration(AstNode.Type.Func.ExplicitFunc(listOf(), AstNode.Type.Text),
                                AstNode.Command.Expression.Value.Identifier("myFunc")
                        )

                )
        )
    }

    @org.junit.jupiter.api.Test
    fun parsesWithImplicitFuncType() {
        assertThat(listOf(
                Token.Type.Func,
                Token.Identifier("myFunc"),
                Token.SpecialChar.Equals,
                Token.SpecialChar.ParenthesesStart,
                Token.SpecialChar.ParenthesesEnd,
                Token.SpecialChar.Colon,
                Token.Type.None,
                Token.SpecialChar.BlockStart,
                Token.SpecialChar.BlockEnd,
                Token.SpecialChar.EndOfLine
        ),
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(listOf(), AstNode.Type.None),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf(),
                                        AstNode.Type.None,
                                        listOf<AstNode.Command.Expression.LambdaExpression>()
                                )

                        )

                )
        )
    }

    @org.junit.jupiter.api.Test
    fun failsOnTooManySeparators() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func)
            yield(Token.SpecialChar.SquareBracketStart)
            yield(Token.Type.Number)
            yield(Token.SpecialChar.ListSeparator)
            yield(Token.SpecialChar.ListSeparator)
            yield(Token.Type.Text)
            yield(Token.SpecialChar.SquareBracketEnd)
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.EndOfLine)
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }
    //endregion FuncTypeDcl

    //region LambdaExpression
    @org.junit.jupiter.api.Test
    fun parsesFunctionWithTwoLambdaParameters() {
        assertThat(
                listOf(
                        Token.Type.Func,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.Type.Number,
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.ListSeparator,
                        Token.Type.Text,
                        Token.Identifier("myParam2"),
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.Colon,
                        Token.Type.None,
                        Token.SpecialChar.BlockStart,
                        Token.SpecialChar.BlockEnd,
                        Token.SpecialChar.EndOfLine
                ),
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
                                        listOf<AstNode.Command.Expression.LambdaExpression>()
                                )

                        )

                )
        )
    }

    @org.junit.jupiter.api.Test
    fun parsesWithFuncParamAndLambdaParam() {
        assertThat(
                listOf(
                        Token.Type.Func,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.ListSeparator,
                        Token.Type.None,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.Type.Number,
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.Colon,
                        Token.Type.None,
                        Token.SpecialChar.BlockStart,
                        Token.SpecialChar.BlockEnd,
                        Token.SpecialChar.EndOfLine
                ),
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
                                        listOf<AstNode.Command.Expression.LambdaExpression>()
                                )

                        )
                )
        )
    }


    @Test
    fun failsOnAssignmentInLambdaParameterDefinition(){
        val lexer = DummyLexer(listOf(
                Token.Type.Func,
                Token.Identifier("myFunc"),
                Token.SpecialChar.Equals,
                Token.SpecialChar.ParenthesesStart,
                Token.Type.Number,
                Token.Identifier("myParam1"),
                Token.SpecialChar.Equals,
                Token.Literal.Number(5.0),
                Token.SpecialChar.ParenthesesEnd,
                Token.SpecialChar.Colon,
                Token.Type.None,
                Token.SpecialChar.EndOfLine
        ))
        Assertions.assertThrows(InitializedFunctionParameterError::class.java,
                { Parser(lexer).generateAbstractSyntaxTree() })

    }

    @Test
    fun failsOnOverloadingWithDifferentAmountOfParameters(){
        val lexer = DummyLexer(listOf(
                Token.Type.Func,
                Token.Identifier("myFunc"),
                Token.SpecialChar.Equals,
                Token.SpecialChar.ParenthesesStart,
                Token.Type.Number,
                Token.Identifier("myParam1"),
                Token.SpecialChar.ParenthesesEnd,
                Token.SpecialChar.Colon,
                Token.Type.None,
                Token.SpecialChar.BlockStart,
                Token.SpecialChar.BlockEnd,
                Token.SpecialChar.EndOfLine,
                Token.Type.Func,
                Token.Identifier("myFunc"),
                Token.SpecialChar.Equals,
                Token.SpecialChar.ParenthesesStart,
                Token.Type.Number,
                Token.Identifier("myParam1"),
                Token.SpecialChar.ListSeparator,
                Token.Type.Number,
                Token.Identifier("myParam1"),
                Token.SpecialChar.ParenthesesEnd,
                Token.SpecialChar.Colon,
                Token.Type.None,
                Token.SpecialChar.BlockStart,
                Token.SpecialChar.BlockEnd,
                Token.SpecialChar.EndOfLine
        ))

        val exception = Assertions.assertThrows(Exception::class.java,
                { Parser(lexer).generateAbstractSyntaxTree() })
        assertThat(exception.message, equalTo("Unable to overload with different amount of parameters!"))

    }

    @Test
    fun failsOnOverloadingWithSameParameters(){
        val lexer = DummyLexer(listOf(
                Token.Type.Func,
                Token.Identifier("myFunc"),
                Token.SpecialChar.Equals,
                Token.SpecialChar.ParenthesesStart,
                Token.Type.Number,
                Token.Identifier("myParam1"),
                Token.SpecialChar.ListSeparator,
                Token.Type.Number,
                Token.Identifier("myParam1"),
                Token.SpecialChar.ParenthesesEnd,
                Token.SpecialChar.Colon,
                Token.Type.Number,
                Token.SpecialChar.BlockStart,
                Token.SpecialChar.BlockEnd,
                Token.SpecialChar.EndOfLine,
                Token.Type.Func,
                Token.Identifier("myFunc"),
                Token.SpecialChar.Equals,
                Token.SpecialChar.ParenthesesStart,
                Token.Type.Number,
                Token.Identifier("myParam1"),
                Token.SpecialChar.ListSeparator,
                Token.Type.Number,
                Token.Identifier("myParam1"),
                Token.SpecialChar.ParenthesesEnd,
                Token.SpecialChar.Colon,
                Token.Type.Bool,
                Token.SpecialChar.BlockStart,
                Token.SpecialChar.BlockEnd,
                Token.SpecialChar.EndOfLine
        ))

        val exception = Assertions.assertThrows(Exception::class.java,
                { Parser(lexer).generateAbstractSyntaxTree() })
        assertThat(exception.message,
                    equalTo("Function of same name with these parameters has already been declared!"))

    }

    @org.junit.jupiter.api.Test
    fun parsesFuncWithBody() {
        assertThat(
                listOf(
                        Token.Type.Func,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.Colon,
                        Token.Type.None,
                        Token.SpecialChar.EndOfLine,
                        Token.SpecialChar.BlockStart,
                        Token.Type.Number,
                        Token.Identifier("myNum"),
                        Token.SpecialChar.EndOfLine,
                        Token.Type.Text,
                        Token.Identifier("myText"),
                        Token.SpecialChar.EndOfLine,
                        Token.SpecialChar.BlockEnd,
                        Token.SpecialChar.EndOfLine
                ),
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(listOf(), AstNode.Type.None),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression(
                                        listOf(),
                                        AstNode.Type.None,
                                        listOf(
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
                                        )

                                )
                        )
                )
        )
    }

}