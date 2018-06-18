package exceptions

/**
 * Class used to log missing arguments.
 * @param functionName The name of the function which needs arguemts
 */
class MissingArgumentError(
    lineNumber: Int,
    fileName: String,
    lineIndex: Int,
    lineText: String,
    private val functionName: String
)
    : ParserException(lineNumber, fileName, lineIndex, lineText) {
    override val errorMessage = "Missing argument for function '$functionName'."
    override val helpText = "Have you included all arguments for function?"
}
