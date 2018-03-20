package lexer

sealed class Token {
    data class Identifier(val value: String) : Token()
    sealed class Literal : Token()  {
        data class Text(val value: String) : Token()
        data class Number(val value: Double) : Token()
        data class Bool(val value: Boolean) : Token()
    }
    sealed class SpecialChar : Token() {
        class BlockStart : Token()
        class BlockEnd : Token()
        class SquareBracketStart : Token()
        class SquareBracketEnd : Token()
        class ParenthesesStart : Token()
        class ParenthesesEnd : Token()
        class EndOfLine : Token()
        class ListSeparator : Token()
        class LineContinue : Token()
        class Equals : Token()
        class Arrow : Token()
    }
}
