package lexer

/**
 * The default implementation of the ILexer interface
 */
class Lexer : ILexer {
    override fun lexStuff(string: String) = string.split(" ")
}
