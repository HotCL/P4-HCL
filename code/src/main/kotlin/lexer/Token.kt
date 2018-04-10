package lexer

/**
 * Lexical getTokenSequence emitted by the lexer
 */
sealed class Token {
    data class Identifier(val value: String) : Token()

    override fun toString() = this::class.simpleName!!

    sealed class Literal : Token() {
        data class Text(val value: String) : Literal()
        data class Number(val value: Double) : Literal()
        data class Bool(val value: Boolean) : Literal()
    }

    sealed class SpecialChar : Token() {
        object BlockStart : SpecialChar()
        object BlockEnd : SpecialChar()
        object SquareBracketStart : SpecialChar()
        object SquareBracketEnd : SpecialChar()
        object ParenthesesStart : SpecialChar()
        object ParenthesesEnd : SpecialChar()
        object EndOfLine : SpecialChar()
        object ListSeparator : SpecialChar()
        object LineContinue : SpecialChar()
        object Equals : SpecialChar()
        object Colon : SpecialChar()
    }

    sealed class Type : Token() {
        object Var : Type()
        object None : Type()
        object Text : Type()
        object Number : Type()
        object Bool : Type()
        object Tuple : Type()
        object List : Type()
        object Func : Type()
    }

    object Return : Token()
}
