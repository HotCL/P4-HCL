package exceptions

abstract class ParserException(lineNumber: Int,
                           lineIndex: Int,
                           lineText: String): CompilationException(lineNumber, lineIndex, lineText)
