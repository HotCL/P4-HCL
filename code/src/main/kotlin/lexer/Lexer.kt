package lexer

import com.natpryce.hamkrest.matches
import java.util.regex.Pattern.matches
import kotlin.coroutines.experimental.buildSequence

/**
 * The default implementation of the ILexer interface
 */
class Lexer(private val inputContent: String) : ILexer {
    var lineIndex = 0
    var lineNumber = 0
    var charNumber = 0
    // TODO make this less shitty
    override fun getTokenSequence(): Sequence<PositionalToken> = buildSequence {
        var remainingChars = inputContent.substring(charNumber)
        // sets token when we have a match. otherwise it keeps popping.
        // first iteration nothing happens as current_string is empty. this is done to be DRY.
        while(remainingChars.isNotEmpty()) {
            var currentString = ""
            var token:Token? = null
            while (token == null) {
                if (remainingChars.take(1) == " " && currentString == "") {
                    remainingChars = inputContent.substring(++charNumber)
                    lineIndex++
                    continue
                }
                // if the next chars is a specialChar then the current string must be a literal
                token = getSpecialCharOrNull(currentString)
                // if token is null AND the next char is either a space or the one or two next chars are a special char
                // then what is currently in the buffer is a separate token.
                // for instance with currentString == "bool" and 3next 2 chars being "->" then we need bool to be a token and then "yield" here.
                // if however the next char is not a special char (for instance just an "f" then we need to continue popping.
                if (token == null && currentString != "" && (remainingChars.isEmpty() || (remainingChars.take(1) == " " ||
                                (getSpecialCharOrNull(remainingChars.take(1)) != null ||
                                        getSpecialCharOrNull(remainingChars.take(2)) != null)))) {

                    token = when (currentString) {
                    //bool
                        "true" -> Token.Literal.Bool(true)
                        "false" -> Token.Literal.Bool(false)
                    // types
                        "var" -> Token.Type.Var()
                        "none" -> Token.Type.None()
                        "txt" -> Token.Type.Text()
                        "num" -> Token.Type.Number()
                        "bool" -> Token.Type.Bool()
                        "tuple" -> Token.Type.Tuple()
                        "list" -> Token.Type.List()
                        "func" -> Token.Type.Func()
                        else -> null
                    }
                    if (token == null) {
                        // number literal
                        token = if (currentString.matches("-?\\d+(\\.\\d+)?".toRegex()))
                            Token.Literal.Number(currentString)
                        // string/text literal
                        else if ((currentString.startsWith("'") && currentString.endsWith("'")) || currentString.startsWith('"') && currentString.endsWith('"'))
                            Token.Literal.Text(currentString)
                        else
                            Token.Identifier(currentString)
                    }
                }

                if (token == null) {
                    currentString += remainingChars.take(1)
                    remainingChars = inputContent.substring(++charNumber)
                    lineIndex++
                }
            }
            yield(PositionalToken(token,lineNumber,lineIndex-currentString.length))
        }
    }


    override fun inputLine(lineNumber: Int) = inputContent.split("\n")[lineNumber]

    private fun getSpecialCharOrNull(current_string: String): Token? {
        val endlines = arrayListOf("\n","\r\n",";")
        return when(current_string)
        {
            "=" -> Token.SpecialChar.Equals()
            "[" -> Token.SpecialChar.SquareBracketStart()
            "]" -> Token.SpecialChar.SquareBracketEnd()
            "{" -> Token.SpecialChar.BlockStart()
            "}" -> Token.SpecialChar.BlockEnd()
            "(" -> Token.SpecialChar.ParenthesesStart()
            ")" -> Token.SpecialChar.ParenthesesEnd()
            in endlines -> {
                lineNumber++
                lineIndex = 0
                return Token.SpecialChar.EndOfLine()
            }
            "," -> Token.SpecialChar.ListSeparator()
            "\\" -> Token.SpecialChar.LineContinue()
            "->" -> Token.SpecialChar.Arrow()
            else -> null
        }
    }
}
