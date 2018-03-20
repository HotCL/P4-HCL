package logger

import exceptions.CompilationException

class Logger: ILogger {
    override fun logCompilationError(error: CompilationException) {
        logErrorAtLocation(error.lineText, error.lineNumber, error.lineIndex)
    }

    private fun logErrorAtLocation(line: String?, lineNumber: Int?, lineIndex: Int?) {
        println(".- Error on line ${lineNumber ?: "unknown"} at index ${lineIndex ?: "unknown"}:")
        println("| ${line ?: "Line content is unknown..."}")
        println("|" + "-" * (lineIndex ?: 0) + "^")
    }

    private operator fun String.times (num: Int) = (0 .. num).joinToString("") { this }
}
