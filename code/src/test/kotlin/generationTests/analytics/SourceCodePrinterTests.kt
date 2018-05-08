package generationTests.analytics

import generation.SourceCodePrinter
import hclTestFramework.parser.*
import org.junit.jupiter.api.Test
import parser.AbstractSyntaxTree
import parser.AstNode
import kotlin.test.assertEquals


class SourceCodePrinterTests {

    @Test
    fun canPrintAllLiteralAssignments() {
        val output = SourceCodePrinter().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                "a" assignedTo num(5.0),
                "b" assignedTo txt("hej med dig"),
                "c" assignedTo bool(true),
                "d" assignedTo list(num(1), num(2)),
                "e" assignedTo tpl(num(1), num(2))
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
                "print".calledWith(txt,"+".calledWith(num,listOf("getMyAge".called(num),num(1))))
        ).toMutableList()))

        assertEquals(
                "getMyAge + 1.0 print\n",
                output
        )
    }

    @Test
    fun canPrintLambdaExpression() {
        val output = SourceCodePrinter().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                "plus" assignedTo(lambda() returning num withArguments listOf("a" asType num, "b" asType num)
                        withBody ret("+".calledWith(num,listOf(
                        "a" asIdentifier num,
                        "b" asIdentifier num
                ))))
        ).toMutableList()))

        assertEquals(
                "plus = (num a, num b): num {\nreturn a + b\n}\n",
                output
        )
    }

    @Test
    fun canPrintDeclarationOfAllTypes() {
        val output = SourceCodePrinter().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                "test" declaredAs tpl(func(none,listOf(num,txt)),list(bool))
        ).toMutableList()))
        assertEquals(
                "tuple[func[num, text, none], list[bool]] test\n",
                output
        )
    }

    @Test
    fun canPrintDeclarationWithAssignment() {
        val output = SourceCodePrinter().generate(AbstractSyntaxTree(listOf<AstNode.Command>(
                "x" declaredAs num withValue num(5)
        ).toMutableList()))

        assertEquals(
                "num x = 5.0\n",
                output
        )
    }
}
