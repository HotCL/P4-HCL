package logger

import exceptions.ParserException

class MissingArgumentError(lineNumber: Int, lineIndex: Int, lineText: String, functionName: String)
    : ParserException(lineNumber, lineIndex, lineText){
    private val _functionName = functionName
    override val errorType: String
        get() = "MISSING-ARGUMENT-ERROR"
    override val errorMessage: String
        get() = "Missing argument for function '$_functionName'."
    override val helpText: String
        get() = "Have you included all arguments for function?"
}
