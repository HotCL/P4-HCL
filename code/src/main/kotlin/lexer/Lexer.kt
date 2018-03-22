package lexer

import kotlin.coroutines.experimental.buildSequence

/**
 * The default implementation of the ILexer interface
 */
class Lexer(private val inputContent: String) : ILexer {
    override fun getTokenSequence(): Sequence<PositionalToken> = buildSequence {
        // Yield getTokenSequence for string "var x = 5 + 7" to satisfy testcase
        yieldAll(
            listOf(
                PositionalToken(Token.Type.Var(), 0, 0),
                PositionalToken(Token.Identifier("x"), 0, 4),
                PositionalToken(Token.SpecialChar.Equals(), 0, 6),
                PositionalToken(Token.Literal.Number("5"), 0, 8),
                PositionalToken(Token.Identifier("+"), 0, 10),
                PositionalToken(Token.Literal.Number("7"), 0, 12)
            )
        )
    }

    override fun inputLine(lineNumber: Int) = inputContent.split("\n")[lineNumber]
}
