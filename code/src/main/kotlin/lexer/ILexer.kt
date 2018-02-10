package lexer

/**
 * The interface which all Lexer implementations are to implement.
 */
interface ILexer {
    /**
     * Tokenize the string.
     * @param string String to split into tokens
     */
    fun lexStuff(string: String): List<String>
}
