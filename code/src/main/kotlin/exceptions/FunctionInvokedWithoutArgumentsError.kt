package exceptions

/**
 * Class used to log when user tries to call an infix function without arguments.
 * @param nameOfFunc The name of the function that is being called
 */
class FunctionInvokedWithoutArgumentsError(
    lineNumber: Int,
    fileName: String,
    lineIndex: Int,
    lineText: String,
    private val nameOfFunc: String
)
    : ParserException(lineNumber, fileName, lineIndex, lineText) {
    override val errorMessage = "Function $nameOfFunc cannot be called with 0 arguments!"
    override val helpText = "Make sure you are calling the correct function."
}
