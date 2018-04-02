package logger

import exceptions.ParserException

class ZeroDivisionError(lineNumber: Int, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, lineIndex, lineText){
    override val errorType: String
        get() = "ZERO-DIVISION-ERROR"
    override val errorMessage: String
        get() = "Division by 0 found"
    override val helpText: String
        get() = "Check your variables"
}

