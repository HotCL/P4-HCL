package lexer

/**
 * Lexical getNextToken emitted by the lexer
 */
sealed class Token {
    class Identifier(val value: String) : Token()
    sealed class Literal : Token() {
        class Text(val value: String) : Literal()
        class Number(strValue: String, val value: Double = strValue.toDouble()) : Literal()
        class Bool(strValue: String, val value: Boolean = strValue.toBoolean()) : Literal()
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
        class Arrow : SpecialChar()
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
