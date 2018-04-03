package logger

import exceptions.CompilationException
/**
 * The logger class, used for all output from the compiler.
 */
open class Logger: ILogger {
    /**
     * Function used to log all compilation errors from the compiler.
     */
    override fun logCompilationError(error: CompilationException) {
        writeLine("- ERROR: ${error.errorType} found at line ${error.lineNumber} index ${error.lineIndex}:")
        writeLine(" | ${error.lineText}")
        writeLine(" |" + " " * error.lineIndex + "^--")
        writeLine(" | ${error.errorMessage}")
        if (error.helpText != "") writeLine(" | -->help: ${error.helpText}")
    }
    private operator fun String.times (num: Int) = (0 .. num).joinToString("") { this }

    open fun writeLine(text: String){
        println(text)
    }
}

class TestLogger : Logger() {
    val buffer : StringBuilder = StringBuilder()
    override fun writeLine(text: String) {
        buffer.append(text + "\n")
    }
}
