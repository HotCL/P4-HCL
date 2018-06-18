package exceptions

/**
 * Abstract class for exceptions thrown from the parser
 */
abstract class ParserException(
    lineNumber: Int,
    fileName: String,
    lineIndex: Int,
    lineText: String
) : CompilationException(lineNumber, fileName, lineIndex, lineText)
