package lexer

/**
 * Lexical getTokenSequence emitted by the lexer
 */
sealed class Token {
    data class Identifier(val value: String) : Token() {
        override fun toString() = super.toString() + "[$value]"
    }

    override fun toString() = this.javaClass.name!!

    sealed class Literal : Token() {
        class Text(val value: String) : Literal() {
            override fun toString() = super.toString() + "[$value]"
        }
        class Number(strValue: String, val value: Double = strValue.toDouble()) : Literal() {
            override fun toString() = super.toString() + "[$value]"
        }
        class Bool(val value: Boolean) : Literal() {
            override fun toString() = super.toString() + "[$value]"
        }
    }

    sealed class SpecialChar : Token() {
        class BlockStart : SpecialChar()
        class BlockEnd : SpecialChar()
        class SquareBracketStart : SpecialChar()
        class SquareBracketEnd : SpecialChar()
        class ParenthesesStart : SpecialChar()
        class ParenthesesEnd : SpecialChar()
        class EndOfLine : SpecialChar()
        class ListSeparator : SpecialChar()
        class LineContinue : SpecialChar()
        class Equals : SpecialChar()
        class Colon : SpecialChar()
    }

    sealed class Type : Token() {
        class Var : Type()
        class None : Type()
        class Text : Type()
        class Number : Type()
        class Bool : Type()
        class Tuple : Type()
        class List : Type()
        class Func : Type()
    }

    class Return : Token()
}
