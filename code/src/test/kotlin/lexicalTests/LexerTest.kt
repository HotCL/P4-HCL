package lexicalTests

import lexer.Lexer
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import lexer.PositionalToken
import org.junit.jupiter.api.Assertions.assertTrue

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
        assertTrue(positionalToken.token.let { token -> validationExpression(token) })
        assertThat(positionalToken.lineIndex, equalTo(expectedLineIndex))
        assertThat(positionalToken.lineNumber, equalTo(ExpectedLineNumber))
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

    @org.junit.jupiter.api.Disabled
    @org.junit.jupiter.api.Test
    fun lexerTestLegalIdentifiers() {
        val lex = Lexer("hej var1 +- æøå ↑ ☺☻ ??? _this £50 *bold* 6lol")

        val tokens = lex.getTokenSequence().toList()

        tokens.dropLast(1).forEach{ assertTrue(it.token is lexer.Token.Identifier) }
        assertTrue(tokens.last().token is lexer.Token.SpecialChar.EndOfLine)
    }

    @org.junit.jupiter.api.Disabled
    @org.junit.jupiter.api.Test
    fun lexerTestIllegalIdentifiers() {
        val lex = Lexer("var = \"id \"id\" 'di 'di' [] { () true \\ , }")
        //Tokens: var, =, "id, "id", 'di, 'di', [, ], {, (, ), true, \, ",", ->, }
        val tokens = lex.getTokenSequence().toList()

        tokens.dropLast(1).forEach{ assertTrue(it.token !is lexer.Token.Identifier) }
        assertTrue(tokens.last().token is lexer.Token.SpecialChar.EndOfLine)
    }

    @org.junit.jupiter.api.Test
    fun lexerTestBooleanLiteralTokens() {
        val lex = Lexer("true false")

        val tokens = lex.getTokenSequence().toList()

        assertTrue(tokens[0].token is lexer.Token.Literal.Bool)
        assertTrue(tokens[1].token is lexer.Token.Literal.Bool)
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
                30, 0)
        assertPositionalToken(tokens[9],
                { token -> token is lexer.Token.SpecialChar.EndOfLine },
                34, 0)
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
        val lex = Lexer("x=y[0]")

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
    }

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

    @org.junit.jupiter.api.Test
    fun lexerTestGetLine() {
        val lexer = Lexer("Hej\nMed\nDig!")
        assertThat(lexer.inputLine(0), equalTo("Hej\n"))
        assertThat(lexer.inputLine(1), equalTo("Med\n"))
        assertThat(lexer.inputLine(2), equalTo("Dig!\n"))
    }
}
