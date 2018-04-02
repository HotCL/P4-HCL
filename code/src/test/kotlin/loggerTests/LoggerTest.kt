package loggerTests
import logger.*

class TokenTest {

    @org.junit.jupiter.api.Test
    fun testTypeError() {
        val error = TypeError(4, 10, "var b = 4 < \"5\"", "<", listOf("num", "txt", "bool"))
        val logger = Logger()
        logger.logCompilationError(error)
    }

    @org.junit.jupiter.api.Test
    fun testUndeclaredError() {
        val error = UndeclaredError(12, 8, "txt s = textString", "textString")
        val logger = Logger()
        logger.logCompilationError(error)
    }

    @org.junit.jupiter.api.Test
    fun testUnitializedError() {
        val error = UninitializedError(12, 4, "x = someUninitializedVariable + 10", "someUninitializedVariable")
        val logger = Logger()
        logger.logCompilationError(error)
    }

    @org.junit.jupiter.api.Test
    fun testZeroDivisionError() {
        val error = ZeroDivisionError(7, 6, "x = 42/0")
        val logger = Logger()
        logger.logCompilationError(error)
    }
    @org.junit.jupiter.api.Test
    fun testMissingArgumentError() {
        val error = MissingArgumentError(11, 2, "2 someFunction", "someFunction")
        val logger = Logger()
        logger.logCompilationError(error)
    }
    @org.junit.jupiter.api.Test
    fun testMissingEncapsulation() {
        val error = MissingEncapsulationError(30, 0, "({()}")
        val logger = Logger()
        logger.logCompilationError(error)
    }
}
