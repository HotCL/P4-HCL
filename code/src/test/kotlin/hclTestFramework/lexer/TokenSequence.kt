package hclTestFramework.lexer

import lexer.Token

class TokenSequence {
    val tokens = mutableListOf<Token>()
    fun identifier(value: String) = this.also { tokens.add(Token.Identifier(value)) }
    fun text(value: String) = this.also { tokens.add(Token.Literal.Text(value)) }
    fun number(value: Double) = this.also { tokens.add(Token.Literal.Number(value)) }
    fun bool(value: Boolean) = this.also { tokens.add(Token.Literal.Bool(value)) }
    val `{` get () = this.also { tokens.add(lexer.Token.SpecialChar.BlockStart) }
    val `}` get () = this.also { tokens.add(lexer.Token.SpecialChar.BlockEnd) }
    val squareStart get () = this.also { tokens.add(lexer.Token.SpecialChar.SquareBracketStart) }
    val squareEnd get () = this.also { tokens.add(lexer.Token.SpecialChar.SquareBracketEnd) }
    val `(` get () = this.also { tokens.add(lexer.Token.SpecialChar.ParenthesesStart) }
    val `)` get () = this.also { tokens.add(lexer.Token.SpecialChar.ParenthesesEnd) }
    val newLine get () = this.also { tokens.add(lexer.Token.SpecialChar.EndOfLine) }
    val `,` get () = this.also { tokens.add(lexer.Token.SpecialChar.ListSeparator) }
    val `=` get () = this.also { tokens.add(lexer.Token.SpecialChar.Equals) }
    val colon get () = this.also { tokens.add(lexer.Token.SpecialChar.Colon) }
    val `var` get () = this.also { tokens.add(lexer.Token.Type.Var) }
    val  none get () = this.also { tokens.add(lexer.Token.Type.None) }
    val  text get () = this.also { tokens.add(lexer.Token.Type.Text) }
    val  number get () = this.also { tokens.add(lexer.Token.Type.Number) }
    val  bool get () = this.also { tokens.add(lexer.Token.Type.Bool) }
    val  tuple get () = this.also { tokens.add(lexer.Token.Type.Tuple) }
    val  list get () = this.also { tokens.add(lexer.Token.Type.List) }
    val  func get () = this.also { tokens.add(lexer.Token.Type.Func) }
    val `return` get () = this.also { tokens.add(lexer.Token.Return) }
}
fun buildTokenSequence(builder: TokenSequence.() -> Unit) = TokenSequence().apply(builder).tokens