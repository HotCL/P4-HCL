package loggerTests

import exceptions.CompilationException
import exceptions.ErrorTypes
import exceptions.ParserException
import logger.Logger

class TokenTest {
    @org.junit.jupiter.api.Test
    fun testTypeError() {
        val lexicalError = CompilationException(ErrorTypes.TYPE_ERROR, "Operator '<' cannot be applied on operands 'num' and 'txt'", 4, 10, "var b = 4 < \"5\".")
        val logger = Logger()
        logger.logCompilationError(lexicalError)
    }

    @org.junit.jupiter.api.Test
    fun testUndeclaredError() {
        val error = ParserException(ErrorTypes.UNDECLARED_IDENTIFIER_ERROR, "identifier 'textString' undeclared", 12, 8, "txt s = textString")
        val logger = Logger()
        logger.logCompilationError(error)
    }
    @org.junit.jupiter.api.Test
    fun testUnitializedError() {
        val error = ParserException(ErrorTypes.UNINITIALIZED_ERROR, "Use of uninitialized variable 'someUninitializedVariable'", 12, 4, "x = someUninitializedVariable + 10")
        val logger = Logger()
        logger.logCompilationError(error)
    }
    @org.junit.jupiter.api.Test
    fun testZeroDivisionError() {
        val error = ParserException(ErrorTypes.ZERO_DIVISION, "Division by 0 found", 7, 6, "x = 42/0")
        val logger = Logger()
        logger.logCompilationError(error)
    }
}
