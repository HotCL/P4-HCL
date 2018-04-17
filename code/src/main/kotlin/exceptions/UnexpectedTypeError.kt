package exceptions

/**
 * Class used to log type errors.
 * @param expectedType The expected type for expression
 * @param actualType The actual type parsed
 */
class UnexpectedTypeError(lineNumber: Int, lineIndex: Int, lineText: String,
                private val expectedType: String, private val actualType: String)
    : ParserException(lineNumber, lineIndex, lineText) {

    override val errorMessage = "Cannot implicit cast type '$actualType' to type '$expectedType'."
    override val helpText = "Try casting your types to match eachother."
}