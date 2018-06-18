package exceptions

/**
 * Class used to log errors where the a type is used implicity, forexample "list<func>", whereas func isn't explicit.
 */
class ImplicitTypeNotAllowed(lineNumber: Int, fileName: String, lineIndex: Int, lineText: String)
    : ParserException(lineNumber, fileName, lineIndex, lineText) {
    override val errorMessage = "Cannot initialize function arguments in declaration."
    override val helpText = "Function arguments are initialized when the function is called."
}
