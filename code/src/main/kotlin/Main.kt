import lexer.Lexer
import parser.Parser

fun main(args: Array<String>) {
    val code = "" +
            "var ret+2 = (num x): num {  } \n" +
            "var ret+5 = (num x): num {  } \n" +
            "ret+2 ret+5\n"

    val lexer = Lexer(code)
    println("Tokens: \n" + lexer.getTokenSequence().joinToString(",\n") { "${it.token::class.qualifiedName}" })
    val parser = Parser(lexer)
    println("Ast: " + parser.generateAbstractSyntaxTree())
}

