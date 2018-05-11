import exceptions.CompilationException
import generation.cpp.ProgramGenerator
import lexer.Lexer
import logger.Logger
import parser.Parser
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val code = "var isBøs = true\n" +
        "isBøs then { \"du bøs\" print }\n" +
        "isBøs not then { \"du ik bøs\" print }"

    val lexer = Lexer(code)
    val parser = Parser(lexer)
    val logger = Logger()
    val ast = try {
         parser.generateAbstractSyntaxTree()
    } catch (exception: CompilationException) {
        logger.logCompilationError(exception)
        exitProcess(-1)
    }
    //println("Ast: $ast")
    //println(SourceCodePrinter().generate(ast))
    val programFiles = ProgramGenerator().generate(ast)
    programFiles.forEach { print("FILE: ${it.fileName}:\n\n${it.content}\n\n\n") }
}

