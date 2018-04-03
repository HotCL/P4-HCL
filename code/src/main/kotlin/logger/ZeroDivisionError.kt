package logger

import exceptions.ParserException

class ZeroDivisionError(lineNumber: Int, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, lineIndex, lineText){
    override val errorType = "ZERO-DIVISION-ERROR"
    override val errorMessage = "Division by 0 found"
    override val helpText = "Check your variables"
}

