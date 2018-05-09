package generationTests.cpp

import builtins.HclBuiltinFunctions
import exceptions.CompilationException
import generation.cpp.ProgramGenerator
import hclTestFramework.codegen.*
import hclTestFramework.parser.*
import lexer.Lexer
import logger.Logger
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Test
import parser.AstNode
import parser.Parser
import kotlin.system.exitProcess
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CodeGenTests {
    @Test
    fun tempCoverageTest() {
        val code = "tuple[num, txt] t\n" +
                   "tuple[list[num], func[none]] y\n" +
                   "var myList = [1, 2, 3]"
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


object CodeGenerationTest : Spek({
    given("HCL CPP code generator") {
        listOf (
                // Return 0 by default
            listOf<AstNode.Command>() shouldReturn 0,
                // Hardcode return 5
            ret(num(5)) shouldReturn 5,
                // Return var x (5) + var y (10)
            listOf(
                    "x" declaredAs num withValue num(5),
                    "y" declaredAs num withValue num(10),
                    ret("+" calledWith listOf("x".asIdentifier, "y".asIdentifier))
            ) shouldReturn 15,
            listOf(
                    "myFun" declaredAs func(num, func(num)) withValue (
                            lambda() returning num withArgument ("myNestedFunc" asType func(num))
                                    andBody ("myNestedFunc".called())
                            ),
                    ret("myFun" calledWith (lambda() returning num andBody ret(num(5))))
            ) shouldReturn 5
        ).forEach { testCase ->
            on("the AST nodes: ${testCase.astNodes}") {
                val expectedResult: String = when (testCase.expectedResult) {
                    is TextOutput -> "print: ${testCase.expectedResult.string}"
                    is ReturnValue -> "return: ${testCase.expectedResult.value}"
                    is TextAndReturn -> "print: ${testCase.expectedResult.string} and return: ${testCase.expectedResult.value}"
                }
                it("should $expectedResult") {
                    val result = compileAndExecuteForAst(HclBuiltinFunctions.functions + testCase.astNodes)
                    assertNotNull(result)
                    when (testCase.expectedResult) {
                        is TextOutput -> assertEquals(testCase.expectedResult.string, result!!.string)
                        is ReturnValue -> assertEquals(testCase.expectedResult.value, result!!.returnValue)
                        is TextAndReturn -> {
                            assertEquals(testCase.expectedResult.string, result!!.string)
                            assertEquals(testCase.expectedResult.value, result.returnValue)
                        }
                    }
                }
            }
        }
    }
})
