import lexer.Lexer

fun main(args: Array<String>) {
    val lexer = Lexer()
    val tokens = lexer.lexStuff("My fancy string to lex")
    tokens.forEach(::println)
}

