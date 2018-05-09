package parserTests

import hclTestFramework.lexer.buildTokenSequence
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import parser.ParserWithoutBuiltins

class MiscellaneousTests {
    @Test
    fun failsOnLineStartsWithEquals() {
        val lexer = DummyLexer(buildTokenSequence {
            `=`.newLine
        })
        assertThrows(Exception::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }
}
