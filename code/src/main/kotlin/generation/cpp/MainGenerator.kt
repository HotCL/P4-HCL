package generation.cpp

import generation.IPrinter
import parser.AbstractSyntaxTree
import parser.AstNode

class MainGenerator : IPrinter {
    override fun generate(ast: AbstractSyntaxTree): String {
        val stringBuilder = StringBuilder()
        val declarations = ast.genFromFilter { it.isDecl }
        val setup = ast.genFromFilter { !it.isLoop && !it.isDecl }
        val loop = ast.genFromFilter { it.isLoop }
        stringBuilder.appendln(declarations)
        stringBuilder.appendln(setup.wrapSetup())
        stringBuilder.appendln(loop.wrapLoop())

        stringBuilder.appendln(("setup();\n" +
                (if (loop.isNotBlank()) "while(1) { loop(); }\n"  else "")+
                "return 0;\n").wrapMain()
        )
        return stringBuilder.toString()
    }

    private fun String.addSerialBegin() = "\n#if ARDUINO_AVR_UNO\n" +
            "Serial.begin(9600);\n" +
            "#endif\n"

    private fun String.wrapLoop() = "void loop() {\n${this.splitIndented}\n}"
    private fun String.wrapSetup() = "void setup() { \n${this.splitIndented}\n}"
    private fun String.wrapMain() = "" +
            "#if !ARDUINO_AVR_UNO\n" +
            "int main() {\n${this.splitIndented}\n    return return_code;}\n" +
            "#endif"

    private val String.splitIndented get() = this.split("\n").joinToString("\n") { "    $it" }
    private val AstNode.Command.isLoop get() = this is AstNode.Command.Expression.FunctionCall && identifier.name == "loop"
    private val AstNode.Command.isDecl get() = this is AstNode.Command.Declaration

}
