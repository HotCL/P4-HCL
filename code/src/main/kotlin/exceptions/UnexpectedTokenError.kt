package exceptions
import lexer.PositionalToken
import lexer.Token
/**
 * Class used to log unexpectedToken errors
 * @param actualToken The actual Token passed
 */
class UnexpectedTokenError(
    lineNumber: Int,
    fileName: String,
    lineIndex: Int,
    lineText: String,
    private val actualToken: Token
)
                                : ParserException(lineNumber, fileName, lineIndex, lineText) {
    override val errorMessage = "Found unexpected token: '$actualToken'."
    override val helpText = ""
    constructor(posToken: PositionalToken, lineText: String) : this(posToken.lineNumber, posToken.fileName,
            posToken.lineIndex, lineText, posToken.token)
}
