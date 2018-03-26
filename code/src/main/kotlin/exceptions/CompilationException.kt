package exceptions

/**
 * Exception to be thrown when a compilation error occurs.
 * @param errorMessage Error description
 * @param lineNumber Line number from source file that caused the compilation error
 * @param lineIndex Line index from source file that caused the compilation error
 * @param lineText The textual representation of the line in which the compilation error occurred
 */
open class CompilationException(val errorMessage: String,
                                val lineNumber: Int,
                                val lineIndex: Int,
                                val lineText: String): Exception()
