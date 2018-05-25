package exceptions

/**
 * Exception thrown when trying to re-declare a identifier that is already defined with same signature
 */
class AlreadyDeclaredException(lineNumber: Int, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, lineIndex, lineText) {
    override val errorMessage = "You cannot declare the same identifier twice. "
    override val helpText = "Try changing the name of the identifier. If this is a function," +
            " then make sure you are not re-declaring a function with the same arguments."
}
