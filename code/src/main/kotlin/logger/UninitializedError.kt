package logger

import exceptions.ParserException

class UninitializedError(lineNumber: Int, lineIndex: Int, lineText: String, private val nameOfVar: String)
                        : ParserException(lineNumber, lineIndex, lineText){
    override val errorType = "UNINITIALIZED-ERROR"
    override val errorMessage = "Use of uninitialized variable '$nameOfVar' found."
    override val helpText = "Try initializing variable before use."
}
