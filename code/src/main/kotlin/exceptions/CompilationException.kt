package exceptions

/**
 * Exception to be thrown when a compilation error occurs.
 * @param errorType The type of the error as enum
 * @param errorMessage Error description
 * @param lineNumber Line number from source file that caused the compilation error
 * @param lineIndex Line index from source file that caused the compilation error
 * @param lineText The textual representation of the line in which the compilation error occurred
 */
open class CompilationException(val errorType: ErrorTypes,
                                val lineNumber: Int,
                                val lineIndex: Int,
                                val errorMessage: String,
                                val lineText: String,
                                val helpText: String = ""): Exception()

enum class ErrorTypes{
    TYPE_ERROR,
    UNDECLARED_IDENTIFIER_ERROR,
    UNINITIALIZED_ERROR,
    ZERO_DIVISION,
    MISSING_PARENTHESIS,
    MISSING_BRACKETS,
    MISSING_ARGUMENT
}
