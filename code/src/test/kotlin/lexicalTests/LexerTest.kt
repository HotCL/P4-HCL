package lexicalTests

import lexer.Lexer
import lexer.PositionalToken
import hclTestFramework.lexer.buildTokenSequence
import lexer.Token
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.*

private fun lexerTestTokenGeneration() = TestData(
    "var x = 5 + 7\nx = x times 10.0;",
    buildTokenSequence {
        `var`.identifier("x").`=`.number(5.0).identifier("+").number(7.0).newLine.
                identifier("x").`=`.identifier("x").identifier("times").number(10.0).newLine
    },
    listOf (
            listOf(0, 4, 6, 8, 10, 12, 13),
            listOf(0, 2, 4, 6, 12, 16)
    )
)

private fun lexerTestIgnoreComments() = TestData(
        "var x = 5# fem er et ret cool tal tbh\nx = 4",
        buildTokenSequence {
            `var`.identifier("x").`=`.number(5.0).newLine.identifier("x").`=`.number(4.0)
        },
        listOf (
                listOf(0, 4, 6, 8, 9),
                listOf(0, 2, 4)
        )
)

private fun lexerTestTypeDeclarationTokens() = TestData(
        "var none num func tuple list bool txt",
        buildTokenSequence { `var`.none.number.func.tuple.list.bool.text },
        listOf (listOf(0, 4, 9, 13, 18, 24, 29, 34))
)



