package lexer

import kotlin.coroutines.experimental.buildSequence

/**
 * The default implementation of the ILexer interface
 */
class Lexer(private val inputContent: String) : ILexer {
    var lineIndex = 0
    var lineNumber = 0
    var charNumber = 0
    var inputBuffer = ""
    // TODO make this less shitty
    override fun getTokenSequence(): Sequence<PositionalToken> = buildSequence {
        // resets relevat values in case of running multiple times.
        lineIndex = 0
        lineNumber = 0
        charNumber = 0
        inputBuffer = inputContent

        //keeps popping tokens until the inputbuffer is empty.
        while(inputBuffer.isNotEmpty()) {
            var currentString = ""
            var token:Token? = null
            // first iteration nothing happens as current_string is empty. this is done to be DRY.
            // sets token when we have a match. otherwise it keeps popping.
            while (token == null) {
                //ignore spaces
                if (inputBuffer.take(1) == " " && currentString == "") {
                    popInputBuffer()
                    continue
                }
                // if the next chars is a specialChar then the current string must be a literal
                token = getSpecialCharOrNull(currentString)

                // if token is null AND currentString is not empty, we check whether the remaningChars begins with
                // a keyword. if it does, then the current read chars must be a token.
                if (token == null && currentString != "" && isNextCharsSpecialChar(inputBuffer)) {

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
                    currentString += inputBuffer.take(1)
                    popInputBuffer()
                }
            }

            // yield/return current positionalToken
            yield(PositionalToken(token,lineNumber,lineIndex-currentString.length))

            // change linenumber and reset lineIndex if token is EndOfLine
            if(token is lexer.Token.SpecialChar.EndOfLine){
                lineNumber++
                lineIndex = 0
            }
        }
    }



    override fun inputLine(lineNumber: Int) = inputContent.split("\n")[lineNumber]

    private fun popInputBuffer(){
        inputBuffer = inputContent.substring(++charNumber)
        lineIndex++
    }

    private fun isNextCharsSpecialChar(remainingChars: String): Boolean {
        return (remainingChars.isEmpty() || (remainingChars.take(1) == " " ||
                (getSpecialCharOrNull(remainingChars.take(1)) != null ||
                        getSpecialCharOrNull(remainingChars.take(2)) != null)))
    }

    private fun getSpecialCharOrNull(current_string: String): Token? {
        val endOfLines = arrayListOf("\n","\r\n",";")
        return when(current_string)
        {
            "=" -> Token.SpecialChar.Equals()
            "[" -> Token.SpecialChar.SquareBracketStart()
            "]" -> Token.SpecialChar.SquareBracketEnd()
            "{" -> Token.SpecialChar.BlockStart()
            "}" -> Token.SpecialChar.BlockEnd()
            "(" -> Token.SpecialChar.ParenthesesStart()
            ")" -> Token.SpecialChar.ParenthesesEnd()
            in endOfLines -> return Token.SpecialChar.EndOfLine()
            "," -> Token.SpecialChar.ListSeparator()
            "\\" -> Token.SpecialChar.LineContinue()
            "->" -> Token.SpecialChar.Arrow()
            else -> null
        }
    }
}
