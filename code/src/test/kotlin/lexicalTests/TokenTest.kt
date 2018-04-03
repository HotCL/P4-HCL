package lexicalTests

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import lexer.PositionalToken
import lexer.Token

class TokenTest {
    @org.junit.jupiter.api.Test
    fun testTokens() {
        val tokens = listOf("Identifier", "\"Text\"", "5", "true", "var", "num", "bool", "text", "func",
                                       "tuple", "list", "{", "}", "\n", "[", "]", "(", ")", ",", ";", "->", "=",
                                       "return").mapIndexed { index, lexeme ->
            when(lexeme) {
                "Identifier" -> Token.Identifier(lexeme)
                "\"Text\"" -> Token.Literal.Text(lexeme.drop(1).dropLast(1))
                "5"      -> Token.Literal.Number(lexeme)
                "true"   -> Token.Literal.Bool(true)
                "return" -> Token.Return()
                "tuple" -> Token.Type.Tuple()
                "bool" -> Token.Type.Bool()
                "text" -> Token.Type.Text()
                "func" -> Token.Type.Func()
                "list" -> Token.Type.List()
                "none" -> Token.Type.None()
                "var" -> Token.Type.Var()
                "num" -> Token.Type.Number()
                "\n" -> Token.SpecialChar.EndOfLine()
                "->" -> Token.SpecialChar.Arrow()
                "{" -> Token.SpecialChar.BlockStart()
                "}" -> Token.SpecialChar.BlockEnd()
                "[" -> Token.SpecialChar.SquareBracketStart()
                "]" -> Token.SpecialChar.SquareBracketEnd()
                "(" -> Token.SpecialChar.ParenthesesStart()
                ")" -> Token.SpecialChar.ParenthesesEnd()
                "," -> Token.SpecialChar.ListSeparator()
                ";" -> Token.SpecialChar.LineContinue()
                "=" -> Token.SpecialChar.Equals()
                else -> throw Exception("Unexpected lexeme '$lexeme' in token test")
            }.let {
                PositionalToken(it, index, 0)
            }
        }
        tokens.map { it.token }.forEach {
            when(it) {
                is Token.Identifier     -> assertThat(it.value, equalTo("Identifier"))
                is Token.Literal.Text   -> assertThat(it.value, equalTo("Text"))
                is Token.Literal.Number -> assertThat(it.value, equalTo(5.0))
                is Token.Literal.Bool   -> assertThat(it.value, equalTo(true))
            }
        }
        assertThat(tokens.sumBy { it.lineNumber }, equalTo((0 until tokens.size).sum()))
    }
}
