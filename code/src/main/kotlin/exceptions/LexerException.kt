package exceptions

abstract class LexerException(lineIndex: Int,
                     lineNumber: Int,
                     lineText: String): CompilationException(lineNumber, lineIndex, lineText)
