package loggerTests

import exceptions.LexerException
import exceptions.ParserException
import logger.Logger

class TokenTest {
    @org.junit.jupiter.api.Test
    fun testLogLexicalError() {
        val lexicalErrorAllInfo = LexerException("Lexical error occured!", 5, 13, "Some fancy line with lexcial error.")
        val lexicalErrorWithLineAndIndex = LexerException("Lexical error occured!", 5, 13)
        val lexicalErrorWithNoInfo = LexerException("Lexical error occured!")
        val logger = Logger()
        logger.logCompilationError(lexicalErrorAllInfo)
        logger.logCompilationError(lexicalErrorWithLineAndIndex)
        logger.logCompilationError(lexicalErrorWithNoInfo)
    }

    @org.junit.jupiter.api.Test
    fun testLogParserError() {
        val parserErrorAllInfo = ParserException("Parser error occured!", 5, 13, "Some fancy line with lexcial error.")
        val parserErrorWithLineAndIndex = ParserException("Parser error occured!", 5, 13)
        val parserErrorWithNoInfo = ParserException("Parser error occured!")
        val logger = Logger()
        logger.logCompilationError(parserErrorAllInfo)
        logger.logCompilationError(parserErrorWithLineAndIndex)
        logger.logCompilationError(parserErrorWithNoInfo)
    }
}
