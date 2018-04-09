package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.ImplicitTypeNotAllowed
import exceptions.InitializedFunctionParameterError
import exceptions.UnexpectedTokenError
import exceptions.WrongTokenTypeError
import lexer.Token
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.Parser
import parser.TreeNode
import kotlin.coroutines.experimental.buildSequence


class FunctionDeclarationTests {

    @Test
    fun canDeclareFunction() {
        assertThat(
                listOf(
                        Token.Type.Func(),
                        Token.SpecialChar.SquareBracketStart(),
                        Token.Type.Number(),
                        Token.SpecialChar.ListSeparator(),
                        Token.Type.Text(),
                        Token.SpecialChar.SquareBracketEnd(),
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.EndOfLine()
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Func.ExplicitFunc(listOf(TreeNode.Type.Number()), TreeNode.Type.Text()),
                                TreeNode.Command.Expression.Value.Identifier("myFunc")
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun failsOnLackingSeperator() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.Type.Text())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.EndOfLine())
        })
        Assertions.assertThrows(WrongTokenTypeError::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }

    @org.junit.jupiter.api.Test
    fun failsOnEmptyTypeSet() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.EndOfLine())
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }

    @org.junit.jupiter.api.Test
    fun parsesWithSingleTypeParameter() {
        assertThat(
                listOf(
                        Token.Type.Func(),
                        Token.SpecialChar.SquareBracketStart(),
                        Token.Type.Text(),
                        Token.SpecialChar.SquareBracketEnd(),
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.EndOfLine()
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(TreeNode.Type.Func.ExplicitFunc(listOf(), TreeNode.Type.Text()),
                                TreeNode.Command.Expression.Value.Identifier("myFunc")
                        )

                )
        )
    }

    @org.junit.jupiter.api.Test
    fun parsesWithImplicitFuncType() {
        assertThat(listOf(
                Token.Type.Func(),
                Token.Identifier("myFunc"),
                Token.SpecialChar.Equals(),
                Token.SpecialChar.ParenthesesStart(),
                Token.SpecialChar.ParenthesesEnd(),
                Token.SpecialChar.Colon(),
                Token.Type.None(),
                Token.SpecialChar.BlockStart(),
                Token.SpecialChar.BlockEnd(),
                Token.SpecialChar.EndOfLine()
        ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Func.ImplicitFunc(),
                                TreeNode.Command.Expression.Value.Identifier("myFunc"),
                                TreeNode.Command.Expression.LambdaExpression
                                (
                                        listOf(),
                                        TreeNode.Type.None(),
                                        listOf<TreeNode.Command.Expression.LambdaExpression>()
                                )

                        )

                )
        )
    }

    @org.junit.jupiter.api.Test
    fun failsOnTooManySeparators() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.Type.Func())
            yield(Token.SpecialChar.SquareBracketStart())
            yield(Token.Type.Number())
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.SpecialChar.ListSeparator())
            yield(Token.Type.Text())
            yield(Token.SpecialChar.SquareBracketEnd())
            yield(Token.Identifier("myFunc"))
            yield(Token.SpecialChar.EndOfLine())
        })
        Assertions.assertThrows(UnexpectedTokenError::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }
    //endregion FuncTypeDcl

    //region LambdaExpression
    @org.junit.jupiter.api.Test
    fun parsesFunctionWithTwoLambdaParameters() {
        assertThat(
                listOf(
                        Token.Type.Func(),
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals(),
                        Token.SpecialChar.ParenthesesStart(),
                        Token.Type.Number(),
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.ListSeparator(),
                        Token.Type.Text(),
                        Token.Identifier("myParam2"),
                        Token.SpecialChar.ParenthesesEnd(),
                        Token.SpecialChar.Colon(),
                        Token.Type.None(),
                        Token.SpecialChar.BlockStart(),
                        Token.SpecialChar.BlockEnd(),
                        Token.SpecialChar.EndOfLine()
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Func.ImplicitFunc(),
                                TreeNode.Command.Expression.Value.Identifier("myFunc"),
                                TreeNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                TreeNode.ParameterDeclaration
                                                (
                                                        TreeNode.Type.Number(),
                                                        TreeNode.Command.Expression.Value.Identifier("myParam1")
                                                ),
                                                TreeNode.ParameterDeclaration
                                                (
                                                        TreeNode.Type.Text(),
                                                        TreeNode.Command.Expression.Value.Identifier("myParam2")
                                                )
                                        ),
                                        TreeNode.Type.None(),
                                        listOf<TreeNode.Command.Expression.LambdaExpression>()
                                )

                        )

                )
        )
    }

    @org.junit.jupiter.api.Test
    fun parsesWithFuncParamAndLambdaParam() {
        assertThat(
                listOf(
                        Token.Type.Func(),
                        Token.SpecialChar.SquareBracketStart(),
                        Token.Type.Number(),
                        Token.SpecialChar.ListSeparator(),
                        Token.Type.None(),
                        Token.SpecialChar.SquareBracketEnd(),
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals(),
                        Token.SpecialChar.ParenthesesStart(),
                        Token.Type.Number(),
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.ParenthesesEnd(),
                        Token.SpecialChar.Colon(),
                        Token.Type.None(),
                        Token.SpecialChar.BlockStart(),
                        Token.SpecialChar.BlockEnd(),
                        Token.SpecialChar.EndOfLine()
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(TreeNode.Type.Func.ExplicitFunc(
                                listOf(TreeNode.Type.Number()), //InputTypes
                                TreeNode.Type.None() //ReturnType
                        ),
                                TreeNode.Command.Expression.Value.Identifier("myFunc"),
                                TreeNode.Command.Expression.LambdaExpression(
                                        listOf(
                                                TreeNode.ParameterDeclaration(
                                                        TreeNode.Type.Number(),
                                                        TreeNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ),
                                        TreeNode.Type.None(),
                                        listOf<TreeNode.Command.Expression.LambdaExpression>()
                                )

                        )
                )
        )
    }


    @Test
    fun failsOnAssignmentInLambdaParameterDefinition(){
        val lexer = DummyLexer(listOf(
                Token.Type.Func(),
                Token.Identifier("myFunc"),
                Token.SpecialChar.Equals(),
                Token.SpecialChar.ParenthesesStart(),
                Token.Type.Number(),
                Token.Identifier("myParam1"),
                Token.SpecialChar.Equals(),
                Token.Literal.Number("5"),
                Token.SpecialChar.ParenthesesEnd(),
                Token.SpecialChar.Colon(),
                Token.Type.None(),
                Token.SpecialChar.EndOfLine()
        ))
        Assertions.assertThrows(InitializedFunctionParameterError::class.java,
                { Parser(lexer).generateAbstractSyntaxTree() })

    }

    @org.junit.jupiter.api.Test
    fun parsesFuncWithBody() {
        assertThat(
                listOf(
                        Token.Type.Func(),
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals(),
                        Token.SpecialChar.ParenthesesStart(),
                        Token.SpecialChar.ParenthesesEnd(),
                        Token.SpecialChar.Colon(),
                        Token.Type.None(),
                        Token.SpecialChar.EndOfLine(),
                        Token.SpecialChar.BlockStart(),
                        Token.Type.Number(),
                        Token.Identifier("myNum"),
                        Token.SpecialChar.EndOfLine(),
                        Token.Type.Text(),
                        Token.Identifier("myText"),
                        Token.SpecialChar.EndOfLine(),
                        Token.SpecialChar.BlockEnd(),
                        Token.SpecialChar.EndOfLine()
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Func.ImplicitFunc(),
                                TreeNode.Command.Expression.Value.Identifier("myFunc"),
                                TreeNode.Command.Expression.LambdaExpression(
                                        listOf(),
                                        TreeNode.Type.None(),
                                        listOf(
                                                TreeNode.Command.Declaration(
                                                        TreeNode.Type.Number(),
                                                        TreeNode.Command.Expression.Value.Identifier("myNum"),
                                                        null
                                                ),
                                                TreeNode.Command.Declaration(
                                                        TreeNode.Type.Text(),
                                                        TreeNode.Command.Expression.Value.Identifier("myText"),
                                                        null
                                                )
                                        )

                                )
                        )
                )
        )
    }

}