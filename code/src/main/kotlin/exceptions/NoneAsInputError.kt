package exceptions

/**
 * Class used to log errors where the type 'none' is used as input-parameter to functions
 */
class NoneAsInputError(lineNumber: Int, fileName: String, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, fileName, lineIndex, lineText) {
    override val errorMessage = "Functions can not have input-parameter of type 'none'."
    override val helpText = "'none' can only be used as a functions return-type."
}
