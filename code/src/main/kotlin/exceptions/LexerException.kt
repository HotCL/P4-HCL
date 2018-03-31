package exceptions

class LexerException(errorType: ErrorTypes,
                     lineNumber: Int,
                     lineIndex: Int,
                     errorMessage: String,
                     lineText: String,
                     helpText: String = ""): CompilationException(errorType, lineNumber, lineIndex, errorMessage, lineText, helpText)
