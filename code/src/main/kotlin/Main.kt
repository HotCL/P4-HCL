import exceptions.CompilationException
import generation.cpp.ProgramGenerator
import lexer.Lexer
import logger.Logger
import parser.Parser
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val code = "" +
            "tuple[num, txt] t\n" +
            "tuple[list[num], func[none]] y\n" +
            "list[num] myList=[1,2,3,4.0]\n" +
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
    //println("Ast: $ast")
    //println(SourceCodePrinter().generate(ast))
    val programFiles = ProgramGenerator().generate(ast)
    programFiles.forEach { print("FILE: ${it.fileName}:\n\n${it.content}\n\n\n") }
}

