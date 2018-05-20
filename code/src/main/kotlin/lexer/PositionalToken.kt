package lexer

/**
 * Delegation of responsibility. This class has the positional data regarding a token, following, basically,
 * a wrapper pattern.
 * This way we can make all the fields read-only.
 *
 */
class PositionalToken(val token: Token, val lineNumber: Int, val lineIndex: Int, val fileName: String)
