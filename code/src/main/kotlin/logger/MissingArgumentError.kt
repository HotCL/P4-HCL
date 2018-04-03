package logger

import exceptions.ParserException

class MissingArgumentError(lineNumber: Int, lineIndex: Int, lineText: String, private val functionName: String)
    : ParserException(lineNumber, lineIndex, lineText){
    override val errorType = "MISSING-ARGUMENT-ERROR"
    override val errorMessage  = "Missing argument for function '$functionName'."
    override val helpText = "Have you included all arguments for function?"
}
