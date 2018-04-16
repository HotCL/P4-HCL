package generationTests.analytics

import generation.SourceCodePrinter
import org.junit.jupiter.api.Test
import parser.AbstractSyntaxTree
import parser.TreeNode
import kotlin.test.assertEquals


class SourceCodePrinterTests {

    @Test
    fun canPrintAllLiteralAssignments() {
        val output = SourceCodePrinter().generateOutput(AbstractSyntaxTree(listOf<TreeNode.Command>(
                TreeNode.Command.Assignment(
                        TreeNode.Command.Expression.Value.Identifier("a"),
                        TreeNode.Command.Expression.Value.Literal.Number(5.0)
                ),

                TreeNode.Command.Assignment(
                        TreeNode.Command.Expression.Value.Identifier("b"),
                        TreeNode.Command.Expression.Value.Literal.Text("hej med dig")
                ),

                TreeNode.Command.Assignment(
                        TreeNode.Command.Expression.Value.Identifier("c"),
                        TreeNode.Command.Expression.Value.Literal.Bool(true)
                ),

                TreeNode.Command.Assignment(
                        TreeNode.Command.Expression.Value.Identifier("d"),
                        TreeNode.Command.Expression.Value.Literal.List(listOf<TreeNode.Command.Expression>
                        (
                                TreeNode.Command.Expression.Value.Literal.Number(1.0),
                                TreeNode.Command.Expression.Value.Literal.Number(2.0)
                        ))
                ),

                TreeNode.Command.Assignment(
                        TreeNode.Command.Expression.Value.Identifier("e"),
                        TreeNode.Command.Expression.Value.Literal.Tuple(listOf<TreeNode.Command.Expression>
                        (
                                TreeNode.Command.Expression.Value.Literal.Number(1.0),
                                TreeNode.Command.Expression.Value.Literal.Number(2.0)
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
        val output = SourceCodePrinter().generateOutput(AbstractSyntaxTree(listOf<TreeNode.Command>(
                TreeNode.Command.Expression.FunctionCall(
                        TreeNode.Command.Expression.Value.Identifier("print"),
                        listOf(
                                TreeNode.Command.Expression.FunctionCall(
                                        TreeNode.Command.Expression.Value.Identifier("+"),
                                        listOf(
                                                TreeNode.Command.Expression.FunctionCall(
                                                        TreeNode.Command.Expression.Value.Identifier("getMyAge"),
                                                        listOf()
                                                ),
                                                TreeNode.Command.Expression.Value.Literal.Number(1.0)
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
        val output = SourceCodePrinter().generateOutput(AbstractSyntaxTree(listOf<TreeNode.Command>(
                TreeNode.Command.Assignment(
                        TreeNode.Command.Expression.Value.Identifier("plus"),
                        TreeNode.Command.Expression.LambdaExpression(
                                listOf(
                                        TreeNode.ParameterDeclaration(TreeNode.Type.Number,
                                                TreeNode.Command.Expression.Value.Identifier("a")
                                        ),

                                        TreeNode.ParameterDeclaration(TreeNode.Type.Number,
                                                TreeNode.Command.Expression.Value.Identifier("b")
                                        )
                                ),
                                TreeNode.Type.Number,
                                listOf(TreeNode.Command.Return(TreeNode.Command.Expression.FunctionCall(
                                        TreeNode.Command.Expression.Value.Identifier("+"),
                                        listOf(
                                                TreeNode.Command.Expression.Value.Identifier("a"),
                                                TreeNode.Command.Expression.Value.Identifier("b")
                                        )
                                )))
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
        val output = SourceCodePrinter().generateOutput(AbstractSyntaxTree(listOf<TreeNode.Command>(
                TreeNode.Command.Declaration(
                        TreeNode.Type.Tuple(listOf(
                                TreeNode.Type.Func.ExplicitFunc(listOf(
                                        TreeNode.Type.Number,
                                        TreeNode.Type.Text
                                ),TreeNode.Type.None),
                                TreeNode.Type.List(TreeNode.Type.Bool)
                        )),
                        TreeNode.Command.Expression.Value.Identifier("test"),null

                )
        ).toMutableList()))

        assertEquals(
                "tuple[func[num, text, none], list[bool]] test\n",
                output
        )
    }

    @Test
    fun canPrintDeclarationWithAssignment() {
        val output = SourceCodePrinter().generateOutput(AbstractSyntaxTree(listOf<TreeNode.Command>(
                TreeNode.Command.Declaration(
                        TreeNode.Type.Number,
                        TreeNode.Command.Expression.Value.Identifier("x"),
                        TreeNode.Command.Expression.Value.Literal.Number(5.0)

                )
        ).toMutableList()))

        assertEquals(
                "num x = 5.0\n",
                output
        )
    }
}
