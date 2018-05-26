package exceptions

/**
 * It is not allowed to pass a function into another function if the argument-function is generic.
 * For instance:
     * var map = (list[num] l, func[num, text] f): list[text] {...}
     * var something = (T1 i): T2 {...}
     * :something map
 * is not allowed.
 */
class GenericPassedFunctionException(lineNumber: Int, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, lineIndex, lineText) {
    override val errorMessage = "You cannot pass a function with generic types into another function."
    override val helpText = "Make sure that any function passed into another function isn't generic."
}
