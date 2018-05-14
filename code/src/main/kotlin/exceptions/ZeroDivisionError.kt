package exceptions

/**
 * Class used to log zero division errors.
 */
class ZeroDivisionError(lineNumber: Int, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, lineIndex, lineText) {
    override val errorMessage = "Division by 0 found"
    override val helpText = "Check your variables"
}
