package exceptions

/**
 * Class used to log missing encapsulations.
 */
class MissingEncapsulationError(lineNumber: Int, fileName: String, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, fileName, lineIndex, lineText) {
    private val missingToken: Char = lineText[lineIndex]
    override val errorMessage = "Missing closing character for '$missingToken'"
    override val helpText = "Remember to always close encapsulations."
}
