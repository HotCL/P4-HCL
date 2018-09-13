package exceptions

/**
 * Exception thrown when trying to overload a defined function with different amount of parameters than the original.
 */
class OverloadWithDifferentAmountOfArgumentsException(
    lineNumber: Int,
    fileName: String,
    lineIndex: Int,
    lineText: String
)
    : ParserException(lineNumber, fileName, lineIndex, lineText) {
    override val errorMessage = "You cannot overload a function with a different amount of parameters!"
    override val helpText = "Try to rename the method or use a tuple for the excess parameters."
}
