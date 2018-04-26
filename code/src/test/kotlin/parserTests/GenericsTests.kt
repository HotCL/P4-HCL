package parserTests

import com.natpryce.hamkrest.assertion.assertThat
import exceptions.GenericPassedFunctionException
import exceptions.UndeclaredError
import exceptions.UnexpectedTokenError
import exceptions.UnexpectedTypeError
import lexer.Token
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import parser.AstNode
import parser.Parser
import parser.ParserWithoutBuiltins

class GenericsTests{

    @Test
    fun canDeclareWithGenerics() {
        assertThat(
                listOf(
                        Token.Type.Func,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Identifier("T"),
                        Token.SpecialChar.ListSeparator,
                        Token.Type.Text,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.Identifier("T"),
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.Colon,
                        Token.Type.Text,
                        Token.SpecialChar.BlockStart,
                        Token.Literal.Text("yeah"),
                        Token.SpecialChar.BlockEnd,
                        Token.SpecialChar.EndOfLine
                ),
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.GenericType("T")),
                                        AstNode.Type.Text),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.GenericType("T"),
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ),
                                        AstNode.Type.Text,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Literal.Text("yeah")
                                                )
                                        ))
                                )
                        )
                )
        )
    }

    @Test
    fun canCallFunctionGenerics() {
        assertThat(
                listOf(
                        Token.Type.Func,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Identifier("T"),
                        Token.SpecialChar.ListSeparator,
                        Token.Type.Text,
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.Identifier("T"),
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.Colon,
                        Token.Type.Text,
                        Token.SpecialChar.BlockStart,
                        Token.Literal.Text("generics!"),
                        Token.SpecialChar.BlockEnd,
                        Token.SpecialChar.EndOfLine,

                        Token.Literal.Number(5.0),
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.EndOfLine

                ),
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.GenericType("T")),
                                        AstNode.Type.Text),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.GenericType("T"),
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ),
                                        AstNode.Type.Text,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Literal.Text("generics!")
                                                )
                                        ))
                                )
                        ),
                        AstNode.Command.Expression.FunctionCall(
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                listOf(AstNode.Command.Expression.Value.Literal.Number(5.0)
                                )
                        )
                )
        )
    }

    @Test
    fun canCallFunctionGenericsNested() {
        assertThat(
                listOf(
                        Token.Type.Func,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.List,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Identifier("T"),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.SpecialChar.ListSeparator,
                        Token.Type.Tuple,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.ListSeparator,
                        Token.Identifier("T3"),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.SpecialChar.ListSeparator,
                        Token.Identifier("T"),
                        Token.SpecialChar.ListSeparator,
                        Token.Identifier("T"),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.Type.List,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Identifier("T"),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myParam"),
                        Token.SpecialChar.ListSeparator,
                        Token.Type.Tuple,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Type.Number,
                        Token.SpecialChar.ListSeparator,
                        Token.Identifier("T3"),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.ListSeparator,
                        Token.Identifier("T"),
                        Token.Identifier("myT"),
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.Colon,
                        Token.Identifier("T"),
                        Token.SpecialChar.BlockStart,
                        Token.Identifier("myT"),
                        Token.SpecialChar.BlockEnd,
                        Token.SpecialChar.EndOfLine,

                        Token.Type.Number,
                        Token.Identifier("x"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Literal.Number(1.0),
                        Token.SpecialChar.ListSeparator,
                        Token.Literal.Number(2.0),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.ParenthesesStart,
                        Token.Literal.Number(1.0),
                        Token.SpecialChar.ListSeparator,
                        Token.Literal.Text("test"),
                        Token.SpecialChar.ParenthesesEnd,
                        Token.Literal.Number(9.0),


                        Token.SpecialChar.EndOfLine

                ),
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(
                                                AstNode.Type.List(AstNode.Type.GenericType("T")),
                                                AstNode.Type.Tuple(listOf(
                                                        AstNode.Type.Number,
                                                        AstNode.Type.GenericType("T3"))),
                                                AstNode.Type.GenericType("T")
                                        ),
                                        AstNode.Type.GenericType("T")),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.List(AstNode.Type.GenericType("T")),
                                                        AstNode.Command.Expression.Value.Identifier("myParam")
                                                ),
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.Tuple(listOf(
                                                                AstNode.Type.Number,
                                                                AstNode.Type.GenericType("T3"))),
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                ),
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.GenericType("T"),
                                                        AstNode.Command.Expression.Value.Identifier("myT")
                                                )
                                        ),
                                        AstNode.Type.GenericType("T"),
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                    AstNode.Command.Expression.Value.Identifier("myT")
                                                )
                                        ))
                                )
                        ),
                        AstNode.Command.Declaration(
                                AstNode.Type.Number,
                                AstNode.Command.Expression.Value.Identifier("x"),
                                AstNode.Command.Expression.FunctionCall(
                                        AstNode.Command.Expression.Value.Identifier("myFunc"),
                                        listOf(
                                                AstNode.Command.Expression.Value.Literal.List(listOf(
                                                        AstNode.Command.Expression.Value.Literal.Number(1.0),
                                                        AstNode.Command.Expression.Value.Literal.Number(2.0)

                                                )),
                                                AstNode.Command.Expression.Value.Literal.Tuple(listOf(
                                                        AstNode.Command.Expression.Value.Literal.Number(1.0),
                                                        AstNode.Command.Expression.Value.Literal.Text("test")

                                                )),
                                                AstNode.Command.Expression.Value.Literal.Number(9.0)
                                        )
                                )
                        )
                )
        )
    }

    @Test
    fun identifyFunctionTest(){
        assertThat(
                listOf(
                        Token.Type.Func,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Identifier("T"),
                        Token.SpecialChar.ListSeparator,
                        Token.Identifier("T"),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.Identifier("T"),
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.Colon,
                        Token.Identifier("T"),
                        Token.SpecialChar.BlockStart,
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.BlockEnd,
                        Token.SpecialChar.EndOfLine

                ),
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.GenericType("T")),
                                        AstNode.Type.GenericType("T")),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.GenericType("T"),
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ),
                                        AstNode.Type.GenericType("T"),
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ))
                                )
                        )
                )
        )
    }

    @Test
    fun failOnCallExpressionWrongType() {
        val lexer = DummyLexer(listOf(
                Token.Type.Func,
                Token.SpecialChar.SquareBracketStart,
                Token.Identifier("T"),
                Token.SpecialChar.ListSeparator,
                Token.Identifier("T"),
                Token.SpecialChar.SquareBracketEnd,
                Token.Identifier("myFunc"),
                Token.SpecialChar.Equals,
                Token.SpecialChar.ParenthesesStart,
                Token.Identifier("T"),
                Token.Identifier("myParam"),
                Token.SpecialChar.ParenthesesEnd,
                Token.SpecialChar.Colon,
                Token.Identifier("T"),
                Token.SpecialChar.BlockStart,
                Token.Identifier("myParam"),
                Token.SpecialChar.BlockEnd,
                Token.SpecialChar.EndOfLine,

                Token.Type.Bool,
                Token.Identifier("x"),
                Token.SpecialChar.Equals,
                Token.Literal.Number(1.0),
                Token.Identifier("myFunc"),
                Token.SpecialChar.EndOfLine

        ))


        Assertions.assertThrows(UnexpectedTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }

    }


    @Test
    fun failOnPassedFunctionWithGenerics() {
        val lexer = DummyLexer(listOf(
                Token.Type.Func,
                Token.SpecialChar.SquareBracketStart,
                Token.Type.Func,
                Token.SpecialChar.SquareBracketStart,
                Token.Type.Number,
                Token.SpecialChar.SquareBracketEnd,
                Token.SpecialChar.ListSeparator,
                Token.Type.Number,
                Token.SpecialChar.SquareBracketEnd,
                Token.Identifier("myFunc"),
                Token.SpecialChar.Equals,
                Token.SpecialChar.ParenthesesStart,
                Token.Type.Func,
                Token.SpecialChar.SquareBracketStart,
                Token.Type.Number,
                Token.SpecialChar.SquareBracketEnd,
                Token.Identifier("myParam"),
                Token.SpecialChar.ParenthesesEnd,
                Token.SpecialChar.Colon,
                Token.Type.Number,
                Token.SpecialChar.BlockStart,
                Token.Literal.Number(2.0),
                Token.Identifier("passFunc"),
                Token.SpecialChar.BlockEnd,
                Token.SpecialChar.EndOfLine,

                Token.Type.Func,
                Token.SpecialChar.SquareBracketStart,
                Token.Identifier("T"),
                Token.SpecialChar.ListSeparator,
                Token.Identifier("T"),
                Token.SpecialChar.SquareBracketEnd,
                Token.Identifier("passFunc"),
                Token.SpecialChar.Equals,
                Token.SpecialChar.ParenthesesStart,
                Token.Identifier("T"),
                Token.Identifier("value"),
                Token.SpecialChar.ParenthesesEnd,
                Token.SpecialChar.Colon,
                Token.Identifier("T"),
                Token.SpecialChar.BlockStart,
                Token.Identifier("value"),
                Token.SpecialChar.BlockEnd,
                Token.SpecialChar.EndOfLine,

                Token.Type.Number,
                Token.Identifier("x"),
                Token.SpecialChar.Equals,
                Token.SpecialChar.Colon,
                Token.Identifier("passFunc"),
                Token.Identifier("myFunc"),
                Token.SpecialChar.EndOfLine
        ))

        Assertions.assertThrows(GenericPassedFunctionException::class.java) {
            ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree()
        }

    }

    @Test
    fun failOnCallDifferentTypesInArgs() {
        val lexer = DummyLexer(listOf(
                Token.Type.Func,
                Token.SpecialChar.SquareBracketStart,
                Token.Identifier("T"),
                Token.SpecialChar.ListSeparator,
                Token.Identifier("T"),
                Token.SpecialChar.ListSeparator,
                Token.Identifier("T"),
                Token.SpecialChar.SquareBracketEnd,
                Token.Identifier("myFunc"),
                Token.SpecialChar.Equals,
                Token.SpecialChar.ParenthesesStart,
                Token.Identifier("T"),
                Token.Identifier("myParam"),
                Token.SpecialChar.ListSeparator,
                Token.Identifier("T"),
                Token.Identifier("myParam2"),
                Token.SpecialChar.ParenthesesEnd,
                Token.SpecialChar.Colon,
                Token.Identifier("T"),
                Token.SpecialChar.BlockStart,
                Token.Identifier("myParam"),
                Token.SpecialChar.BlockEnd,
                Token.SpecialChar.EndOfLine,

                Token.Type.Number,
                Token.Identifier("x"),
                Token.SpecialChar.Equals,
                Token.Literal.Number(1.0),
                Token.Identifier("myFunc"),
                Token.Literal.Bool(true),
                Token.SpecialChar.EndOfLine

        ))
        Assertions.assertThrows(UndeclaredError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }

    }

    @Test
    fun failOnTypesNotMatchingWithExpression() {
        val lexer = DummyLexer(listOf(
                Token.Type.Func,
                Token.SpecialChar.SquareBracketStart,
                Token.Identifier("T"),
                Token.SpecialChar.ListSeparator,
                Token.Type.Number,
                Token.SpecialChar.SquareBracketEnd,
                Token.Identifier("myFunc"),
                Token.SpecialChar.Equals,
                Token.SpecialChar.ParenthesesStart,
                Token.Identifier("T2"),
                Token.Identifier("myParam1"),
                Token.SpecialChar.ParenthesesEnd,
                Token.SpecialChar.Colon,
                Token.Type.Text,
                Token.SpecialChar.BlockStart,
                Token.Literal.Text("haha"),
                Token.SpecialChar.BlockEnd,
                Token.SpecialChar.EndOfLine
        ))

        Assertions.assertThrows(UnexpectedTypeError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }
    }


    @Test
    fun canPassFunction() {
        assertThat(
                listOf(
                        Token.Type.Var,
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.Type.Func,
                        Token.SpecialChar.SquareBracketStart,
                        Token.Identifier("T"),
                        Token.SpecialChar.SquareBracketEnd,
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.Colon,
                        Token.Identifier("T"),
                        Token.SpecialChar.BlockStart,
                        Token.Identifier("myParam1"),
                        Token.SpecialChar.BlockEnd,
                        Token.SpecialChar.EndOfLine,

                        Token.Type.Var,
                        Token.Identifier("passFunc"),
                        Token.SpecialChar.Equals,
                        Token.SpecialChar.ParenthesesStart,
                        Token.SpecialChar.ParenthesesEnd,
                        Token.SpecialChar.Colon,
                        Token.Type.Number,
                        Token.SpecialChar.BlockStart,
                        Token.Literal.Number(5.0),
                        Token.SpecialChar.BlockEnd,
                        Token.SpecialChar.EndOfLine,

                        Token.SpecialChar.Colon,
                        Token.Identifier("passFunc"),
                        Token.Identifier("myFunc"),
                        Token.SpecialChar.EndOfLine

                ),
                matchesAstChildren(
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(AstNode.Type.Func.ExplicitFunc(
                                                listOf(),
                                                AstNode.Type.GenericType("T")
                                        )),
                                        AstNode.Type.GenericType("T")),
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf
                                        (
                                                AstNode.ParameterDeclaration
                                                (
                                                        AstNode.Type.Func.ExplicitFunc(
                                                                listOf(),
                                                                AstNode.Type.GenericType("T")
                                                        ),
                                                        AstNode.Command.Expression.Value.Identifier("myParam1")
                                                )
                                        ),
                                        AstNode.Type.GenericType("T"),
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(AstNode.Command.Expression.FunctionCall(
                                                        AstNode.Command.Expression.Value.Identifier("myParam1"),
                                                        listOf()))
                                        ))
                                )
                        ),
                        AstNode.Command.Declaration(
                                AstNode.Type.Func.ExplicitFunc(
                                        listOf(),
                                        AstNode.Type.Number),
                                AstNode.Command.Expression.Value.Identifier("passFunc"),
                                AstNode.Command.Expression.LambdaExpression
                                (
                                        listOf(),
                                        AstNode.Type.Number,
                                        AstNode.Command.Expression.LambdaBody(listOf(
                                                AstNode.Command.Return(
                                                        AstNode.Command.Expression.Value.Literal.Number(5.0)
                                                )
                                        ))
                                )
                        ),
                        AstNode.Command.Expression.FunctionCall(
                                AstNode.Command.Expression.Value.Identifier("myFunc"),
                                listOf(AstNode.Command.Expression.Value.Identifier("passFunc"))
                        )
                )
        )
    }
    @org.junit.jupiter.api.Test
    fun failGenericInList() {
        val lexer = DummyLexer(listOf(
                Token.Type.List,
                Token.SpecialChar.SquareBracketStart,
                Token.Identifier("T"),
                Token.SpecialChar.SquareBracketEnd,
                Token.Identifier("x"),
                Token.SpecialChar.EndOfLine
        ))

        Assertions.assertThrows(UnexpectedTokenError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }

    }
    @org.junit.jupiter.api.Test
    fun failGenericAsPlainType() {
        val lexer = DummyLexer(listOf(
                Token.Identifier("T"),
                Token.Identifier("x"),
                Token.SpecialChar.EndOfLine
        ))

        Assertions.assertThrows(UndeclaredError::class.java) { ParserWithoutBuiltins(lexer).generateAbstractSyntaxTree() }

    }
}
