package exceptions

/**
 * Class used to log uninitialized implicit type declaration errors.
 * @param nameOfVar The name of the variable that is uninitialized
 */
class UninitializedImplicitTypeError(
    lineNumber: Int,
    fileName: String,
    lineIndex: Int,
    lineText: String,
    private val nameOfVar: String
)
    : ParserException(lineNumber, fileName, lineIndex, lineText) {
    override val errorMessage = "Cannot implicitly declare variable $nameOfVar!"
    override val helpText = "Implicit variables must be initialized upon declaration."
}
