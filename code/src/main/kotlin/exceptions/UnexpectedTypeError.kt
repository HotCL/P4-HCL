package exceptions

/**
 * Class used to log type errors.
 * @param expectedType The expected type for expression
 * @param actualType The actual type parsed
 */
class UnexpectedTypeError(
    lineNumber: Int,
    fileName: String,
    lineIndex: Int,
    lineText: String,
    expectedType: String,
    actualType: String
)
    : ParserException(lineNumber, fileName, lineIndex, lineText) {

    override val errorMessage = "Cannot implicitly cast type '$actualType' to type '$expectedType'."
    override val helpText = "Try casting your types to match eachother."
}
