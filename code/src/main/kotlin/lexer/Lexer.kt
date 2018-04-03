package lexer

import kotlin.coroutines.experimental.buildSequence

/**
 * The default implementation of the ILexer interface
 */
class Lexer(private val inputContent: String) : ILexer {
    private val endLines = listOf('\n',';')
    private val specialChars = listOf('=', '[', ']', '(', ')', '{', '}', ',', '\\', ':') + endLines
    private val endOfLineRegex = "\\r\\n|\\n|\\r".toRegex()

    override fun getTokenSequence(): Sequence<PositionalToken> = buildSequence {
        val currentString = StringBuilder()
        inputContent.split(endOfLineRegex).forEachIndexed { lineNumber, line ->
            (line + "\n").forEachIndexed { indexNumber, char ->
                if (char !in listOf(' ', '\t')) currentString.append(char)
                with(currentString.toString()) {
                    when {
                        isNotEmpty() && isSpecialChar() -> getSpecialCharTokenOrNull()
                        upcomingSpecialCharOrWhiteSpace(lineNumber, indexNumber+1) && isNotEmpty() -> when (this) {

                        // types
                            "var" -> Token.Type.Var()
                            "none" -> Token.Type.None()
                            "txt" -> Token.Type.Text()
                            "num" -> Token.Type.Number()
                            "bool" -> Token.Type.Bool()
                            "tuple" -> Token.Type.Tuple()
                            "list" -> Token.Type.List()
                            "func" -> Token.Type.Func()
                        //bool
                            "true" -> Token.Literal.Bool(true)
                            "false" -> Token.Literal.Bool(false)
                            else -> getLiteralOrIdentifier()
                        }
                    // special char
                    // identifier or number literal
                        else -> null
                    }.let {
                        if (it != null) {
                            yield(PositionalToken(it, lineNumber, indexNumber - (length-1)))
                            currentString.setLength(0)
                        }
                    }
                }
            }
        }
    }

    private fun upcomingSpecialCharOrWhiteSpace(lineNumber: Int, indexNumber: Int) = with(inputLine(lineNumber)) {
        indexNumber < length && specialChars.any { get(indexNumber) == it } || get(indexNumber).isWhitespace()
    }

    override fun inputLine(lineNumber: Int) = inputContent.split(endOfLineRegex)[lineNumber] + "\n"

    private fun String.isSpecialChar() = specialChars.any { it == this[0] }

    private fun String.getLiteralOrIdentifier() = when {
    // Number literal
        matches("-?\\d+(\\.\\d+)?".toRegex()) -> Token.Literal.Number(this)
    // string/txt literal
        startsWith("'") && endsWith("'") -> Token.Literal.Text(this)
        startsWith("\"") && endsWith("\"") -> Token.Literal.Text(this)
    // Identifier
        else -> Token.Identifier(this)
    }

    private fun String.getSpecialCharTokenOrNull(): Token? = when(this[0]) {
        '=' -> Token.SpecialChar.Equals()
        '[' -> Token.SpecialChar.SquareBracketStart()
        ']' -> Token.SpecialChar.SquareBracketEnd()
        '{' -> Token.SpecialChar.BlockStart()
        '}' -> Token.SpecialChar.BlockEnd()
        '(' -> Token.SpecialChar.ParenthesesStart()
        ')' -> Token.SpecialChar.ParenthesesEnd()
        ',' -> Token.SpecialChar.ListSeparator()
        '\\' -> Token.SpecialChar.LineContinue()
        ':' -> Token.SpecialChar.Colon()
        in endLines.apply { toString() } -> Token.SpecialChar.EndOfLine()
        else -> null
    }
}
