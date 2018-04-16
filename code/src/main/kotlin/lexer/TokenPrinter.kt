package lexer

fun printTokens(tokens: List<Token>) = tokens.joinToString(" ") { formatToken(it) }

private fun formatToken(token: Token) = when (token) {
    is Token.Identifier -> token.value
    is Token.Literal.Bool -> token.value.toString()
    is Token.Literal.Text -> token.value
    is Token.Literal.Number -> token.value.toString()
    Token.Return -> "return"
    Token.Type.Bool -> "bool"
    Token.Type.Func -> "func"
    Token.Type.List -> "list"
    Token.Type.None -> "none"
    Token.Type.Number -> "num"
    Token.Type.Tuple -> "tuple"
    Token.Type.Var -> "var"
    Token.Type.Text -> "text"
    Token.SpecialChar.ListSeparator -> ","
    Token.SpecialChar.ParenthesesEnd -> ")"
    Token.SpecialChar.ParenthesesStart -> "("
    Token.SpecialChar.SquareBracketEnd -> "]"
    Token.SpecialChar.SquareBracketStart -> "["
    Token.SpecialChar.EndOfLine -> "\n"
    Token.SpecialChar.Equals -> "="
    Token.SpecialChar.BlockEnd -> "}"
    Token.SpecialChar.BlockStart -> "{"
    Token.SpecialChar.Colon -> ":"
}
