package exceptions

/**
 * Error thrown when string lacks end encapsulation
 */
class StringDoesntEndError(lineNumber: Int, lineIndex: Int, lineText: String)
    : LexerException(lineNumber, lineIndex, lineText) {
    private val missingToken: String = lineText.substring(lineIndex)
    override val errorMessage = "TXT literal lacks closing - '$missingToken'"
    override val helpText = "Try adding a ' or \" "
}
