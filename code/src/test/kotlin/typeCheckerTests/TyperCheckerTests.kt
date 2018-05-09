package typeCheckerTests

import com.natpryce.hamkrest.assertion.assertThat
import lexer.Token
import parser.AstNode
import parserTests.matchesAstChildren

class TyperCheckerTests {
    @org.junit.jupiter.api.Test
    fun parseTupleTypeWithIdentifiers() {
        assertThat(
                listOf(
                        Token.Type.Number,
                        Token.Identifier("myNumber"),
                        Token.SpecialChar.Equals,
                        Token.Literal.Number(5.0),
                        Token.SpecialChar.EndOfLine,
                        Token.Type.Text,
                        Token.Identifier("myText"),
                        Token.SpecialChar.Equals,
                        Token.Literal.Text("someText"),
                        Token.SpecialChar.EndOfLine,
                        Token.Type.Tuple,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.ListSeparator,
                        Token.Type.Text,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myTuple"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.Identifier("myNumber"),
                        Token.SpecialChar.ListSeparator,
                        Token.Identifier("myText"),
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.EndOfLine
                ),
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Number,
                                AstNode.Command.Expression.Value.Identifier("myNumber",AstNode.Type.Number),
                                AstNode.Command.Expression.Value.Literal.Number(5.0)
                        ),
                        AstNode.Command.Declaration(
                                AstNode.Type.Text,
                                AstNode.Command.Expression.Value.Identifier("myText",
                                        AstNode.Type.Text),
                                AstNode.Command.Expression.Value.Literal.Text("someText")
                        ),
                        AstNode.Command.Declaration(
                                AstNode.Type.Tuple(listOf(
                                        AstNode.Type.Number,
                                        AstNode.Type.Text
                                )
                                ),
                                AstNode.Command.Expression.Value.Identifier("myTuple",
                                        AstNode.Type.Tuple(listOf(
                                                AstNode.Type.Number,
                                                AstNode.Type.Text
                                        ))),
                                AstNode.Command.Expression.Value.Literal.Tuple(
                                        listOf(
                                                AstNode.Command.Expression.Value.Identifier("myNumber",
                                                        AstNode.Type.Number),
                                                AstNode.Command.Expression.Value.Identifier("myText",
                                                        AstNode.Type.Text)
                                        )
                                )
                        )
                )
        )
    }

}
