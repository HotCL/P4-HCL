package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import exceptions.InitializedFunctionParameterError
import exceptions.UnexpectedTokenError
import exceptions.WrongTokenTypeError
import lexer.Token
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import parser.Parser
import parser.AstNode
import kotlin.coroutines.experimental.buildSequence
import kotlin.test.assertEquals


class  InlineLambdaTests{

    @Test
    @Disabled
    fun canDeclareFunction() {
        assertThat(
                listOf(
                        Token.Type.Func,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Func,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.ListSeparator,
                        Token.Type.Text,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.SpecialChar.ListSeparator,
                        Token.Type.Text,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.Type.Func,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.ListSeparator,
                        Token.Type.Text,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.Colon,
                        Token.Type.Text,
                        Token.SpecialChar.BlockStart,
                        Token.SpecialChar.BlockEnd,
                        Token.SpecialChar.EndOfLine

                        /* TODO write rest of test */
                ),
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(listOf(AstNode.Type.Number), AstNode.Type.Text),
                                AstNode.Command.Expression.Value.Identifier("myFunc")
                        )
                )
        )
    }
}