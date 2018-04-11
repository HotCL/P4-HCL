package typeCheckerTests

import com.natpryce.hamkrest.assertion.assertThat
import lexer.Token
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import parser.TreeNode
import parser.typechecker.TypeChecker
import parserTests.matchesAstChildren
import sun.reflect.generics.tree.Tree

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
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Number,
                                TreeNode.Command.Expression.Value.Identifier("myNumber"),
                                TreeNode.Command.Expression.Value.Literal.Number(5.0)
                        ),
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Text,
                                TreeNode.Command.Expression.Value.Identifier("myText"),
                                TreeNode.Command.Expression.Value.Literal.Text("someText")
                        ),
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Tuple(listOf(
                                        TreeNode.Type.Number,
                                        TreeNode.Type.Text
                                )
                                ),
                                TreeNode.Command.Expression.Value.Identifier("myTuple"),
                                TreeNode.Command.Expression.Value.Literal.Tuple(
                                        listOf(
                                                TreeNode.Command.Expression.Value.Identifier("myNumber"),
                                                TreeNode.Command.Expression.Value.Identifier("myText")
                                        )
                                )
                        )
                )
        )
    }
}