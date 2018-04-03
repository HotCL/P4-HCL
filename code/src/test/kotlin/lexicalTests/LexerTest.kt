package lexicalTests

import lexer.Lexer
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isA
import lexer.PositionalToken
import org.junit.jupiter.api.Assertions.assertTrue
import java.lang.reflect.Type

class LexerTest {
    @org.junit.jupiter.api.Test
    fun lexerTestTokenGeneration() {
        val lex = Lexer("var x = 5 + 7\nx = x times 10;")

        val tokensPositional = lex.getTokenSequence().toList()

        assertPositionalToken(tokensPositional[0],
                { token -> token is lexer.Token.Type.Var },
                0, 0)

        assertPositionalToken(tokensPositional[1],
                { token -> token is lexer.Token.Identifier && token.value == "x" },
                4, 0)

        assertPositionalToken(tokensPositional[2],
                { token -> token is lexer.Token.SpecialChar.Equals },
                6, 0)

        assertPositionalToken(tokensPositional[3],
                { token -> token is lexer.Token.Literal.Number && token.value == 5.0 },
                8, 0)

        assertPositionalToken(tokensPositional[4],
                {token -> token is lexer.Token.Identifier && token.value == "+" },
                10, 0)

        assertPositionalToken(tokensPositional[5],
                { token -> token is lexer.Token.Literal.Number && token.value == 7.0 },
                12, 0)

        assertPositionalToken(tokensPositional[6],
                { token -> token is lexer.Token.SpecialChar.EndOfLine },
                13, 0)

        assertPositionalToken(tokensPositional[7],
                { token -> token is lexer.Token.Identifier && token.value == "x" },
                0, 1)

        assertPositionalToken(tokensPositional[8],
                { token -> token is lexer.Token.SpecialChar.Equals },
                2, 1)

        assertPositionalToken(tokensPositional[9],
                { token -> token is lexer.Token.Identifier && token.value == "x" },
                4, 1)

        assertPositionalToken(tokensPositional[10],
                { token -> token is lexer.Token.Identifier && token.value == "times" },
                6, 1)

        assertPositionalToken(tokensPositional[11],
                { token -> token is lexer.Token.Literal.Number && token.value == 10.0 },
                12, 1)
        assertPositionalToken(tokensPositional[12],
                { token -> token is lexer.Token.SpecialChar.EndOfLine },
                14, 1)

    }

    private fun assertPositionalToken(positionalToken: PositionalToken, validationExpression: (lexer.Token) -> Boolean,
                                      expectedLineIndex:Int, ExpectedLineNumber:Int)
    {
        assertTrue(positionalToken.token.let { token -> validationExpression(token) },"Failed on: ${positionalToken.token}")
        assertThat(positionalToken.lineIndex, equalTo(expectedLineIndex))
        assertThat(positionalToken.lineNumber, equalTo(ExpectedLineNumber))
    }

    @org.junit.jupiter.api.Test
    fun lexerTestGetLine() {
        val lexer = Lexer("Hej\nMed\nDig!")
        assertThat(lexer.inputLine(0), equalTo("Hej\n"))
        assertThat(lexer.inputLine(1), equalTo("Med\n"))
        assertThat(lexer.inputLine(2), equalTo("Dig!\n"))
    }
}
