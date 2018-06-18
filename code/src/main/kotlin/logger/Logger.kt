package logger

import exceptions.CompilationException
/**
 * The logger class, used for all output from the compiler.
 */
open class Logger : ILogger {
    /**
     * Function used to log all compilation errors from the compiler.
     */
    override fun logCompilationError(error: CompilationException) {
        writeLine("\n- ERROR: ${error.errorType} found at ${error.fileName}" +
                " line ${error.lineNumber} index ${error.lineIndex}:")
        writeLine(" | ${error.lineText.trimEnd()}")
        writeLine(" |" + " " * error.lineIndex + "^--")
        writeLine(" | ${error.errorMessage}")
        if (error.helpText != "") writeLine(" | -->help: ${error.helpText}\n")
    }
    private operator fun String.times (num: Int) = (0..num).joinToString("") { this }

    open fun writeLine(text: String) {
        println(text)
    }
}
