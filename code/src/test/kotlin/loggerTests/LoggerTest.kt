package loggerTests
import logger.*
import org.junit.jupiter.api.Assertions.assertEquals

class TokenTest {

    @org.junit.jupiter.api.Test
    fun testTypeError() {
        val error = TypeError(4, 10, "var b = 4 < \"5\"", "<", listOf("num", "txt", "bool"))
        val logger = TestLogger()
        logger.logCompilationError(error)
        assertEquals("- ERROR: TYPE-ERROR found at line 4 index 10:\n" +
                             " | var b = 4 < \"5\"\n" +
                             " |           ^--\n" +
                             " | Function '<' not defined for types: 'num', 'txt' and 'bool'.\n" +
                             " | -->help: Try casting your types to match eachother.\n", logger.buffer.toString())
    }

    @org.junit.jupiter.api.Test
    fun testUndeclaredError() {
        val error = UndeclaredError(12, 8, "txt s = textString", "textString")
        val logger = TestLogger()
        logger.logCompilationError(error)
        assertEquals("- ERROR: UNDECLARED-ERROR found at line 12 index 8:\n" +
                             " | txt s = textString\n" +
                             " |         ^--\n" +
                             " | Undeclared identifier 'textString' found.\n" +
                             " | -->help: Declare identifier before use.\n", logger.buffer.toString())
    }

    @org.junit.jupiter.api.Test
    fun testUnitializedError() {
        val error = UninitializedError(12, 4, "x = someUninitializedVariable + 10", "someUninitializedVariable")
        val logger = TestLogger()
        logger.logCompilationError(error)
        assertEquals("- ERROR: UNINITIALIZED-ERROR found at line 12 index 4:\n" +
                             " | x = someUninitializedVariable + 10\n" +
                             " |     ^--\n" +
                             " | Use of uninitialized variable 'someUninitializedVariable' found.\n" +
                             " | -->help: Try initializing variable before use.\n", logger.buffer.toString())
    }

    @org.junit.jupiter.api.Test
    fun testZeroDivisionError() {
        val error = ZeroDivisionError(7, 6, "x = 42/0")
        val logger = TestLogger()
        logger.logCompilationError(error)
        assertEquals("- ERROR: ZERO-DIVISION-ERROR found at line 7 index 6:\n" +
                             " | x = 42/0\n" +
                             " |       ^--\n" +
                             " | Division by 0 found\n" +
                             " | -->help: Check your variables\n", logger.buffer.toString())
    }
    @org.junit.jupiter.api.Test
    fun testMissingArgumentError() {
        val error = MissingArgumentError(11, 2, "2 someFunction", "someFunction")
        val logger = TestLogger()
        logger.logCompilationError(error)
        assertEquals("- ERROR: MISSING-ARGUMENT-ERROR found at line 11 index 2:\n" +
                             " | 2 someFunction\n" +
                             " |   ^--\n" +
                             " | Missing argument for function 'someFunction'.\n" +
                             " | -->help: Have you included all arguments for function?\n", logger.buffer.toString())
    }
    @org.junit.jupiter.api.Test
    fun testMissingEncapsulation() {
        val error = MissingEncapsulationError(30, 0, "({()}")
        val logger = TestLogger()
        logger.logCompilationError(error)
        assertEquals("- ERROR: MISSING-ENCAPSULATION-ERROR found at line 30 index 0:\n" +
                             " | ({()}\n" +
                             " | ^--\n" +
                             " | Missing closing character for '('\n" +
                             " | -->help: Remember to always close encapsulations.\n", logger.buffer.toString())
    }
}
