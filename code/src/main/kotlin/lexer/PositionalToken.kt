package lexer


/** TODO make header.
 * Delegation of responsibility. This class has the positional data regarding a token, following, basically,
 * a wrapper pattern.
 * This way we can make all the fields read-only and blah.
 *
 */
class PositionalToken(val token: Token, val lineNumber: Int, val lineIndex: Int)
