package logger

import exceptions.ParserException

class UndeclaredError(lineNumber: Int, lineIndex: Int, lineText: String, nameOfUnit: String = "variable")
    : ParserException(lineNumber, lineIndex, lineText){

    private val nameOfUnitialized = nameOfUnit
    override val errorType: String
        get() = "UNDECLARED-ERROR"
    override val errorMessage: String
        get() = "Undeclared identifier '$nameOfUnitialized' found."
    override val helpText: String
        get() = "Declare identifier before use."
}
