package generationTests.analytics

import generation.GraphvizPrinter
import org.junit.jupiter.api.Test
import parser.AbstractSyntaxTree
import parser.TreeNode
import kotlin.test.assertEquals


class GraphvizPrinterTests {
    /**
     * As we don't really care how newlines or comments are structured - these are a nice to have - not a requirement
     */
    private fun compactOutput(input: String): String = input.
            replace(Regex("#.*\n"),"").replace("\n","")

    @Test
    fun canPrintAllLiteralAssignments() {
        val output = GraphvizPrinter().generateOutput(AbstractSyntaxTree(listOf<TreeNode.Command>(
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

        ).toMutableList()))
        assertEquals(
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"=\"];2;2 [label=\"Identifier(name=a)\"];1 -- 2;3;3 [label=\"Number(value=5.0)\"];1 -- 3;0 -- 1;4;4 [label=\"=\"];5;5 [label=\"Identifier(name=b)\"];4 -- 5;6;6 [label=\"Text(value=hej med dig)\"];4 -- 6;0 -- 4;7;7 [label=\"=\"];8;8 [label=\"Identifier(name=c)\"];7 -- 8;9;9 [label=\"Bool(value=true)\"];7 -- 9;0 -- 7;10;10 [label=\"=\"];11;11 [label=\"Identifier(name=d)\"];10 -- 11;12;12 [label=\"Literal: List\"];13;13 [label=\"Number(value=1.0)\"];12 -- 13;14;14 [label=\"Number(value=2.0)\"];12 -- 14;10 -- 12;0 -- 10;15;15 [label=\"=\"];16;16 [label=\"Identifier(name=e)\"];15 -- 16;17;17 [label=\"Literal: Tuple\"];18;18 [label=\"Number(value=1.0)\"];17 -- 18;19;19 [label=\"Number(value=2.0)\"];17 -- 19;15 -- 17;0 -- 15;}",
                compactOutput(output)
        )
    }

    @Test
    fun canPrintNestedFunctionCall() {
        val output = GraphvizPrinter().generateOutput(AbstractSyntaxTree(listOf<TreeNode.Command>(
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
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"call\"];2;2 [label=\"Identifier(name=" +
                        "print)\"];1 -- 2;3;3 [label=\"call\"];4;4 [label=\"Identifier(name=+)\"];3 -- 4;5;5 [label=" +
                        "\"call\"];6;6 [label=\"Identifier(name=getMyAge)\"];5 -- 6;3 -- 5;7;7 [label=\"Number(value" +
                        "=1.0)\"];3 -- 7;1 -- 3;0 -- 1;}",
                compactOutput(output)
        )
    }

    @Test
    fun canPrintLambdaExpression() {
        val output = GraphvizPrinter().generateOutput(AbstractSyntaxTree(listOf<TreeNode.Command>(
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
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"=\"];2;2 [label=\"Identifier(name=" +
                        "plus)\"];1 -- 2;3;3 [label=\"Lambda\"];4;4 [label=\"TYPE:num\"];4;4 [label=\"TYPE:num\"];" +
                        "3 -- 4;5;5 [label=\"parameter\"];6;6 [label=\"TYPE:num\"];6;6 [label=\"TYPE:num\"];5 -- 6" +
                        ";7;7 [label=\"Identifier(name=a)\"];5 -- 7;3 -- 5;8;8 [label=\"parameter\"];9;9 [label=\"" +
                        "TYPE:num\"];9;9 [label=\"TYPE:num\"];8 -- 9;10;10 [label=\"Identifier(name=b)\"];8 -- 10;3" +
                        " -- 8;11;11 [label=\"return\"];12;12 [label=\"call\"];13;13 [label=\"Identifier(name=+)\"]" +
                        ";12 -- 13;14;14 [label=\"Identifier(name=a)\"];12 -- 14;15;15 [label=\"Identifier(name=b)\"" +
                        "];12 -- 15;11 -- 12;3 -- 11;1 -- 3;0 -- 1;}",
                compactOutput(output)
        )
    }

    @Test
    fun canPrintDeclarationOfAllTypes() {
        val output = GraphvizPrinter().generateOutput(AbstractSyntaxTree(listOf<TreeNode.Command>(
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
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"=\"];2;2 [label=\"Identifier(name=" +
                        "test)\"];1 -- 2;3;3 [label=\"TYPE:Tuple\"];4;4 [label=\"TYPE:func\"];5;5 [label=\"TYPE:" +
                        "num\"];5;5 [label=\"TYPE:num\"];4 -- 5;6;6 [label=\"TYPE:text\"];6;6 [label=\"TYPE:text\"" +
                        "];4 -- 6;3 -- 4;7;7 [label=\"TYPE:List\"];8;8 [label=\"TYPE:bool\"];8;8 [label=\"TYPE:bool" +
                        "\"];7 -- 8;3 -- 7;1 -- 3;0 -- 1;}",
                compactOutput(output)
        )
    }

    @Test
    fun canPrintDeclarationWithAssignment() {
        val output = GraphvizPrinter().generateOutput(AbstractSyntaxTree(listOf<TreeNode.Command>(
                TreeNode.Command.Declaration(
                        TreeNode.Type.Number,
                        TreeNode.Command.Expression.Value.Identifier("x"),
                        TreeNode.Command.Expression.Value.Literal.Number(5.0)

                )
        ).toMutableList()))
        assertEquals(
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"=\"];2;2 [label=\"Identifier(name=x" +
                        ")\"];1 -- 2;3;3 [label=\"TYPE:num\"];3;3 [label=\"TYPE:num\"];1 -- 3;4;4 [label=\"Number(va" +
                        "lue=5.0)\"];1 -- 4;0 -- 1;}",
                compactOutput(output)
        )
    }
}
