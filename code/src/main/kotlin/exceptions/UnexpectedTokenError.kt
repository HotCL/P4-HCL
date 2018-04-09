package exceptions
import lexer.Token
/**
 * Class used to log unexpectedToken errors
 * @param actualToken The actual Token passed
 */
class UnexpectedTokenError(lineNumber: Int, lineIndex: Int, lineText: String, private val actualToken: Token)
                                : ParserException(lineNumber, lineIndex, lineText){
    override val errorMessage = "Found unexpected token: '$actualToken'."
    override val helpText = ""
}