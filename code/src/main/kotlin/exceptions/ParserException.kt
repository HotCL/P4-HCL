package exceptions

class ParserException(errorType: ErrorTypes,
                      errorMessage: String,
                      lineNumber: Int,
                      lineIndex: Int,
                      lineText: String): CompilationException(errorType, errorMessage, lineNumber, lineIndex, lineText)
