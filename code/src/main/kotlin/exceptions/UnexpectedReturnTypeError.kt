package exceptions

/**
 * Error thrown when trying to return a type that was not allowed/expected
 * @param expectedType The expected type for expression
 * @param actualType The actual type parsed
 */
class UnexpectedReturnTypeError(
    lineNumber: Int,
    fileName: String,
    lineIndex: Int,
    lineText: String,
    expectedType: String,
    actualType: String
)
    : ParserException(lineNumber, fileName, lineIndex, lineText) {
    override val errorMessage = "Return type of '$actualType' was not expected. '$expectedType' was."
    override val helpText = "Fix return type"
}
