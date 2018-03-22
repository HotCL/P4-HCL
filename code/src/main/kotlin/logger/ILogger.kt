package logger

import exceptions.CompilationException

/**
 * The logging interface. This interface should be used for and declare all output from the compiler.
 */
interface ILogger {
    /**
     * Used to log all compilation errors
     */
    fun logCompilationError(error: CompilationException)
}
