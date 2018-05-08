package generationTests.cpp

import exceptions.CompilationException
import generation.cpp.ProgramGenerator
import hclTestFramework.codegen.compileAndExecuteCpp
import lexer.Lexer
import logger.Logger
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import parser.Parser
import kotlin.system.exitProcess

class CodeGenTests {
    @Disabled
    @Test
    fun tempCoverageTest() {
        val code = "" +
                "tuple[num, txt] t\n" +
                "tuple[list[num], func[none]] y\n" +
                "var myVar = 5\n" +
                "var plus5 = (num x): num { x + myVar }\n" +
                "var shouldBe10 = myVar plus5"

        val lexer = Lexer(code)
        val parser = Parser(lexer)
        val logger = Logger()
        val ast = try {
            parser.generateAbstractSyntaxTree()
        } catch (exception: CompilationException) {
            logger.logCompilationError(exception)
            exitProcess(-1)
        }

        val programFiles = ProgramGenerator().generate(ast)
        compileAndExecuteCpp(programFiles)
    }
}
