package generationTests.analytics

import generation.SourceCodePrinter
import org.junit.jupiter.api.Test
import parser.AbstractSyntaxTree
import parser.AstNode
import kotlin.test.assertEquals


class SourceCodePrinterTests {

    @Test
    fun canPrintAllLiteralAssignments() {
        val output = SourceCodePrinter().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                AstNode.Command.Assignment(
                        AstNode.Command.Expression.Value.Identifier("a"),
                        AstNode.Command.Expression.Value.Literal.Number(5.0)
                ),

                AstNode.Command.Assignment(
                        AstNode.Command.Expression.Value.Identifier("b"),
                        AstNode.Command.Expression.Value.Literal.Text("hej med dig")
                ),

                AstNode.Command.Assignment(
                        AstNode.Command.Expression.Value.Identifier("c"),
                        AstNode.Command.Expression.Value.Literal.Bool(true)
                ),

                AstNode.Command.Assignment(
                        AstNode.Command.Expression.Value.Identifier("d"),
                        AstNode.Command.Expression.Value.Literal.List(listOf<AstNode.Command.Expression>
                        (
                                AstNode.Command.Expression.Value.Literal.Number(1.0),
                                AstNode.Command.Expression.Value.Literal.Number(2.0)
                        ))
                ),

                AstNode.Command.Assignment(
                        AstNode.Command.Expression.Value.Identifier("e"),
                        AstNode.Command.Expression.Value.Literal.Tuple(listOf<AstNode.Command.Expression>
                        (
                                AstNode.Command.Expression.Value.Literal.Number(1.0),
                                AstNode.Command.Expression.Value.Literal.Number(2.0)
                        ))
                )

        ).toMutableList()
        )
        )
        assertEquals(
                "a = 5.0\n"+
                        "b = \"hej med dig\"\n"+
                        "c = True\n"+
                        "d = [1.0, 2.0]\n"+
                        "e = (1.0, 2.0)\n",
                output
        )
    }

    @Test
    fun canPrintNestedFunctionCall() {
        val output = SourceCodePrinter().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                AstNode.Command.Expression.FunctionCall(
                        AstNode.Command.Expression.Value.Identifier("print"),
                        listOf(
                                AstNode.Command.Expression.FunctionCall(
                                        AstNode.Command.Expression.Value.Identifier("+"),
                                        listOf(
                                                AstNode.Command.Expression.FunctionCall(
                                                        AstNode.Command.Expression.Value.Identifier("getMyAge"),
                                                        listOf()
                                                ),
                                                AstNode.Command.Expression.Value.Literal.Number(1.0)
                                        )
                                )
                        )
                )
        ).toMutableList()))

        assertEquals(
                "getMyAge + 1.0 print\n",
                output
        )
    }

    @Test
    fun canPrintLambdaExpression() {
        val output = SourceCodePrinter().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                AstNode.Command.Assignment(
                        AstNode.Command.Expression.Value.Identifier("plus"),
                        AstNode.Command.Expression.LambdaExpression(
                                listOf(
                                        AstNode.ParameterDeclaration(AstNode.Type.Number,
                                                AstNode.Command.Expression.Value.Identifier("a")
                                        ),

                                        AstNode.ParameterDeclaration(AstNode.Type.Number,
                                                AstNode.Command.Expression.Value.Identifier("b")
                                        )
                                ),
                                AstNode.Type.Number,
                                AstNode.Command.Expression.LambdaBody(
                                listOf(AstNode.Command.Return(AstNode.Command.Expression.FunctionCall(
                                        AstNode.Command.Expression.Value.Identifier("+"),
                                        listOf(
                                                AstNode.Command.Expression.Value.Identifier("a"),
                                                AstNode.Command.Expression.Value.Identifier("b")
                                        )
                                ))))
                        )

                )
        ).toMutableList()))

        assertEquals(
                "plus = (num a, num b): num {\nreturn a + b\n}\n",
                output
        )
    }

    @Test
    fun canPrintDeclarationOfAllTypes() {
        val output = SourceCodePrinter().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                AstNode.Command.Declaration(
                        AstNode.Type.Tuple(listOf(
                                AstNode.Type.Func.ExplicitFunc(listOf(
                                        AstNode.Type.Number,
                                        AstNode.Type.Text
                                ),AstNode.Type.None),
                                AstNode.Type.List(AstNode.Type.Bool)
                        )),
                        AstNode.Command.Expression.Value.Identifier("test"),null

                )
        ).toMutableList()))

        assertEquals(
                "tuple[func[num, text, none], list[bool]] test\n",
                output
        )
    }

    @Test
    fun canPrintDeclarationWithAssignment() {
        val output = SourceCodePrinter().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                AstNode.Command.Declaration(
                        AstNode.Type.Number,
                        AstNode.Command.Expression.Value.Identifier("x"),
                        AstNode.Command.Expression.Value.Literal.Number(5.0)

                )
        ).toMutableList()))

        assertEquals(
                "num x = 5.0\n",
                output
        )
    }
}
