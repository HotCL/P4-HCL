package exceptions

import parser.AstNode
import parser.typeName

/**
 * Class used to log unknown function overload errors.
 * @param nameOfVar The name of the variable that is uninitialized
 */
class UnknownFunctionOverload(
    lineNumber: Int,
    fileName: String,
    lineIndex: Int,
    lineText: String,
    nameOfFunction: String,
    providedArguments: List<AstNode.Type>
)
    : ParserException(lineNumber, fileName, lineIndex, lineText) {
    override val errorMessage = "Unable to invoke $nameOfFunction with the following arguments: " +
            "(${providedArguments.joinToString { it.typeName }})!"
    override val helpText = "Please check the available overloads for $nameOfFunction."
}
