package logger

import exceptions.ParserException

/**
 * Class used to log missing encapsulations.
 */
class MissingEncapsulationError(lineNumber: Int, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, lineIndex, lineText){
    private val missingToken: Char = lineText[lineIndex]
    override val errorType = "MISSING-ENCAPSULATION-ERROR"
    override val errorMessage = "Missing closing character for '$missingToken'"
    override val helpText  = "Remember to always close encapsulations."
}
