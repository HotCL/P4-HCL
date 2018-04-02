package exceptions

/**
 * Exception to be thrown when a compilation error occurs.
 * @param lineNumber Line number from source file that caused the compilation error
 * @param lineIndex Line index from source file that caused the compilation error
 * @param lineText The textual representation of the line in which the compilation error occurred
 */
abstract class CompilationException(val lineNumber: Int,
                                    val lineIndex: Int,
                                    val lineText: String): Exception(){
    abstract val errorType: String
    abstract val errorMessage: String
    abstract val helpText: String
}
