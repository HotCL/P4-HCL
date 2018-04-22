package exceptions

import lexer.Token
/**
 * Class used to log errors where the type 'none' is used as input-parameter to functions
 */
class NoneAsInputError(lineNumber: Int, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, lineIndex, lineText){
    override val errorMessage = "Functions can not have input-parameter of type 'none'."
    override val helpText = "'none' can only be used as a functions return-type."
}
