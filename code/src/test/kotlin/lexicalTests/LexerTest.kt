package lexicalTests

import lexer.Lexer
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.isA
import org.junit.jupiter.api.Assertions.assertTrue

class LexerTest {
    @org.junit.jupiter.api.Test
    fun lexerTestTokenGeneration() {
        val lex = Lexer("var x = 5 + 7")
        val tokensPositional = lex.tokens().toList()
        tokensPositional.onEach { println(it.token) }
        val tokens = tokensPositional.map { it.token }
        assertThat(tokens[0], isA<lexer.Token.Type.Var>())
        assertThat(tokensPositional[0].lineIndex, equalTo(0))
        assertThat(tokens[1], isA<lexer.Token.Identifier>())
        assertTrue(tokens[1].let { token -> token is lexer.Token.Identifier && token.value == "x" })
        assertThat(tokensPositional[1].lineIndex, equalTo(4))
        assertThat(tokens[2], isA<lexer.Token.SpecialChar.Equals>())
        assertThat(tokensPositional[2].lineIndex, equalTo(6))
        assertThat(tokens[3], isA<lexer.Token.Literal.Number>())
        assertTrue(tokens[3].let { token -> token is lexer.Token.Literal.Number && token.value == 5.0 })
        assertThat(tokensPositional[3].lineIndex, equalTo(8))
        assertThat(tokens[4], isA<lexer.Token.Identifier>())
        assertTrue(tokens[4].let { token -> token is lexer.Token.Identifier && token.value == "+" })
        assertThat(tokensPositional[4].lineIndex, equalTo(10))
        assertThat(tokens[5], isA<lexer.Token.Literal.Number>())
        assertTrue(tokens[5].let { token -> token is lexer.Token.Literal.Number && token.value == 7.0 })
        assertThat(tokensPositional[5].lineIndex, equalTo(12))
    }

    @org.junit.jupiter.api.Test
    fun lexerTestGetLine() {
        val lexer = Lexer("Hej\nMed\nDig!")
        assertThat(lexer.inputLine(0), equalTo("Hej"))
        assertThat(lexer.inputLine(1), equalTo("Med"))
        assertThat(lexer.inputLine(2), equalTo("Dig!"))
    }
}
