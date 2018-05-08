import exceptions.CompilationException
import generation.cpp.ProgramGenerator
import lexer.Lexer
import logger.Logger
import parser.Parser
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val code = "" +
            "var thenElse = (bool condition, func[T] trueBody, func[T] falseBody): T {\n" +
            "T output\n"+
            "condition then {output = trueBody}\n" +
            "condition not then { output = falseBody}\n"+
            "return output\n"+
            "}\n" +
            "var x = 21\n"+
            "var y = 20\n"+
            "num bigNum = x > y thenElse { x } { y }\n"+
            "return bigNum"

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

