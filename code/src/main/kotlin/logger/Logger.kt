package logger

import exceptions.CompilationException
/**
 * The logger class, used for all output from the compiler.
 */
class Logger: ILogger {
    /**
     * Function used to log all compilation errors from the compiler.
     */
    override fun logCompilationError(error: CompilationException) {
        print("- ERROR: ${error.errorType} found at line ${error.lineNumber} index ${error.lineIndex}:\n")
        println(" | ${error.lineText}")
        println(" |" + " " * error.lineIndex + "^--")
        println(" | ${error.errorMessage}")
        if (error.helpText != "") println(" | -->help: ${error.helpText}")
    }
    private operator fun String.times (num: Int) = (0 .. num).joinToString("") { this }
}
