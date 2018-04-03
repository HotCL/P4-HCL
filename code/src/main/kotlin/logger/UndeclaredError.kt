package logger

import exceptions.ParserException

class UndeclaredError(lineNumber: Int, lineIndex: Int, lineText: String, private val nameOfVar: String)
    : ParserException(lineNumber, lineIndex, lineText){
    override val errorType = "UNDECLARED-ERROR"
    override val errorMessage = "Undeclared identifier '$nameOfVar' found."
    override val helpText = "Declare identifier before use."
}
