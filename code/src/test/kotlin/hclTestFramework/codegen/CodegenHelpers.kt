package hclTestFramework.codegen

import generation.FilePair
import generation.cpp.ProgramGenerator
import hclTestFramework.parser.assignedTo
import parser.AbstractSyntaxTree
import parser.AstNode
import utils.CommandResult
import utils.compileCpp
import utils.runCommand
import java.io.File

fun compileAndExecuteCpp(files: List<FilePair>, dir: String, keepFiles: Boolean = false): CommandResult? {
    compileCpp(files, dir, keepFiles)
    val programFile = File("program")
    return try {
        programFile.setExecutable(true)
        programFile.absolutePath.runCommand()
    } catch (e: Exception) {
        null
    } finally {
        File("program").delete()
    }
}

fun compileAndExecuteForAst(astNodes: List<AstNode.Command>) =
        compileAndExecuteCpp(ProgramGenerator().generate(AbstractSyntaxTree(astNodes)), "testDir")

sealed class ExpectedResult
data class TextOutput(val string: String) : ExpectedResult()
data class ReturnValue(val value: Int) : ExpectedResult()
data class TextAndReturn(val string: String, val value: Int) : ExpectedResult()

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
