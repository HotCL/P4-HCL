package lexicalTests

import exceptions.StringDoesntEndError
import lexer.Lexer
import lexer.PositionalToken
import hclTestFramework.lexer.buildTokenSequence
import lexer.Token
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.*

class LexerTestMisc {
    @org.junit.jupiter.api.Test
    fun lexerUnfinishedStringFails() {
        assertThrows(StringDoesntEndError::class.java, {Lexer("\"hej").getTokenSequence().toList()})
        assertThrows(StringDoesntEndError::class.java, {Lexer("'hej").getTokenSequence().toList()})
    }

    @org.junit.jupiter.api.Test
    fun lexerTestGetLine() {
        val lexer = Lexer("Hej\nMed\nDig!")
        assertEquals("Hej\n", lexer.inputLine(0))
        assertEquals("Med\n", lexer.inputLine(1))
        assertEquals("Dig!\n", lexer.inputLine(2))
    }
}

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
    listOf (listOf(0, 4, 9, 12, 16, 18, 21, 25, 31, 35, 42))
)

private fun lexerTestBooleanLiteralTokens() = TestData(
    "true false",
    buildTokenSequence { bool(true).bool(false) },
    listOf (listOf(0, 5))
)

private fun lexerTestFunctionDeclaration() = TestData(
    "func print = (txt message) : none\n{ }",
    buildTokenSequence {
        func.identifier("print").`=`.`(`.text.identifier("message").`)`.colon.none.newLine.`{`.`}`
    },
    listOf (
        listOf(0, 5, 11, 13, 14, 18, 25, 27, 29, 33),
        listOf(0, 2)
    )
)

private fun lexerTestDoesntRequireWhitespaces() = TestData(
    "x=y[0]txt",
    buildTokenSequence {
        identifier("x").`=`.identifier("y").squareStart.number(0.0).squareEnd.text
    },
    listOf (
        listOf(0, 1, 2, 3, 4, 5, 6)
    )
)

private fun testAllSpecialChars() = TestData(
    "={}()[]:,;",
    buildTokenSequence {
        `=`.`{`.`}`.`(`.`)`.squareStart.squareEnd.colon.`,`.newLine
    },
    listOf (
        (0 .. 9).toList()
    )
)

private fun lexerTestInlineLambdaExpression() = TestData(
    "x = 1 to 10 map {value * 2}",
    buildTokenSequence {
        identifier("x").`=`.number(1.0).identifier("to").number(10.0).identifier("map").`{`.identifier("value").identifier("*").number(2.0).`}`
    },
    listOf (
        listOf(0, 2, 4, 6, 9, 12, 16, 17, 23, 25, 26)
    )
)

private fun lexerTestNegativeNumber() = TestData("-5", buildTokenSequence { number(-5.0).newLine }, listOf(listOf (0, 2)))

private fun lexerIgnoresEndOfLineAfterLineContinue() =
        TestData("a\\\n=", buildTokenSequence { identifier("a").`=`.newLine }, listOf(listOf (0), listOf(0, 1)))

private fun lexerTestTabTest() =
        TestData("x=\t5", buildTokenSequence { identifier("x").`=`.number(5.0) }, listOf(listOf (0, 1, 3)))

private fun lexerTestIllegalIdentifiers() = TestData(
    "var = \"id\" 'di 'di' [] { () true , }",
    buildTokenSequence {
        `var`.`=`.text("id").text("di 'di").squareStart.squareEnd.`{`.`(`.`)`.bool(true).`,`.`}`
    },
    listOf (
        listOf(0, 4, 6, 11, 20, 21, 23, 25, 26, 28, 33, 35)
    )
)

private fun lexerTestMultiWordString() =
        TestData("\"hej med dig\"", buildTokenSequence { text("hej med dig").newLine }, listOf (listOf(0, 13)))

private fun lexerTestmultiWordStringEscapedQuotes() =
        TestData("\"hej \\\"med\\\" dig\"", buildTokenSequence { text("hej \\\"med\\\" dig").newLine }, listOf (listOf(0, 17)))

object LexerTestInputYieldsOutput : Spek({
    given("the HCL lexer") {
        listOf(
            lexerTestTokenGeneration(),
            lexerTestIgnoreComments(),
            lexerTestTypeDeclarationTokens(),
            lexerTestLegalIdentifiers(),
            lexerTestBooleanLiteralTokens(),
            lexerTestFunctionDeclaration(),
            lexerTestDoesntRequireWhitespaces(),
            testAllSpecialChars(),
            lexerTestInlineLambdaExpression(),
            lexerTestNegativeNumber(),
            lexerIgnoresEndOfLineAfterLineContinue(),
            lexerTestTabTest(),
            lexerTestIllegalIdentifiers(),
            lexerTestMultiWordString(),
            lexerTestmultiWordStringEscapedQuotes()
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
                        val expectedToken = res.first.token
                        when (expectedToken) {
                            is Token.Identifier -> assertEquals(expectedToken.value, (res.second.token as? Token.Identifier)?.value)
                            is Token.Literal.Text -> assertEquals(expectedToken.value, (res.second.token as? Token.Literal.Text)?.value)
                            is Token.Literal.Bool -> assertEquals(expectedToken.value, (res.second.token as? Token.Literal.Bool)?.value)
                            is Token.Literal.Number -> assertEquals(expectedToken.value, (res.second.token as? Token.Literal.Number)?.value)
                            else -> assertEquals(res.first.token, res.second.token)
                        }
                        assertEquals(res.first.lineIndex, res.second.lineIndex)
                        assertEquals(res.first.lineNumber, res.second.lineNumber)
                    }
                }
            }
        }
    }
})

private data class TestData(val string: String, val tokenList: List<Token>, val positions: List<List<Int>>)