package exceptions

/**
 * Abstract class for exceptions thrown from the lexer
 */
abstract class LexerException(
    lineIndex: Int,
    fileName: String,
    lineNumber: Int,
    lineText: String
) : CompilationException(lineNumber, fileName, lineIndex, lineText)
