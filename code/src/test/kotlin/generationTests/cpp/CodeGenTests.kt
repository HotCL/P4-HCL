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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import parser.AstNode
import parser.Parser
import kotlin.system.exitProcess
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CodeGenTests {
    @Disabled
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

fun testReturnNoExplicitReturn() = listOf<AstNode.Command>()
fun testExplicitReturn5() = setRet(num(5))
fun testReturnVarX5PlusVarY10() = listOf(
    "x" declaredAs num withValue num(5),
    "y" declaredAs num withValue num(10),
    setRet("+" returning num calledWith  listOf("x".asIdentifier(num), "y".asIdentifier(num)))
)

fun testSimpleLambda() =
    listOf("x" declaredAs num withValue num(5),
        "myFun" declaredAs func(num, num) withValue (
            lambda() returning num withArgument ("param" asType num)
                andBody ret("+" returning num calledWith listOf("param".asIdentifier(num), "x".asIdentifier(num)))
            ),
        setRet("myFun" returning num calledWith listOf("myFun" returning num calledWith num(10)))
    )

fun testCreateList() =
    listOf("myList" declaredAs list(num) withValue list(num(1), num(5)),

        setRet("at" returning num calledWith listOf("myList".asIdentifier(list(num)), num(1)))
    )

fun testCreateTuple() =
    listOf("myTuple" declaredAs tpl(num,txt) withValue tpl(num(1), txt("hello")),

        setRet("element0" returning num calledWith listOf("myTuple".asIdentifier(tpl(num,txt))))
    )

fun testPrint() =
    listOf("print" returning none calledWith listOf(txt("hello world"))
    )

fun testGenericHighOrderFunction () = listOf(
        "retInputFunc" declaredAs func(generic("T"), listOf(func(generic("T")))) withValue (lambda()
                returning generic("T") withArgument ("inputFunc" asType generic("T")) andBody
                ret(("inputFunc" returning generic("T")).called())
                ),
        setRet("retInputFunc" returning generic("T") calledWith
                (lambda() returning num withArgument ("myNum" asType num) andBody ret(num(5)))
        )
)

fun testHighOrderFunction () = listOf(
        "modifyNumber" declaredAs func(num, listOf(num, func(num, num))) withValue (lambda()
                returning num withArguments listOf("myNum" asType num, "modFunction" asType func(num, num)) andBody
                    ret("modFunction" returning num calledWith "myNum".asIdentifier(num))
                ),
        setRet("modifyNumber" returning num calledWith listOf(
                num(5),
                lambda() returning num withArgument ("myNum" asType num) andBody
                        ret("+" returning num calledWith listOf("myNum".asIdentifier(num), num(7)))
                )
        )
)

fun testDeclarationAssignmentDeclarationToAssignedVariable() = listOf(
        "x" declaredAs num withValue num(5),
        "x" assignedTo num(7),
        "y" declaredAs num withValue "x".asIdentifier(num),
        setRet("y".asIdentifier(num))
)


object CodeGenerationTest : Spek({
    given("HCL CPP code generator") {
        listOf (
            testReturnNoExplicitReturn() shouldReturn 0,
            testExplicitReturn5() shouldReturn 5,
            testReturnVarX5PlusVarY10() shouldReturn 15,
            testSimpleLambda()  shouldReturn 20,
            testCreateList() shouldReturn 5,
            testCreateTuple() shouldReturn 1,
            testPrint() shouldReturn "hello world",
            testHighOrderFunction() shouldReturn 12,
            testGenericHighOrderFunction() shouldReturn 5,
            testDeclarationAssignmentDeclarationToAssignedVariable() shouldReturn 7
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
