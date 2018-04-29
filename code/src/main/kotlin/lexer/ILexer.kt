package lexer

/**
 * The interface which all Lexer implementations are to implement.
 */
interface ILexer {
    /**
     * Gets the next lexical token.
     */
    fun getTokenSequence(): Sequence<PositionalToken>

    /** TODO rename
     * Gets a line from the input text that is being analyzed.
     * @param lineNumber line number from the source code file
     */
    fun inputLine(lineNumber: Int): String
}
