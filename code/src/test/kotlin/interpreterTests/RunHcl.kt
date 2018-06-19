package interpreterTests

import exceptions.CompilationException
import interpreter.kotlin.KtInterpreter
import lexer.Lexer
import logger.Logger
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import parser.kotlin.KtParser

class RunHcl {
    @Test @Disabled
    fun run() {
        val hclCode =
"""
5 + 7 print
x print
"""
        val lexer = Lexer(mapOf("input" to hclCode))
        val parser = KtParser(lexer)
        val interpreter = KtInterpreter(parser)
        val logger = Logger()
        try {
            interpreter.run()
        } catch (exception: CompilationException) {
            logger.logCompilationError(exception)
        }
    }
}
