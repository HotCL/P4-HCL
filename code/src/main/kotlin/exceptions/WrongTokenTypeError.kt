package exceptions
import lexer.Token
/**
 * Class used to log wrong token type errors
 * @param expectedToken The Token that is expected
 * @param actualToken The actual Token passed
 */
class WrongTokenTypeError(lineNumber: Int, lineIndex: Int, lineText: String,
                          private val expectedToken: Token, private val actualToken: Token)
                          : ParserException(lineNumber, lineIndex, lineText){
    override val errorMessage = "Expected Token of type '$expectedToken', but found Token of type '$actualToken'"
    override val helpText = "Are you using the right type of token?"
}
