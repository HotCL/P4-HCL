package exceptions

import lexer.Token
/**
 * Class used to log errors where the type 'none' is used as input-parameter to functions
 */
class InitializedFunctionParameterError(lineNumber: Int, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, lineIndex, lineText){
    override val errorMessage = "Cannot initialize function arguments in declaration."
    override val helpText = "Function arguments are initialized when the function is called."
}
