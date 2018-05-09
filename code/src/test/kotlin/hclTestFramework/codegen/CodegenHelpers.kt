package hclTestFramework.codegen

import generation.FilePair
import generation.cpp.ProgramGenerator
import parser.AbstractSyntaxTree
import parser.AstNode
import utils.CommandResult
import utils.runCommand
import java.io.File



fun compileAndExecuteCpp(files: List<FilePair>) = try {
        val headerFiles = files.filter { it.fileName.endsWith(".h") }
        val cppFiles = files.filter { it.fileName.endsWith(".cpp") }
        headerFiles.forEach { it.print() }
        cppFiles.forEach {
            it.print()
            "g++ -c ${it.fileName} -o ${it.fileName.removeSuffix(".cpp")} -std=c++1z".apply {
                println(this)
                println(runCommand())
            }
        }
        println("Linking:")
        "g++ ${cppFiles.joinToString(" ") { it.fileName.removeSuffix(".cpp") }} -o program".apply {
            println(this)
            println(runCommand())
        }
        println("Running command:")
        "./program".run {
            println(this)
            runCommand()
        }
    } catch (e: Exception) {
        null
    }

fun compileAndExecuteForAst(astNodes: List<AstNode.Command>) =
        compileAndExecuteCpp(ProgramGenerator().generate(AbstractSyntaxTree(astNodes)))

private fun FilePair.print() = File(fileName).writeText(content)


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

