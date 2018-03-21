package logger

import exceptions.CompilationException

class Logger: ILogger {
    override fun logCompilationError(error: CompilationException) {
        logErrorAtLocation(error.lineText, error.lineNumber, error.lineIndex)
    }

    private fun logErrorAtLocation(line: String, lineNumber: Int, lineIndex: Int) {
        println(".- Error on line $lineNumber at index $lineIndex:")
        println("| $line")
        println("|" + "-" * lineIndex + "^")
    }

    private operator fun String.times (num: Int) = (0 .. num).joinToString("") { this }
}
