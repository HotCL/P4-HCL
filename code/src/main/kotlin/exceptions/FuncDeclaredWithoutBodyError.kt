package exceptions

/**
 * Class used to log when user doesn't initialize function upon declaration.
 * @param nameOfFunc The name of the function that is uninitialized
 */
class FuncDeclaredWithoutBodyError(
    lineNumber: Int,
    fileName: String,
    lineIndex: Int,
    lineText: String,
    private val nameOfFunc: String
)
    : ParserException(lineNumber, fileName, lineIndex, lineText) {
    override val errorMessage = "Cannot declare function $nameOfFunc without body!"
    override val helpText = "Functions must always be initialized with a body upon declaration."
}
