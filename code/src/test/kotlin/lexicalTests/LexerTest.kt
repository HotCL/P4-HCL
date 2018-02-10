package lexicalTests

import com.natpryce.hamkrest.and
import lexer.Lexer
import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement

class LexerTest {
    @org.junit.jupiter.api.Test
    fun testLexer() {
        val lexer = Lexer()
        val tokens = lexer.lexStuff("This is a string to lex")
        assert.that(tokens, hasElement("lex") and hasElement("is"))
        assert.that(tokens.size, equalTo(6))
    }
}
