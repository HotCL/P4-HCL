package exceptions

/**
 * Class used to log when variable is used as function.
 * @param nameOfId The name of the identifier in question
 */
class IdentifierIsNotFunctionError(
    lineNumber: Int,
    fileName: String,
    lineIndex: Int,
    lineText: String,
    private val nameOfId: String
)
    : ParserException(lineNumber, fileName, lineIndex, lineText) {
    override val errorMessage = "Tried to use identifier $nameOfId as function, but it is not a function!"
    override val helpText = "Make sure to use the correct identifier."
}
