package generationTests.analytics

import generation.GraphvizPrinter
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import parser.AbstractSyntaxTree
import parser.AstNode
import kotlin.test.assertEquals


class GraphvizPrinterTests {
    /**
     * As we don't really care how newlines or comments are structured - these are a nice to have - not a requirement
     */
    private fun compactOutput(input: String): String = input.
            replace(Regex("#.*\n"),"").replace("\n","")

    @Test
    fun canPrintAllLiteralAssignments() {
        val output = GraphvizPrinter().generateOutput(AbstractSyntaxTree(listOf<AstNode.Command>(
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
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"a=\"];2;2 [label=\"Number(value=5." +
                        "0)\"];1 -- 2;0 -- 1;3;3 [label=\"b=\"];4;4 [label=\"Text(value=hej med dig)\"];3 -- 4;0 " +
                        "-- 3;5;5 [label=\"c=\"];6;6 [label=\"Bool(value=true)\"];5 -- 6;0 -- 5;7;7 [label=\"d=\"]" +
                        ";8;8 [label=\"Literal: List\"];9;9 [label=\"Number(value=1.0)\"];8 -- 9;10;10 [label=\"Nu" +
                        "mber(value=2.0)\"];8 -- 10;7 -- 8;0 -- 7;11;11 [label=\"e=\"];12;12 [label=\"Literal: Tup" +
                        "le\"];13;13 [label=\"Number(value=1.0)\"];12 -- 13;14;14 [label=\"Number(value=2.0)\"];12" +
                        " -- 14;11 -- 12;0 -- 11;}",
                compactOutput(output)
        )
    }

    @Test
    fun canPrintNestedFunctionCall() {
        val output = GraphvizPrinter().generateOutput(AbstractSyntaxTree(listOf<AstNode.Command>(
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
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"call: print\"];2;2 [label=\"call: " +
                        "+\"];3;3 [label=\"call: getMyAge\"];2 -- 3;4;4 [label=\"Number(value=1.0)\"];2 -- 4;1 -- " +
                        "2;0 -- 1;}",
                compactOutput(output)
        )
    }

    @Disabled
    @Test
    fun canPrintLambdaExpression() {
        val output = GraphvizPrinter().generateOutput(AbstractSyntaxTree(listOf<AstNode.Command>(
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
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"plus=\"];2;2 [label=\"Lambda\"];3;3 " +
                        "[label=\"TYPE:num\"];3;3 [label=\"TYPE:num\"];2 -- 3;4;4 [label=\"parameter\"];5;5 [label=" +
                        "\"TYPE:num\"];5;5 [label=\"TYPE:num\"];4 -- 5;6;6 [label=\"Identifier(name=a)\"];4 -- 6;2 " +
                        "-- 4;7;7 [label=\"parameter\"];8;8 [label=\"TYPE:num\"];8;8 [label=\"TYPE:num\"];7 -- 8;9;9" +
                        " [label=\"Identifier(name=b)\"];7 -- 9;2 -- 7;10;10 [label=\"return\"];11;11 [label=\"call:" +
                        " +\"];12;12 [label=\"Identifier(name=a)\"];11 -- 12;13;13 [label=\"Identifier(name=b)\"];11" +
                        " -- 13;10 -- 11;2 -- 10;1 -- 2;0 -- 1;}",
                compactOutput(output)
        )
    }

    @Test
    fun canPrintDeclarationOfAllTypes() {
        val output = GraphvizPrinter().generateOutput(AbstractSyntaxTree(listOf<AstNode.Command>(
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
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"test=\"];2;2 [label=\"TYPE:Tuple\"];" +
                        "3;3 [label=\"TYPE:func\"];4;4 [label=\"TYPE:num\"];4;4 [label=\"TYPE:num\"];3 -- 4;5;5" +
                        " [label=\"TYPE:text\"];5;5 [label=\"TYPE:text\"];3 -- 5;2 -- 3;6;6 [label=\"TYPE:List\"]" +
                        ";7;7 [label=\"TYPE:bool\"];7;7 [label=\"TYPE:bool\"];6 -- 7;2 -- 6;1 -- 2;0 -- 1;}",
                compactOutput(output)
        )
    }

    @Test
    fun canPrintDeclarationWithAssignment() {
        val output = GraphvizPrinter().generateOutput(AbstractSyntaxTree(listOf<AstNode.Command>(
                AstNode.Command.Declaration(
                        AstNode.Type.Number,
                        AstNode.Command.Expression.Value.Identifier("x"),
                        AstNode.Command.Expression.Value.Literal.Number(5.0)

                )
        ).toMutableList()))
        assertEquals(
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"x=\"];2;2 " +
                        "[label=\"TYPE:num\"];2;2 [label=\"TYPE:num\"];1 -- 2;3;3 " +
                        "[label=\"Number(value=5.0)\"];1 -- 3;0 -- 1;}",
                compactOutput(output)
        )
    }
}
