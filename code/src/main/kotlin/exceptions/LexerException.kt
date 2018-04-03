package exceptions

/**
 * Abstract class for exceptions thrown from the lexer
 */
abstract class LexerException(lineIndex: Int,
                     lineNumber: Int,
                     lineText: String): CompilationException(lineNumber, lineIndex, lineText)
