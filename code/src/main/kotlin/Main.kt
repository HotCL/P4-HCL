import generation.SourceCodePrinter
import generation.cpp.ProgramGenerator
import lexer.Lexer
import parser.Parser

fun main(args: Array<String>) {
    val code = "" +
            "var apply = (func[num, num] f, num x): num {\n" +
            "x f\n" +
            "}\n" +
            "var x = { \n" +
            "var z = value \n" +
            "return z\n" +
            "} apply 7"

    val lexer = Lexer(code)
    println("Tokens: \n" + lexer.getTokenSequence().joinToString(",\n") { "${it.token::class.qualifiedName}" })
    val parser = Parser(lexer)
    val ast = parser.generateAbstractSyntaxTree()
    println("Ast: $ast")
    println(SourceCodePrinter().generate(ast))
    val programFiles = ProgramGenerator().generate(ast)
    programFiles.forEach { print("FILE: ${it.fileName}:\n\n${it.content}\n\n\n") }
}

