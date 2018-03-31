package loggerTests

import exceptions.CompilationException
import exceptions.ErrorTypes
import exceptions.ParserException
import logger.Logger

class TokenTest {
    @org.junit.jupiter.api.Test
    fun testTypeError() {
        val lexicalError = CompilationException(ErrorTypes.TYPE_ERROR, 4, 10, "Operator '<' cannot be applied on operands 'num' and 'txt'", "var b = 4 < \"5\"", "Try casting to 'int'")
        val logger = Logger()
        logger.logCompilationError(lexicalError)
    }

    @org.junit.jupiter.api.Test
    fun testUndeclaredError() {
        val error = ParserException(ErrorTypes.UNDECLARED_IDENTIFIER_ERROR, 12, 8, "identifier 'textString' undeclared", "txt s = textString")
        val logger = Logger()
        logger.logCompilationError(error)
    }
    @org.junit.jupiter.api.Test
    fun testUnitializedError() {
        val error = ParserException(ErrorTypes.UNINITIALIZED_ERROR, 12, 4, "Use of uninitialized variable 'someUninitializedVariable'", "x = someUninitializedVariable + 10")
        val logger = Logger()
        logger.logCompilationError(error)
    }
    @org.junit.jupiter.api.Test
    fun testZeroDivisionError() {
        val error = ParserException(ErrorTypes.ZERO_DIVISION, 7, 6, "Division by 0 found", "x = 42/0")
        val logger = Logger()
        logger.logCompilationError(error)
    }
}
