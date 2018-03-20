package exceptions

class LexerException(errorMessage: String,
                      lineNumber: Int? = null,
                      lineIndex: Int? = null,
                      lineText: String? = null): CompilationException(errorMessage, lineNumber, lineIndex, lineText)
