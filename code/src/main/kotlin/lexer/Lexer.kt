package lexer

import kotlin.coroutines.experimental.buildSequence

/**
 * The default implementation of the ILexer interface
 */
class Lexer(private val inputContent: String) : ILexer {
    private val endLines = listOf("\n","\r\n",";")
    private val specialChars = listOf("=", "[", "]", "(", ")", "{", "}", ",", "\\", "->", "<", ">", "\\") + endLines
    private val endOfLineRegex = "\\r\\n|\\n|\\r".toRegex()

    override fun getTokenSequence(): Sequence<PositionalToken> = buildSequence {
        val currentString = StringBuilder()
        inputContent.split(endOfLineRegex).forEachIndexed { lineNumber, line ->
            (line + "\n").forEachIndexed { indexNumber, char ->
                if (char !in listOf(' ', '\t')) currentString.append(char)
                with(currentString.toString()) {
                    when {
                    // types
                        equals("var") -> Token.Type.Var()
                        equals("none") -> Token.Type.None()
                        equals("txt") -> Token.Type.Text()
                        equals("num") -> Token.Type.Number()
                        equals("bool") -> Token.Type.Bool()
                        equals("tuple") -> Token.Type.Tuple()
                        equals("list") -> Token.Type.List()
                        equals("func") -> Token.Type.Func()
                    //bool
                        equals("true") -> Token.Literal.Bool(true)
                        equals("false") -> Token.Literal.Bool(false)
                    // special char
                        isSpecialChar() -> getSpecialCharTokenOrNull()
                    // identifier or number literal
                        upcomingSpecialCharOrWhiteSpace(lineNumber, indexNumber + 1) && isNotEmpty() -> getLiteralOrIdentifier()
                        else -> null
                    }.let {
                        if (it != null) {
                            yield(PositionalToken(it, lineNumber, indexNumber - (length - 1)))
                            currentString.setLength(0)
                        }
                    }
                }
            }
        }
    }

    private fun upcomingSpecialCharOrWhiteSpace(lineNumber: Int, indexNumber: Int) = with(inputLine(lineNumber)) {
        indexNumber < length && specialChars.any { substring(indexNumber).startsWith(it) } || get(indexNumber).isWhitespace()
    }

    override fun inputLine(lineNumber: Int) = inputContent.split(endOfLineRegex)[lineNumber] + "\n"

    private fun String.isSpecialChar() = specialChars.any { it == this }

    private fun String.getLiteralOrIdentifier() = when {
    // Number literal
        matches("-?\\d+(\\.\\d+)?".toRegex()) -> Token.Literal.Number(this)
    // string/txt literal
        startsWith("'") && endsWith("'") -> Token.Literal.Text(this)
        startsWith("\"") && endsWith("\"") -> Token.Literal.Text(this)
    // Identifier
        else -> Token.Identifier(this)
    }

    private fun String.getSpecialCharTokenOrNull(): Token? = when(this) {
        "=" -> Token.SpecialChar.Equals()
        "[" -> Token.SpecialChar.SquareBracketStart()
        "]" -> Token.SpecialChar.SquareBracketEnd()
        "{" -> Token.SpecialChar.BlockStart()
        "}" -> Token.SpecialChar.BlockEnd()
        "(" -> Token.SpecialChar.ParenthesesStart()
        ")" -> Token.SpecialChar.ParenthesesEnd()
        "," -> Token.SpecialChar.ListSeparator()
        "\\" -> Token.SpecialChar.LineContinue()
        "->" -> Token.SpecialChar.Arrow()
        in endLines -> Token.SpecialChar.EndOfLine()
        else -> null
    }
}
