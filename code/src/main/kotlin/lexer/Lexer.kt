package lexer

import com.natpryce.hamkrest.matches
import com.natpryce.hamkrest.startsWith
import java.util.regex.Pattern.matches
import kotlin.coroutines.experimental.buildSequence

/**
 * The default implementation of the ILexer interface
 */
class Lexer(private val inputContent: String) : ILexer {
    var lineIndex = 0
    var lineNumber = 0
    var charNumber = 0
    var remainingChars = inputContent
    // TODO make this less shitty
    override fun getTokenSequence(): Sequence<PositionalToken> = buildSequence {

        // sets token when we have a match. otherwise it keeps popping.
        // first iteration nothing happens as current_string is empty. this is done to be DRY.
        while(remainingChars.isNotEmpty()) {
            var currentString = ""
            var token:Token? = null
            while (token == null) {
                if (remainingChars.take(1) == " " && currentString == "") {
                    popRemaingChars()
                    continue
                }
                // if the next chars is a specialChar then the current string must be a literal
                token = getSpecialCharOrNull(currentString)
                // if token is null AND the next char is either a space or the one or two next chars are a special char
                // then what is currently in the buffer is a separate token.
                // for instance with currentString == "bool" and 3next 2 chars being "->" then we need bool to be a token and then "yield" here.
                // if however the next char is not a special char (for instance just an "f" then we need to continue popping.
                if (token == null && currentString != "" && isNextCharsSpecialChar(remainingChars)) {

                    token = with(currentString){
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
                        // number literal
                            matches("-?\\d+(\\.\\d+)?".toRegex()) -> Token.Literal.Number(currentString)
                        // string/txt literal
                            startsWith("'") && endsWith("'") -> Token.Literal.Number(currentString)
                            startsWith("\"") && endsWith("\"") -> Token.Literal.Number(currentString)
                        //bool
                            equals("true") -> Token.Literal.Bool(true)
                            equals("false") -> Token.Literal.Bool(false)
                        // identifer
                            else -> Token.Identifier(currentString)
                        }
                    }
                }

                if (token == null) {
                    currentString += remainingChars.take(1)
                    popRemaingChars()
                }
            }
            yield(PositionalToken(token,lineNumber,lineIndex-currentString.length))
            if(token is lexer.Token.SpecialChar.EndOfLine){
                lineNumber++
                lineIndex = 0
            }
        }
    }



    override fun inputLine(lineNumber: Int) = inputContent.split("\n")[lineNumber]

    private fun popRemaingChars(){
        remainingChars = inputContent.substring(++charNumber)
        lineIndex++
    }

    private fun isNextCharsSpecialChar(remainingChars: String): Boolean {
        return (remainingChars.isEmpty() || (remainingChars.take(1) == " " ||
                (getSpecialCharOrNull(remainingChars.take(1)) != null ||
                        getSpecialCharOrNull(remainingChars.take(2)) != null)))
    }

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
            in endlines -> return Token.SpecialChar.EndOfLine()
            "," -> Token.SpecialChar.ListSeparator()
            "\\" -> Token.SpecialChar.LineContinue()
            "->" -> Token.SpecialChar.Arrow()
            else -> null
        }
    }
}
