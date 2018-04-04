package exceptions

/**
 * Class used to log undeclared variables errors.
 * @param nameOfVar The name of the identifier that is undeclared
 */
class UndeclaredError(lineNumber: Int, lineIndex: Int, lineText: String, private val nameOfVar: String)
    : ParserException(lineNumber, lineIndex, lineText){
    override val errorMessage = "Undeclared identifier '$nameOfVar' found."
    override val helpText = "Declare identifier before use."
}
