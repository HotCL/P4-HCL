package exceptions

class LexerException(errorType: ErrorTypes,
                     errorMessage: String,
                      lineNumber: Int,
                     lineIndex: Int,
                      lineText: String): CompilationException(errorType, errorMessage, lineNumber, lineIndex, lineText)
