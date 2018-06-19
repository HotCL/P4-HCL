import exceptions.CompilationException
import interpreter.kotlin.KtInterpreter
import lexer.Lexer
import lexer.PositionalToken
import lexer.Token
import logger.Logger
import org.jline.reader.*
import org.jline.reader.impl.completer.StringsCompleter
import parser.kotlin.KtParser
import stdlib.Stdlib
import kotlin.coroutines.experimental.buildSequence
import kotlin.system.exitProcess
import org.jline.utils.AttributedStyle
import org.jline.utils.AttributedStringBuilder
import org.jline.reader.LineReader
import org.jline.utils.AttributedString


class REPL {
    private val completions = listOf(
            "var", "map", "where", "equals", "value", "at", "element0", "element1", "mod", "toText", "length",
            "and", "or", "then", "thenElse", "greaterThan", "lessThan", "notEquals", "greaterThanEqual", "lessThanEqual",
            "print", "splitAt", "to", "while", "in", "all", "any", "forEach", "firstIndexWhere", "notIn",
            "num", "txt", "bool", "list", "tpl"
    )
    private val reader = LineReaderBuilder.builder()
            .completer(StringsCompleter(completions))
            .highlighter(DefaultHighlighter2())
            .build()
    private val previousContent = mutableListOf<String>()

    private fun readLine() = try {
        reader.readLine(">>> ")
    } catch (cancel: UserInterruptException) {
        println("Quit!")
        exitProcess(0)
    }

    private fun inputSequence() = buildSequence {
        while (true) {
            val readLine = readLine()
            yield(readLine)
            previousContent.add(readLine)
        }
    }

    fun start() {
        while (true) {
            try {
                val logger = Logger()
                val lexer = Lexer(mapOf(Stdlib.getStdlibContent()), previousContent.asSequence() + inputSequence())
                val parser = KtParser(lexer)
                try {
                    KtInterpreter(parser).apply { printExpression = true }.run()
                } catch (exception: CompilationException) {
                    logger.logCompilationError(exception)
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                continue
            }
        }
    }
}

class DefaultHighlighter2: Highlighter {
    override fun highlight(reader: LineReader, buffer: String): AttributedString {
        val lexer = Lexer(mapOf("line" to buffer))
        val tokens = lexer.getTokenSequence().toList()

        val sb = AttributedStringBuilder()
        (0 until buffer.length).forEach {
            val currentToken = tokens.currentToken(it)
            if (it == currentToken?.lineIndex) {
                sb.style(currentToken.style)
            }
            val c = buffer[it]
            sb.append(c)
        }
        return sb.toAttributedString()
    }
}

fun List<PositionalToken>.currentToken(lineIndex: Int) = firstOrNull { lineIndex <= it.lineIndex }

val PositionalToken.style get(): AttributedStyle = when (token) {
    is Token.Identifier -> AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE)
    is Token.Literal.Text -> AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
    is Token.Literal.Number,
    is Token.Literal.Bool -> AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)
    Token.SpecialChar.BlockStart,
    Token.SpecialChar.BlockEnd,
    Token.SpecialChar.SquareBracketStart,
    Token.SpecialChar.SquareBracketEnd,
    Token.SpecialChar.ParenthesesStart,
    Token.SpecialChar.ParenthesesEnd,
    Token.SpecialChar.EndOfLine,
    Token.SpecialChar.EndOfFile,
    Token.SpecialChar.ListSeparator,
    Token.SpecialChar.Equals,
    Token.SpecialChar.Colon -> AttributedStyle.DEFAULT
    Token.Type.Var,
    Token.Type.None,
    Token.Type.Text,
    Token.Type.Number,
    Token.Type.Bool,
    Token.Type.Tuple,
    Token.Type.List,
    Token.Type.Func -> AttributedStyle.DEFAULT.foreground(AttributedStyle.MAGENTA)
    Token.Return -> AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN)
}