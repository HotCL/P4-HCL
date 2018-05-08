package generation.cpp

import generation.IPrinter
import parser.AbstractSyntaxTree
import parser.AstNode

class MainGenerator : IPrinter {
    override fun generate(ast: AbstractSyntaxTree): String {
        return  ast.genFromFilter { it.isDecl } +
                "\n\n/* Setup function */\n\n" +
                "void setup() {\n" +
                ast.genFromFilter { !it.isLoop && !it.isDecl} +
                "}\n" +
                "\n\n/* Loop function */\n\n" +
                "void loop() {\n" +
                ast.genFromFilter { it.isLoop } +
                "}\n" +
                "#if !ARDUINO_AVR_UNO\n" +
                "int main() {\n" +
                "   setup();\n" +
                "   while(1) { loop(); }\n" +
                "   return 0;\n" +
                "}\n" +
                "#endif\n"
    }

    private val AstNode.Command.isLoop get() = this is AstNode.Command.Expression.FunctionCall && identifier.name == "loop"
    private val AstNode.Command.isDecl get() = this is AstNode.Command.Declaration
}

