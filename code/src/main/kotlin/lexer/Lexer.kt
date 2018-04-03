package lexer

import kotlin.coroutines.experimental.buildSequence

/**
 * The default implementation of the ILexer interface
 */
class Lexer(private val inputContent: String) : ILexer {
    // we don't need to add \r\n as every line ending is removed and then each line is appended with \n
    private val endLines = listOf('\n',';')
    private val specialChars = listOf('=', '[', ']', '(', ')', '{', '}', ',', '\\', ':') + endLines
    private val endOfLineRegex = "\\r\\n|\\n|\\r".toRegex()

    override fun getTokenSequence(): Sequence<PositionalToken> = buildSequence {
        val currentString = StringBuilder()
        inputContent.split(endOfLineRegex).forEachIndexed lineIterator@{ lineNumber, line ->
            (line + '\n').forEachIndexed { indexNumber, char ->
                if (char !in listOf(' ', '\t')) currentString.append(char)
                if (char == '#'){
                    yield(PositionalToken(Token.SpecialChar.EndOfLine(), lineNumber, indexNumber))
                    currentString.setLength(0)
                    return@lineIterator
                }

                with(currentString.toString()) {
                    when {
                        isNotEmpty() -> when { char.isSpecialChar() -> char.getSpecialCharTokenOrNull()
                            isNextSpecialCharWhiteSpaceOrComment(lineNumber, indexNumber) -> when (this) {

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
                                "return" -> Token.Return()
                                else -> getLiteralOrIdentifier()
                            }
                            else -> null
                        }
                        else -> null
                    // special char
                    // identifier or number literal
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

    private fun isNextSpecialCharWhiteSpaceOrComment(lineNumber: Int, indexNumber: Int) = with(inputLine(lineNumber)) {
                val nextCharIndex = indexNumber + 1
                nextCharIndex < length && specialChars.any { get(nextCharIndex) == it }
                        || get(nextCharIndex).isWhitespace() || get(nextCharIndex) == '#'
            }

    override fun inputLine(lineNumber: Int) = inputContent.split(endOfLineRegex)[lineNumber] + '\n'

    private fun Char.isSpecialChar() = specialChars.any { it == this }

    private fun String.getLiteralOrIdentifier() = when {
    // Number literal
        matches("-?\\d+(\\.\\d+)?".toRegex()) -> Token.Literal.Number(this)
    // string/txt literal
        startsWith('\'') && endsWith('\'') -> Token.Literal.Text(this)
        startsWith('"') && endsWith('"') -> Token.Literal.Text(this)
    // Identifier
        startsWith('\'') || startsWith('"') -> throw Exception("TODO make this a lexer exception") // TODO make this a lexer exception
        else -> Token.Identifier(this)
    }

    private fun Char.getSpecialCharTokenOrNull(): Token? = when(this) {
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
