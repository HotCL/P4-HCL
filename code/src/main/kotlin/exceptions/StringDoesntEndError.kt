package exceptions

import kotlin.math.min

/**
 * Error thrown when string lacks end encapsulation
 */
class StringDoesntEndError(lineNumber: Int, lineIndex: Int, lineText: String)
    : LexerException(lineNumber, lineIndex, lineText) {
    private val missingToken: String = lineText.substring(min(lineIndex,lineText.length))
    override val errorMessage = "TXT literal lacks closing - '$missingToken'"
    override val helpText = "Try adding a ' or \" "
}
