package logger

import exceptions.ParserException

class MissingEncapsulationError(lineNumber: Int, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, lineIndex, lineText){
    private val missingToken: Char = lineText[lineIndex]
    override val errorType: String
        get() = "MISSING-ENCAPSULATION-ERROR"
    override val errorMessage: String
        get() = "Missing closing character for '$missingToken'"
    override val helpText: String
        get() = "Remember to always close encapsulations."
}
