package logger

import exceptions.ParserException

class UninitializedError(lineNumber: Int, lineIndex: Int, lineText: String, nameOfUnit: String = "variable")
                        : ParserException(lineNumber, lineIndex, lineText){
    private val nameOfUnitialized = nameOfUnit
    override val errorType: String
        get() = "UNINITIALIZED-ERROR"
    override val errorMessage: String
        get() = "Use of uninitialized variable '$nameOfUnitialized' found."
    override val helpText: String
        get() = "Try initializing variable before use."
}
