package exceptions

/**
 * Class used to log when user tries to pass a lambda argument to a function that doesn't take that argument.
 */
class LambdaArgumentNotAcceptedError(lineNumber: Int, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, lineIndex, lineText) {
    override val errorMessage = "No function found that takes a lambda argument at that position!"
    override val helpText = "Make sure that you're using the correct function,\n" +
            "\tand that the lambda is in the correct position."
}