private fun lexerTestLegalIdentifiers() = TestData(
    "hej var1 +- æøå ↑ ☺☻ ??? _this £50 *bold* 6lol",
    "hej var1 +- æøå ↑ ☺☻ ??? _this £50 *bold* 6lol".split(' ').map { Token.Identifier(it) },
    listOf ("hej var1 +- æøå ↑ ☺☻ ??? _this £50 *bold* 6lol".ind)
)
/*
        val rawInput = "hej var1 +- æøå ↑ ☺☻ ??? _this £50 *bold* 6lol"
        val rawTokens = rawInput.split(' ')
        val lex = Lexer(rawInput)

        val tokens = lex.getTokenSequence().toList()

        tokens.dropLast(1).forEachIndexed {index, posToken ->  assertTrue(posToken.token.let { it is lexer.Token.Identifier &&
                it.value == rawTokens[index]},
                "${posToken.token} is not ${rawTokens[index]}") }
        assertTrue(tokens.last().token == lexer.Token.SpecialChar.EndOfLine)
    }

    fun lexerTestIllegalIdentifiers() {
        val lex = Lexer("var = \"id\" 'di 'di' [] { () true , }")
        //Tokens: var, =, "id, "id", 'di, 'di', [, ], {, (, ), true, \, ",", ->, }
        val tokens = lex.getTokenSequence().toList()

        tokens.dropLast(1).forEach{ assertTrue(it.token !is lexer.Token.Identifier) }
        assertTrue(tokens.last().token == lexer.Token.SpecialChar.EndOfLine)
    }

    fun lexerTestBooleanLiteralTokens() {
        val lex = Lexer("true false")

        val tokens = lex.getTokenSequence().toList()

        assertTrue(tokens[0].token == lexer.Token.Literal.Bool(true))
        assertTrue(tokens[1].token == lexer.Token.Literal.Bool(false))
    }

    fun lexerTestFunctionDeclaration() {
        val lex = Lexer("func print = (txt message) : none\n{ }")

        val tokens = lex.getTokenSequence().toList()

        assertPositionalToken(tokens[0],
                { token -> token == lexer.Token.Type.Func },
                0, 0)
        assertPositionalToken(tokens[1],
                { token -> token == lexer.Token.Identifier("print") },
                5, 0)
        assertPositionalToken(tokens[2],
                { token -> token == lexer.Token.SpecialChar.Equals },
                11, 0)
        assertPositionalToken(tokens[3],
                { token -> token == lexer.Token.SpecialChar.ParenthesesStart },
                13, 0)
        assertPositionalToken(tokens[4],
                { token -> token == lexer.Token.Type.Text },
                14, 0)
        assertPositionalToken(tokens[5],
                { token -> token == lexer.Token.Identifier("message") },
                18, 0)
        assertPositionalToken(tokens[6],
                { token -> token == lexer.Token.SpecialChar.ParenthesesEnd },
                25, 0)
        assertPositionalToken(tokens[7],
                { token -> token == lexer.Token.SpecialChar.Colon },
                27, 0)
        assertPositionalToken(tokens[8],
                { token -> token == lexer.Token.Type.None },
                29, 0)
        assertPositionalToken(tokens[9],
                { token -> token == lexer.Token.SpecialChar.EndOfLine },
                33, 0)
        assertPositionalToken(tokens[10],
                { token -> token == lexer.Token.SpecialChar.BlockStart },
                0, 1)
        assertPositionalToken(tokens[11],
                { token -> token == lexer.Token.SpecialChar.BlockEnd },
                2, 1)
        assertPositionalToken(tokens[12],
                { token -> token == lexer.Token.SpecialChar.EndOfLine },
                3, 1)
    }

    fun lexerTestDoesntRequireWhitespaces() {
        val lex = Lexer("x=y[0]txt")

        val tokens = lex.getTokenSequence().toList()

        assertPositionalToken(tokens[0],
                { token -> token == lexer.Token.Identifier("x") },
                0, 0)

        assertPositionalToken(tokens[1],
                { token -> token == lexer.Token.SpecialChar.Equals },
                1, 0)

        assertPositionalToken(tokens[2],
                { token -> token == lexer.Token.Identifier("y") },
                2, 0)

        assertPositionalToken(tokens[3],
                { token -> token == lexer.Token.SpecialChar.SquareBracketStart },
                3, 0)

        assertPositionalToken(tokens[4],
                { token -> token == lexer.Token.Literal.Number(0.0) },
                4, 0)

        assertPositionalToken(tokens[5],
                { token -> token == lexer.Token.SpecialChar.SquareBracketEnd },
                5, 0)
        assertPositionalToken(tokens[6],
                { token -> token == lexer.Token.Type.Text },
                6, 0)
    }

    fun testAllSpecialChars() {
        val lex = Lexer("={}()[]\n;:,")
        val tokens = lex.getTokenSequence().toList()

        val expected = TokenSequence().`=`.`{`.`}`.`(`.`)`.squareStart.squareEnd.newLine.newLine.colon.`,`.newLine

        assertEquals(expected.tokens, tokens.map { it.token })
        assertEquals(tokens.count(), 12)
    }

    fun testIgnoresEndOfLineAfterLineContinue() {
        val lex = Lexer("a\\\n=")
        val tokens = lex.getTokenSequence().toList()
        assertTrue(tokens[0].token.let{ it is lexer.Token.Identifier && it.value == "a"})
        assertTrue(tokens[1].token == lexer.Token.SpecialChar.Equals)
        assertEquals(tokens.count(),3)

    }

    fun lexerTestInlineLambdaExpression() {
        val lex = Lexer("x = 1 to 10 map {value * 2}")

        val tokens = lex.getTokenSequence().toList()

        assertPositionalToken(tokens[0],
                { token -> token == lexer.Token.Identifier("x") },
                0, 0)
        assertPositionalToken(tokens[1],
                { token -> token == lexer.Token.SpecialChar.Equals },
                2, 0)
        assertPositionalToken(tokens[2],
                { token -> token == lexer.Token.Literal.Number(1.0) },
                4, 0)
        assertPositionalToken(tokens[3],
                { token -> token == lexer.Token.Identifier("to") },
                6, 0)
        assertPositionalToken(tokens[4],
                { token -> token == lexer.Token.Literal.Number(10.0) },
                9, 0)
        assertPositionalToken(tokens[5],
                { token -> token == lexer.Token.Identifier("map") },
                12, 0)
        assertPositionalToken(tokens[6],
                { token -> token == lexer.Token.SpecialChar.BlockStart },
                16, 0)
        assertPositionalToken(tokens[7],
                { token -> token == lexer.Token.Identifier("value") },
                17, 0)
        assertPositionalToken(tokens[8],
                { token -> token == lexer.Token.Identifier("*") },
                23, 0)
        assertPositionalToken(tokens[9],
                { token -> token == lexer.Token.Literal.Number(2.0) },
                25, 0)
        assertPositionalToken(tokens[10],
                { token -> token == lexer.Token.SpecialChar.BlockEnd },
                26, 0)
        assertPositionalToken(tokens[11],
                { token -> token == lexer.Token.SpecialChar.EndOfLine },
                27, 0)
    }


    fun lexerUnfinishedStringFails() {
        assertThrows(StringDoesntEndError::class.java, {Lexer("\"hej").getTokenSequence().toList()})
        assertThrows(StringDoesntEndError::class.java, {Lexer("'hej").getTokenSequence().toList()})
    }

    @org.junit.jupiter.api.Test
    fun multiWordString() {
        val rawString = "hej med dig"
        val tokensPositional = Lexer("\"$rawString\"").getTokenSequence().toList()

        assertPositionalToken(tokensPositional[0],
                { token -> token == lexer.Token.Literal.Text(rawString) },
                0, 0)
        assertEquals(2, tokensPositional.count())
    }

    @org.junit.jupiter.api.Test
    fun multiWordStringEscapedQuotes() {
        val rawString = "hej \\\"med\\\" dig"
        val tokensPositional = Lexer("\"$rawString\"").getTokenSequence().toList()

        assertPositionalToken(tokensPositional[0],
                { token -> token == lexer.Token.Literal.Text(rawString) },
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
                { token -> token == lexer.Token.SpecialChar.Equals },
                1, 0)

        assertPositionalToken(tokens[2],
                { token -> token is lexer.Token.Literal.Number && token.value == 5.0 },
                3, 0)

    }
    @org.junit.jupiter.api.Test
    fun lexerTestNegativeNumber() {
        val lex = Lexer("-5")
        val tokens = lex.getTokenSequence().toList()

        assertPositionalToken(tokens[0],
                { token -> token is lexer.Token.Literal.Number && token.value == -5.0 },
                0, 0)
    }
*/

object LexerTestS : Spek({
    given("the HCL lexer") {
        listOf(
            lexerTestTokenGeneration(),
            lexerTestIgnoreComments(),
            lexerTestTypeDeclarationTokens()
        ).forEach { testData ->
            on(testData.string) {
                val positions = testData.positions.mapIndexed { lineNum, linePositions ->
                        linePositions.map { Pair(it, lineNum)
                    }
                }.flatten()
                val positionalTokens = testData.tokenList.zip(positions).map {
                    PositionalToken(it.first, it.second.second, it.second.first)
                }
                val actualResult = Lexer(testData.string).getTokenSequence().toList()
                val expectedWithResult = positionalTokens.zip(actualResult)
                expectedWithResult.forEach { res ->
                    it("should yield the token ${res.first.token} at idx ${res.first.lineIndex} line ${res.first.lineNumber}") {
                        assertEquals(res.first.token::class.simpleName, res.second.token::class.simpleName)
                        assertEquals(res.first.lineIndex, res.second.lineIndex)
                        assertEquals(res.first.lineNumber, res.second.lineNumber)
                    }
                }
            }
        }
    }
})

private infix fun Int.line(other: Int) = Pair(this, other)
private data class TestData(val string: String, val tokenList: List<Token>, val positions: List<List<Int>>)