package generationTests.analytics

import generation.GraphvizGenerator
import hclTestFramework.parser.*
import org.junit.jupiter.api.Test
import parser.AbstractSyntaxTree
import parser.AstNode
import kotlin.test.assertEquals


class GraphvizGeneratorTests {
    /**
     * As we don't really care how newlines or comments are structured - these are a nice to have - not a requirement
     */
    private fun compactOutput(input: String): String = input.
            replace(Regex("#.*\n"),"").replace("\n","")

    @Test
    fun canPrintAllLiteralAssignments() {
        val output = GraphvizGenerator().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                "a" assignedTo num(5.0),
                "b" assignedTo txt("hej med dig"),
                "c" assignedTo bool(true),
                "d" assignedTo list(num(1),num(2)),
                "e" assignedTo tpl(num(1),num(2))
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
        val output = GraphvizGenerator().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                "print".calledWith(txt,"+".calledWith(num,listOf("getMyAge".called(num),num(1))))
        ).toMutableList()))

        assertEquals(
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"call: print\"];2;2 [label=\"call: " +
                        "+\"];3;3 [label=\"call: getMyAge\"];2 -- 3;4;4 [label=\"Number(value=1.0)\"];2 -- 4;1 -- " +
                        "2;0 -- 1;}",
                compactOutput(output)
        )
    }

    @Test
    fun canPrintLambdaExpression() {
        val output = GraphvizGenerator().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                "plus" assignedTo(lambda() returning num withArguments listOf("a" asType num, "b" asType num)
                        withBody ret("+".calledWith(num,listOf(
                        "a" asIdentifier num,
                        "b" asIdentifier num
                ))))
        ).toMutableList()))

        assertEquals(
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"plus=\"];2;2 [label=\"Lambda\"];3;3" +
                        " [label=\"TYPE:num\"];3;3 [label=\"TYPE:num\"];2 -- 3;4;4 [label=\"parameter\"];5;5 " +
                        "[label=\"TYPE:num\"];5;5 [label=\"TYPE:num\"];4 -- 5;6;6 [label=\"Identifier(name=a)\"];4" +
                        " -- 6;2 -- 4;7;7 [label=\"parameter\"];8;8 [label=\"TYPE:num\"];8;8 [label=\"TYPE:num\"];7" +
                        " -- 8;9;9 [label=\"Identifier(name=b)\"];7 -- 9;2 -- 7;10;10 [label=\"body\"];11;11 " +
                        "[label=\"return\"];12;12 [label=\"call: +\"];13;13 [label=\"Identifier(name=a)\"];12 --" +
                        " 13;14;14 [label=\"Identifier(name=b)\"];12 -- 14;11 -- 12;10 -- 11;2 -- 10;1 -- 2;0 -- 1;}",
                compactOutput(output)
        )
    }

    @Test
    fun canPrintDeclarationOfAllTypes() {
        val output = GraphvizGenerator().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                "test" declaredAs tpl(func(num,listOf(num,txt)),list(bool))
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
        val output = GraphvizGenerator().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                "x" declaredAs num withValue num(5)
        ).toMutableList()))
        assertEquals(
                "graph \"test\" {0;0 [label=\"program\"];1;1 [label=\"x=\"];2;2 " +
                        "[label=\"TYPE:num\"];2;2 [label=\"TYPE:num\"];1 -- 2;3;3 " +
                        "[label=\"Number(value=5.0)\"];1 -- 3;0 -- 1;}",
                compactOutput(output)
        )
    }
}
