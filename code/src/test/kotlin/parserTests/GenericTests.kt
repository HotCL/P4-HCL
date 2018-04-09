package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.ImplicitTypeNotAllowed
import lexer.Token
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import parser.Parser
import parser.TreeNode
import kotlin.coroutines.experimental.buildSequence

class GenericTests {

    @Test
    fun canParseNumDeclaration() {
        assertThat(
                listOf(
                        Token.Type.Number(),
                        Token.Identifier("myId"),
                        Token.SpecialChar.Equals(),
                        Token.Literal.Number("5"),
                        Token.SpecialChar.EndOfLine()
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Number(),
                                TreeNode.Command.Expression.Value.Identifier("myId"),
                                TreeNode.Command.Expression.Value.Literal.Number(5.0)
                        )
                )
        )
    }

    @Test
    fun canParseBool() {
        assertThat(
                listOf(
                        Token.Type.Bool(),
                        Token.Identifier("myBool"),
                        Token.SpecialChar.Equals(),
                        Token.Literal.Bool(true),
                        Token.SpecialChar.EndOfLine()
                ),
                matchesAstChildren(
                        TreeNode.Command.Declaration(
                                TreeNode.Type.Bool(),
                                TreeNode.Command.Expression.Value.Identifier("myBool"),
                                TreeNode.Command.Expression.Value.Literal.Bool(true)
                        )
                )
        )
    }

    @Test
    fun canParseNumAssignment() {
        assertThat(
                listOf(
                        Token.Identifier("myId"),
                        Token.SpecialChar.Equals(),
                        Token.Literal.Number("5"),
                        Token.SpecialChar.EndOfLine()
                ),
                matchesAstChildren(
                        TreeNode.Command.Assignment(
                                TreeNode.Command.Expression.Value.Identifier("myId"),
                                TreeNode.Command.Expression.Value.Literal.Number(5.0)
                        )
                )
        )
    }

    @org.junit.jupiter.api.Test
    fun failsOnImplicitFuncAsParameter() {
        val lexer = DummyLexer(listOf(
                Token.Type.List(),
                Token.SpecialChar.SquareBracketStart(),
                Token.Type.Func(),
                Token.SpecialChar.SquareBracketEnd(),
                Token.Identifier("myFunc"),
                Token.SpecialChar.EndOfLine()
        ))
        Assertions.assertThrows(ImplicitTypeNotAllowed::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }


    @Test
    fun failsOnLineStartsWithEquals() {
        val lexer = DummyLexer(buildSequence {
            yield(Token.SpecialChar.Equals())
            yield(Token.SpecialChar.EndOfLine())
        })

        assertThrows(Exception::class.java) { Parser(lexer).generateAbstractSyntaxTree() }
    }



}
