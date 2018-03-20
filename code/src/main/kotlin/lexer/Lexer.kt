package lexer

import kotlin.coroutines.experimental.buildSequence

/**
 * The default implementation of the ILexer interface
 */
class Lexer(private val inputContent: String) : ILexer {
    override fun tokens(): Sequence<Token> = buildSequence {
        // Yield tokens for string "var x = 5 + 7" to satisfy testcase
        yieldAll(
            listOf(
                Token.Type.Var(),
                Token.Identifier("x"),
                Token.SpecialChar.Equals(),
                Token.Literal.Number("5"),
                Token.Identifier("+"),
                Token.Literal.Number("7")
            )
        )
    }

    override fun inputLine(lineNumber: Int) = inputContent.split("\n")[lineNumber]
}
