package interpreterTests

import interpreter.kotlin.KtInterpreter
import lexer.Lexer
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import parser.kotlin.KtParser
import stdlib.Stdlib
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

object TestHclPrograms : Spek({
    val files = listOf(
            "mapTuples.hcl",
            "HelloWorld.hcl",
            "ReturnSimple.hcl",
            "HelloWorldAndReturn.hcl",
            "stringConcat.hcl",
            "thenElse.hcl",
            "while.hcl",
            "stringAt.hcl",
            "printTuple.hcl",
            "conclusionSnippet.hcl",
            "fizzBuzz.hcl",
            "MapFilter.hcl",
            "staticScope.hcl",
            "subText.hcl",
            "toUneven.hcl",
            "multiScope.hcl",
            "OOP.hcl",
            "OOP_V2.hcl",
            "PrintFibonacci.hcl",
            "Swap.hcl",
            "bubbleSort.hcl",
            "testFirstIndexWhereStdlib.hcl",
            "conclusionExampleNicolaj.hcl",
            "aTupleInFunction.hcl"
    )
    files.filter { it.endsWith(".hcl") }.forEach { file ->
        given(file) {
            val fileContent = javaClass.classLoader.getResource(file).readText()
            val constraints = fileContent.split("\n").first().split("should ").drop(1).map { it.split(" ") }
            val expectedReturn = constraints.firstOrNull { it.first() == "return" }?.get(1)?.toInt() ?: 0
            val expectedPrint = constraints.firstOrNull { it.first() == "print" }?.drop(1)?.joinToString(" ") ?: ""
            if (fileContent.contains("TEST_DISABLED")) {
                it("should not be executed") {}
            } else it("should return $expectedReturn and print \"$expectedPrint\"") {
                val input = mapOf(Stdlib.getStdlibContent(), file to fileContent )
                val lexer = Lexer(input)
                val parser = KtParser(lexer)
                val interpreter = KtInterpreter(parser)
                val outputStream = ByteArrayOutputStream()
                // System.setOut(PrintStream(outputStream))
                val returnCode = interpreter.run()
                // System.setOut(System.out)
                assertEquals(expectedReturn, returnCode,
                        "expected RETURN_CODE=$expectedReturn. was $returnCode.\n" +
                                "full output:\n$returnCode")
                // assertEquals(expectedPrint, outputStream.toString(),
                //        "Expected PRINT=$expectedPrint. was $outputStream.\n")
            }
        }
    }
})