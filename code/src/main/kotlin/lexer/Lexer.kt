package lexer

import exceptions.StringDoesntEndError
import kotlin.coroutines.experimental.buildSequence

/**
 * The default implementation of the ILexer interface
 */
class Lexer(
    private val inputFiles: Map<String, String>,
    private val additionalLines: Sequence<String> = emptySequence()
) : ILexer {
    // we don't need to add \r\n as every line ending is removed and then each line is appended with \n
    private val endLines = listOf('\n', ';')
    private val specialChars = listOf('=', '[', ']', '(', ')', '{', '}', ',', '\\', ':') + endLines

    private val endOfLineRegex = "\\r\\n|\\n|\\r".toRegex()

    private fun StringBuilder.isStartOfStringLiteral(char: Char) =
        this.isNotEmpty() && this[0] in listOf('\'', '"') &&
            (char !in listOf('\'', '"') || this.last() == '\\')

    override fun getTokenSequence(): Sequence<PositionalToken> = buildSequence {
        val currentString = StringBuilder()
        inputFiles.forEach { filePair ->
            val fileName = filePair.key
            val inputContent = filePair.value
            yieldAll(lexLines(inputContent.split(endOfLineRegex).asSequence(), currentString, fileName))
        }
        additionalLines.forEachIndexed { index, line ->
            yieldAll(lexLine(line + "\n", currentString).map {
                PositionalToken(it.token, index, it.lineIndex, "REPL")
            } + PositionalToken(Token.SpecialChar.EndOfFile, 0, 0, "REPL"))
        }
    }

    private fun lexLines(sequence: Sequence<String>, currentString: StringBuilder, fileName: String) =
        sequence.mapIndexed { lineNumber, line ->
            val lineTokens = try {
                lexLine(line + "\n", currentString)
            } catch (stringNotEndError: StringDoesntEndError) {
                stringNotEndError.run {
                    throw StringDoesntEndError(lineNumber, lineIndex, lineText)
                }
            }
            lineTokens.map { PositionalToken(it.token, lineNumber, it.lineIndex, fileName) }
        }.flatten() + PositionalToken(Token.SpecialChar.EndOfFile, 0, 0, fileName)

    private fun lexLine(line: String, currentString: StringBuilder): List<LineToken> {
        val tokens = mutableListOf<LineToken>()
        line.forEachIndexed { indexNumber, char ->
            // handle if we are reading string literal
            if ((currentString.isEmpty() && char in listOf('\'', '"')) ||
                    currentString.isStartOfStringLiteral(char)) {
                if (char == '\n') {
                    throw StringDoesntEndError(0, indexNumber, currentString.toString())
                }
                currentString.append(char)
                return@forEachIndexed
            }
            if (char !in listOf(' ', '\t'))
                currentString.append(char)
            if (char == '#') {
                tokens.add(LineToken(Token.SpecialChar.EndOfLine, indexNumber))
                currentString.setLength(0)
                return tokens
            }
            if (char == '\\' && line[indexNumber + 1] == '\n') {
                currentString.setLength(0)
                return tokens
            }

            with(currentString.toString()) {
                (if (isNotEmpty())
                    when {
                    // special char
                        char.isSpecialChar() -> char.getSpecialCharTokenOrNull()
                        isNextSpecialCharWhiteSpaceOrComment(indexNumber, line) -> when (this) {
                        // types
                            "var" -> lexer.Token.Type.Var
                            "none" -> lexer.Token.Type.None
                            "txt" -> lexer.Token.Type.Text
                            "num" -> lexer.Token.Type.Number
                            "bool" -> lexer.Token.Type.Bool
                            "tuple" -> lexer.Token.Type.Tuple
                            "list" -> lexer.Token.Type.List
                            "func" -> lexer.Token.Type.Func
                        // bool
                            "true" -> lexer.Token.Literal.Bool(true)
                            "false" -> lexer.Token.Literal.Bool(false)
                            "return" -> lexer.Token.Return
                        // identifier or number literal
                            else -> getLiteralOrIdentifier()
                        }
                        else -> null
                    } else null).let {
                    if (it != null) {
                        tokens.add(LineToken(it, indexNumber - (length - 1)))
                        currentString.setLength(0)
                    }
                }
            }
        }
        return tokens
    }

    private fun isNextSpecialCharWhiteSpaceOrComment(indexNumber: Int, line: String): Boolean {
        val nextCharIndex = indexNumber + 1
        return nextCharIndex < line.length && specialChars.any {
            line[nextCharIndex] == it
        } || line[nextCharIndex].isWhitespace() || line[nextCharIndex] == '#'
    }

    override fun inputLine(lineNumber: Int, file: String) = inputFiles[file]!!.split(endOfLineRegex)[lineNumber] + '\n'

    private fun Char.isSpecialChar() = specialChars.any { it == this }

    private fun String.getLiteralOrIdentifier() = when {
    // Number literal
        matches("-?\\d+(\\.\\d+)?".toRegex()) -> Token.Literal.Number(toDouble())
    // string/txt literal
        startsWith('\'') && endsWith('\'') -> Token.Literal.Text(drop(1).dropLast(1))
        startsWith('"') && endsWith('"') -> Token.Literal.Text(drop(1).dropLast(1))
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

    data class LineToken(val token: Token, val lineIndex: Int)
}
