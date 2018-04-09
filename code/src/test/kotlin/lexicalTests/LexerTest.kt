package lexicalTests

import lexer.Lexer
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import lexer.PositionalToken
import exceptions.StringDoesntEndError
import org.junit.jupiter.api.Assertions.*

class LexerTest {
    @org.junit.jupiter.api.Test
    fun lexerTestTokenGeneration() {
        val lex = Lexer("var x = 5 + 7\nx = x times\\\n10.0;")

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
                { token -> token is lexer.Token.SpecialChar.LineContinue },
                11, 1)
        assertPositionalToken(tokensPositional[12],
                { token -> token is lexer.Token.SpecialChar.EndOfLine },
                12, 1)

        assertPositionalToken(tokensPositional[13],
                { token -> token is lexer.Token.Literal.Number && token.value == 10.0 },
                0, 2)
        assertPositionalToken(tokensPositional[14],
                { token -> token is lexer.Token.SpecialChar.EndOfLine },
                4, 2)

    }

    @org.junit.jupiter.api.Test
    fun lexerTestIgnoreComments() {
        val lex = Lexer("var x = 5# fem er et ret cool tal tbh\nx = 4")

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
                { token -> token is lexer.Token.SpecialChar.EndOfLine},
                9, 0)

        assertPositionalToken(tokensPositional[5],
                { token -> token is lexer.Token.Identifier && token.value == "x" },
                0, 1)

        assertPositionalToken(tokensPositional[6],
                { token -> token is lexer.Token.SpecialChar.Equals },
                2, 1)

        assertPositionalToken(tokensPositional[7],
                { token -> token is lexer.Token.Literal.Number && token.value == 4.0 },
                4, 1)

        assertEquals(9, tokensPositional.count())
    }

    @org.junit.jupiter.api.Test
    fun lexerTestTypeDeclarationTokens() {
        val lex = Lexer("var none num func tuple list bool txt")

        val tokens = lex.getTokenSequence().toList()

        assertTrue(tokens[0].token is lexer.Token.Type.Var)
        assertTrue(tokens[1].token is lexer.Token.Type.None)
        assertTrue(tokens[2].token is lexer.Token.Type.Number)
        assertTrue(tokens[3].token is lexer.Token.Type.Func)
        assertTrue(tokens[4].token is lexer.Token.Type.Tuple)
        assertTrue(tokens[5].token is lexer.Token.Type.List)
        assertTrue(tokens[6].token is lexer.Token.Type.Bool)
        assertTrue(tokens[7].token is lexer.Token.Type.Text)
    }

    @org.junit.jupiter.api.Test
    fun lexerTestLegalIdentifiers() {
        val raw_input = "hej var1 +- æøå ↑ ☺☻ ??? _this £50 *bold* 6lol"
        val raw_tokens = raw_input.split(' ')
        val lex = Lexer(raw_input)

        val tokens = lex.getTokenSequence().toList()

        tokens.dropLast(1).forEachIndexed {index, posToken ->  assertTrue(posToken.token.let { it is lexer.Token.Identifier &&
                it.value == raw_tokens[index]},
                "${posToken.token} is not ${raw_tokens[index]}") }
        assertTrue(tokens.last().token is lexer.Token.SpecialChar.EndOfLine)
    }

    @org.junit.jupiter.api.Test
    fun lexerTestIllegalIdentifiers() {
        val lex = Lexer("var = \"id\" 'di 'di' [] { () true \\ , }")
        //Tokens: var, =, "id, "id", 'di, 'di', [, ], {, (, ), true, \, ",", ->, }
        val tokens = lex.getTokenSequence().toList()

        tokens.dropLast(1).forEach{ assertTrue(it.token !is lexer.Token.Identifier) }
        assertTrue(tokens.last().token is lexer.Token.SpecialChar.EndOfLine)
    }

    @org.junit.jupiter.api.Test
    fun lexerTestBooleanLiteralTokens() {
        val lex = Lexer("true false")

        val tokens = lex.getTokenSequence().toList()

        assertTrue(tokens[0].token.let { it is lexer.Token.Literal.Bool && it.value })
        assertTrue(tokens[1].token.let { it is lexer.Token.Literal.Bool && !it.value })
    }

    @org.junit.jupiter.api.Test
    fun lexerTestFunctionDeclaration() {
        val lex = Lexer("func print = (txt message) : none\n{ }")

        val tokens = lex.getTokenSequence().toList()

        assertPositionalToken(tokens[0],
                { token -> token is lexer.Token.Type.Func },
                0, 0)
        assertPositionalToken(tokens[1],
                { token -> token is lexer.Token.Identifier && token.value == "print" },
                5, 0)
        assertPositionalToken(tokens[2],
                { token -> token is lexer.Token.SpecialChar.Equals },
                11, 0)
        assertPositionalToken(tokens[3],
                { token -> token is lexer.Token.SpecialChar.ParenthesesStart },
                13, 0)
        assertPositionalToken(tokens[4],
                { token -> token is lexer.Token.Type.Text },
                14, 0)
        assertPositionalToken(tokens[5],
                { token -> token is lexer.Token.Identifier && token.value == "message" },
                18, 0)
        assertPositionalToken(tokens[6],
                { token -> token is lexer.Token.SpecialChar.ParenthesesEnd },
                25, 0)
        assertPositionalToken(tokens[7],
                { token -> token is lexer.Token.SpecialChar.Colon },
                27, 0)
        assertPositionalToken(tokens[8],
                { token -> token is lexer.Token.Type.None },
                29, 0)
        assertPositionalToken(tokens[9],
                { token -> token is lexer.Token.SpecialChar.EndOfLine },
                33, 0)
        assertPositionalToken(tokens[10],
                { token -> token is lexer.Token.SpecialChar.BlockStart },
                0, 1)
        assertPositionalToken(tokens[11],
                { token -> token is lexer.Token.SpecialChar.BlockEnd },
                2, 1)
        assertPositionalToken(tokens[12],
                { token -> token is lexer.Token.SpecialChar.EndOfLine },
                3, 1)
    }

    @org.junit.jupiter.api.Test
    fun lexerTestDoesntRequireWhitespaces() {
        val lex = Lexer("x=y[0]txt")

        val tokens = lex.getTokenSequence().toList()

        assertPositionalToken(tokens[0],
                { token -> token is lexer.Token.Identifier && token.value == "x" },
                0, 0)

        assertPositionalToken(tokens[1],
                { token -> token is lexer.Token.SpecialChar.Equals },
                1, 0)

        assertPositionalToken(tokens[2],
                { token -> token is lexer.Token.Identifier && token.value == "y" },
                2, 0)

        assertPositionalToken(tokens[3],
                { token -> token is lexer.Token.SpecialChar.SquareBracketStart },
                3, 0)

        assertPositionalToken(tokens[4],
                { token -> token is lexer.Token.Literal.Number && token.value == 0.0 },
                4, 0)

        assertPositionalToken(tokens[5],
                { token -> token is lexer.Token.SpecialChar.SquareBracketEnd },
                5, 0)
        assertPositionalToken(tokens[6],
                { token -> token is lexer.Token.Type.Text },
                6, 0)
    }

    @org.junit.jupiter.api.Test
    fun testAllSpecialChars() {
        val lex = Lexer("={}()[]\\\n;:,")
        val tokens = lex.getTokenSequence().toList()
        assertTrue(tokens[0].token is lexer.Token.SpecialChar.Equals)
        assertTrue(tokens[1].token is lexer.Token.SpecialChar.BlockStart)
        assertTrue(tokens[2].token is lexer.Token.SpecialChar.BlockEnd)
        assertTrue(tokens[3].token is lexer.Token.SpecialChar.ParenthesesStart)
        assertTrue(tokens[4].token is lexer.Token.SpecialChar.ParenthesesEnd)
        assertTrue(tokens[5].token is lexer.Token.SpecialChar.SquareBracketStart)
        assertTrue(tokens[6].token is lexer.Token.SpecialChar.SquareBracketEnd)
        assertTrue(tokens[7].token is lexer.Token.SpecialChar.LineContinue)
        assertTrue(tokens[8].token is lexer.Token.SpecialChar.EndOfLine)
        assertTrue(tokens[9].token is lexer.Token.SpecialChar.EndOfLine)
        assertTrue(tokens[10].token is lexer.Token.SpecialChar.Colon)
        assertTrue(tokens[11].token is lexer.Token.SpecialChar.ListSeparator)
        assertTrue(tokens[9].token is lexer.Token.SpecialChar.EndOfLine)
        assertEquals(tokens.count(),13)

    }

    // TODO figure out if this is irrelevant - this seems a lot like the lexerTestFunctionDeclaration function.
    @org.junit.jupiter.api.Test
    fun lexerTestInlineLambdaExpression() {
        val lex = Lexer("x = 1 to 10 map {value * 2}")

        val tokens = lex.getTokenSequence().toList()

        assertPositionalToken(tokens[0],
                { token -> token is lexer.Token.Identifier && token.value == "x" },
                0, 0)
        assertPositionalToken(tokens[1],
                { token -> token is lexer.Token.SpecialChar.Equals },
                2, 0)
        assertPositionalToken(tokens[2],
                { token -> token is lexer.Token.Literal.Number && token.value == 1.0 },
                4, 0)
        assertPositionalToken(tokens[3],
                { token -> token is lexer.Token.Identifier && token.value == "to" },
                6, 0)
        assertPositionalToken(tokens[4],
                { token -> token is lexer.Token.Literal.Number && token.value == 10.0 },
                9, 0)
        assertPositionalToken(tokens[5],
                { token -> token is lexer.Token.Identifier && token.value == "map" },
                12, 0)
        assertPositionalToken(tokens[6],
                { token -> token is lexer.Token.SpecialChar.BlockStart },
                16, 0)
        assertPositionalToken(tokens[7],
                { token -> token is lexer.Token.Identifier && token.value == "value" },
                17, 0)
        assertPositionalToken(tokens[8],
                { token -> token is lexer.Token.Identifier && token.value == "*" },
                23, 0)
        assertPositionalToken(tokens[9],
                { token -> token is lexer.Token.Literal.Number && token.value == 2.0 },
                25, 0)
        assertPositionalToken(tokens[10],
                { token -> token is lexer.Token.SpecialChar.BlockEnd },
                26, 0)
        assertPositionalToken(tokens[11],
                { token -> token is lexer.Token.SpecialChar.EndOfLine },
                27, 0)
    }

    private fun assertPositionalToken(positionalToken: PositionalToken, validationExpression: (lexer.Token) -> Boolean,
                                      expectedLineIndex:Int, ExpectedLineNumber:Int)
    {
        assertTrue(positionalToken.token.let { token -> validationExpression(token) }, "Failed on: ${positionalToken.token}")
        assertThat(positionalToken.lineIndex, equalTo(expectedLineIndex))
        assertThat(positionalToken.lineNumber, equalTo(ExpectedLineNumber))
    }

    @org.junit.jupiter.api.Test
    fun lexerUnfinishedStringFails() {
        assertThrows(StringDoesntEndError::class.java, {Lexer("\"hej").getTokenSequence().toList()})
        assertThrows(StringDoesntEndError::class.java, {Lexer("'hej").getTokenSequence().toList()})
    }

    @org.junit.jupiter.api.Test
    fun multiWordString() {
        val rawString = "hej med dig"
        val tokensPositional = Lexer("\"$rawString\"").getTokenSequence().toList()

        assertPositionalToken(tokensPositional[0],
                { token -> token is lexer.Token.Literal.Text && token.value == rawString},
                0, 0)
        assertEquals(2, tokensPositional.count())
    }

    @org.junit.jupiter.api.Test
    fun multiWordStringEscapedQuotes() {
        val rawString = "hej \\\"med\\\" dig"
        val tokensPositional = Lexer("\"$rawString\"").getTokenSequence().toList()

        assertPositionalToken(tokensPositional[0],
                { token -> token is lexer.Token.Literal.Text && token.value == rawString},
                0, 0)
        assertEquals(2, tokensPositional.count())
    }


    @org.junit.jupiter.api.Test
    fun lexerTestGetLine() {
        val lexer = Lexer("Hej\nMed\nDig!")
        assertThat(lexer.inputLine(0), equalTo("Hej\n"))
        assertThat(lexer.inputLine(1), equalTo("Med\n"))
        assertThat(lexer.inputLine(2), equalTo("Dig!\n"))
    }

    @org.junit.jupiter.api.Test
    fun lexerTestTabTest() {
        val lex = Lexer("x=\t5")

        val tokens = lex.getTokenSequence().toList()

        assertPositionalToken(tokens[0],
                { token -> token is lexer.Token.Identifier && token.value == "x" },
                0, 0)

        assertPositionalToken(tokens[1],
                { token -> token is lexer.Token.SpecialChar.Equals },
                1, 0)

        assertPositionalToken(tokens[2],
                { token -> token is lexer.Token.Literal.Number && token.value == 5.0 },
                3, 0)

    }
}
