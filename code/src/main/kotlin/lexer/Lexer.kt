package lexer

import exceptions.StringDoesntEndError
import kotlin.coroutines.experimental.buildSequence

/**
 * The default implementation of the ILexer interface
 */
class Lexer(private val inputContent: String) : ILexer {
    // we don't need to add \r\n as every line ending is removed and then each line is appended with \n
    private val endLines = listOf('\n', ';')
    private val specialChars = listOf('=', '[', ']', '(', ')', '{', '}', ',', '\\', ':') + endLines

    private val endOfLineRegex = "\\r\\n|\\n|\\r".toRegex()

    private fun StringBuilder.isStartOfStringLiteral(char: Char) =
        this.isNotEmpty() && this[0] in listOf('\'', '"') &&
            (char !in listOf('\'', '"') || this.last() == '\\')

    override fun getTokenSequence(): Sequence<PositionalToken> = buildSequence {
        val currentString = StringBuilder()
        inputContent.split(endOfLineRegex).forEachIndexed lineIterator@{ lineNumber, line ->
            (line + '\n').forEachIndexed { indexNumber, char ->
                // handle if we are reading string literal
                if ((currentString.isEmpty() && char in listOf('\'', '"')) ||
                    currentString.isStartOfStringLiteral(char)) {
                    if (char == '\n') {
                        throw StringDoesntEndError(lineNumber, indexNumber, currentString.toString())
                    }
                    currentString.append(char)
                    return@forEachIndexed
                }
                if (char !in listOf(' ', '\t'))
                    currentString.append(char)
                if (char == '#') {
                    yield(PositionalToken(Token.SpecialChar.EndOfLine, lineNumber, indexNumber))
                    currentString.setLength(0)
                    return@lineIterator
                }
                if (char == '\\' && inputLine(lineNumber)[indexNumber + 1] == '\n') {
                    currentString.setLength(0)
                    return@lineIterator
                }

                with(currentString.toString()) {
                    (if (isNotEmpty())
                        when {
                        // special char
                            char.isSpecialChar() -> char.getSpecialCharTokenOrNull()
                            isNextSpecialCharWhiteSpaceOrComment(lineNumber, indexNumber) -> when (this) {
                            // types
                                "var" -> Token.Type.Var
                                "none" -> Token.Type.None
                                "txt" -> Token.Type.Text
                                "num" -> Token.Type.Number
                                "bool" -> Token.Type.Bool
                                "tuple" -> Token.Type.Tuple
                                "list" -> Token.Type.List
                                "func" -> Token.Type.Func
                            // bool
                                "true" -> Token.Literal.Bool(true)
                                "false" -> Token.Literal.Bool(false)
                                "return" -> Token.Return
                            // identifier or number literal
                                else -> getLiteralOrIdentifier()
                            }
                            else -> null
                        } else null).let {
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
        nextCharIndex < length && specialChars.any { get(nextCharIndex) == it } ||
            get(nextCharIndex).isWhitespace() || get(nextCharIndex) == '#'
    }

    override fun inputLine(lineNumber: Int) = inputContent.split(endOfLineRegex)[lineNumber] + '\n'

    private fun Char.isSpecialChar() = specialChars.any { it == this }

    private fun String.getLiteralOrIdentifier() = when {
    // Number literal
        matches("-?\\d+(\\.\\d+)?".toRegex()) -> Token.Literal.Number(this.toDouble())
    // string/txt literal
        startsWith('\'') && endsWith('\'') -> Token.Literal.Text(this.drop(1).dropLast(1))
        startsWith('"') && endsWith('"') -> Token.Literal.Text(this.drop(1).dropLast(1))
    // Identifier
        else -> Token.Identifier(this)
    }

    private fun Char.getSpecialCharTokenOrNull(): Token? = when (this) {
        '=' -> Token.SpecialChar.Equals
        '[' -> Token.SpecialChar.SquareBracketStart
        ']' -> Token.SpecialChar.SquareBracketEnd
        '{' -> Token.SpecialChar.BlockStart
        '}' -> Token.SpecialChar.BlockEnd
        '(' -> Token.SpecialChar.ParenthesesStart
        ')' -> Token.SpecialChar.ParenthesesEnd
        ',' -> Token.SpecialChar.ListSeparator
        ':' -> Token.SpecialChar.Colon
        in endLines -> Token.SpecialChar.EndOfLine
        else -> null
    }
}
