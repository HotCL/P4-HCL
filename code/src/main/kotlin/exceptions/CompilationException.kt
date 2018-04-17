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
    val errorType = this::class.simpleName
    abstract val errorMessage: String
    override val message: String? get() = errorMessage
    abstract val helpText: String
}
