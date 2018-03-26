package loggerTests

import exceptions.LexerException
import exceptions.ParserException
import logger.Logger

class TokenTest {
    @org.junit.jupiter.api.Test
    fun testLogLexicalError() {
        val lexicalError = LexerException("Lexical error occured!", 5, 13, "Some fancy line with lexcial error.")
        val logger = Logger()
        logger.logCompilationError(lexicalError)
    }

    @org.junit.jupiter.api.Test
    fun testLogParserError() {
        val parserError = ParserException("Parser error occured!", 5, 13, "Some fancy line with lexcial error.")
        val logger = Logger()
        logger.logCompilationError(parserError)
    }
}
