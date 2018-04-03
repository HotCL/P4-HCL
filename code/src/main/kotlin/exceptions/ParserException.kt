package exceptions

/**
 * Abstract class for exceptions thrown from the parser
 */
abstract class ParserException(lineNumber: Int,
                           lineIndex: Int,
                           lineText: String): CompilationException(lineNumber, lineIndex, lineText)
