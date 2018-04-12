import lexer.Lexer
import parser.Parser

fun main(args: Array<String>) {
    val code = "" +
            "list[num] myList = [5]\n" +
            "list[list[num]] myListList = [myList]"

    val lexer = Lexer(code)
    println("Tokens: \n" + lexer.getTokenSequence().joinToString(",\n") { "${it.token}" })
    val parser = Parser(lexer)
    println("Ast: " + parser.generateAbstractSyntaxTree())
}

