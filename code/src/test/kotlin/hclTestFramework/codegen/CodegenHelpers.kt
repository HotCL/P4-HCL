package hclTestFramework.codegen

import generation.FilePair
import generation.cpp.ProgramGenerator
import hclTestFramework.parser.assignedTo
import parser.AbstractSyntaxTree
import parser.AstNode
import utils.CommandResult
import utils.runCommand
import java.io.File

fun compileAndExecuteCpp(files: List<FilePair>): CommandResult? {
    val testDir = "testDir"
    File(testDir).mkdir()
    return try {
        val headerFiles = files.filter { it.fileName.endsWith(".h") }
        val cppFiles = files.filter { it.fileName.endsWith(".cpp") }
        headerFiles.forEach { it.writeFile(testDir) }
        cppFiles.forEach {
            it.writeFile(testDir)
            "g++ -c ${it.fileName} -o ${it.fileName.removeSuffix(".cpp")} -std=c++11".apply {
                println(this)
                println(runCommand(File("./testDir")))
            }
        }
        println("Linking:")
        "g++ ${cppFiles.joinToString(" ") { it.fileName.removeSuffix(".cpp") }} -o program".apply {
            println(this)
            println(runCommand(File("./testDir")))
        }
        println("Running command:")
        "./program".run {
            println(this)
            runCommand(File("./testDir"))
        }
    } catch (e: Exception) {
        null
    } finally {
        File(testDir).deleteRecursively()
    }
}

fun compileAndExecuteForAst(astNodes: List<AstNode.Command>) =
        compileAndExecuteCpp(ProgramGenerator().generate(AbstractSyntaxTree(astNodes)))

private fun FilePair.writeFile(dir: String) = File("$dir/$fileName").writeText(content)


sealed class ExpectedResult
data class TextOutput(val string: String): ExpectedResult()
data class ReturnValue(val value: Int): ExpectedResult()
data class TextAndReturn(val string: String, val value: Int): ExpectedResult()

data class TestCase(val astNodes: List<AstNode.Command>, val expectedResult: ExpectedResult)

infix fun List<AstNode.Command>.shouldReturn(expectedResult: ExpectedResult) = TestCase(this, expectedResult)
infix fun List<AstNode.Command>.shouldReturn(expectedResult: Int) = TestCase(this, ReturnValue(expectedResult))
infix fun List<AstNode.Command>.shouldReturn(expectedResult: String) = TestCase(this, TextOutput(expectedResult))

infix fun AstNode.Command.shouldReturn(expectedResult: ExpectedResult) = TestCase(listOf(this), expectedResult)
infix fun AstNode.Command.shouldReturn(expectedResult: Int) = TestCase(listOf(this), ReturnValue(expectedResult))
infix fun AstNode.Command.shouldReturn(expectedResult: String) = TestCase(listOf(this), TextOutput(expectedResult))

infix fun Int.and(string: String) = TextAndReturn(string, this)
infix fun String.and(int: Int) = TextAndReturn(this, int)

fun setRet(code: AstNode.Command.Expression) = "RETURN_CODE" assignedTo code

