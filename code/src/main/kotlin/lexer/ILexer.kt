package lexer

/**
 * The interface which all Lexer implementations are to implement.
 */
interface ILexer {
    /**
     * Gets the next lexical token.
     */
    fun nextToken(): Token
}
