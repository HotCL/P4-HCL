package lexicalTests

import lexer.Token
import org.junit.jupiter.api.Assertions.assertIterableEquals


class TokenTest {
    @org.junit.jupiter.api.Test
    fun testTokens() {
        val tokens = listOf("Identifier", "\"Text\"", "5", "true",
                                       "{", "}", "\n", "[", "]", "(", ")", ",", ";", "->", "=").map {
            when(it) {
                "Identifier" -> Token.Identifier(it)
                "\"Text\"" -> Token.Literal.Text(it.drop(1).dropLast(1))
                "5" -> Token.Literal.Number(it.toDouble())
                "true" -> Token.Literal.Bool(it.toBoolean())
                "{" -> Token.SpecialChar.BlockStart()
                "}" -> Token.SpecialChar.BlockEnd()
                "\n" -> Token.SpecialChar.EndOfLine()
                "[" -> Token.SpecialChar.SquareBracketStart()
                "]" -> Token.SpecialChar.SquareBracketEnd()
                "(" -> Token.SpecialChar.ParenthesesStart()
                ")" -> Token.SpecialChar.ParenthesesEnd()
                "," -> Token.SpecialChar.ListSeparator()
                ";" -> Token.SpecialChar.LineContinue()
                "->" -> Token.SpecialChar.Arrow()
                "=" -> Token.SpecialChar.Equals()
                else -> throw Exception("Unexpected lexeme '$it' in token test")
            }
        }
        assertIterableEquals(
            listOf(
                Token.Identifier("Identifier"),
                Token.Literal.Text("Text"),
                Token.Literal.Number(5.0),
                Token.Literal.Bool(true)
            ),
            tokens.take(4)
        )
    }
}
