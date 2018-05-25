package generation.cpp

import generation.IPrinter
import parser.AbstractSyntaxTree
import parser.AstNode

/**
 * Generates the main function in C++ with all necessary header inclusions and calls to setup() and loop()
 */
class MainGenerator : IPrinter {
    override fun generate(ast: AbstractSyntaxTree): String {
        val stringBuilder = StringBuilder(mainHeader)
        val declarations = ast.genFromFilterWithMap ({ it.isDecl }, {
            if (it is AstNode.Command.Declaration && it.expression != null && it.type !is AstNode.Type.Func)
                AstNode.Command.Declaration(it.type, it.identifier)
            else it
        })
        val setup = ast.genFromFilterWithMap ({ !it.isLoop && !it.isDecl }, {
            if (it is AstNode.Command.Declaration && it.expression != null && it.type !is AstNode.Type.Func)
                AstNode.Command.Assignment(it.identifier, it.expression)
            else it
        })
        val loop = ast.genForLoop()
        stringBuilder.appendln(declarations)
        stringBuilder.appendln(setup.wrapSetup())
        stringBuilder.appendln(loop.wrapLoop())

        stringBuilder.appendln(("setup();\n" +
            (if (loop.isNotBlank()) "while(1) { loop(); }\n" else "")).wrapMain()
        )
        return stringBuilder.toString()
    }

    private fun String.prefixSerialBegin() = "" +
        "\n#if defined(ARDUINO_AVR_UNO) || defined(ESP8266)\n" +
        "Serial.begin(9600); // 9600 is the baud rate - must be the same rate used for monitor\n" +
        "while(!Serial);     // Wait for Serial to initialize\n" +
        "#endif\n" + this

    private fun String.wrapLoop() = "void loop() {\n${this.splitIndented}\n}"
    private fun String.wrapSetup() = "void setup() { \n${this.prefixSerialBegin().splitIndented}\n}"
    private fun String.wrapMain() = "" +
        "#if !defined(ARDUINO_AVR_UNO) || !defined(ESP8266)\n" +
        "int main() {\n${this.splitIndented}\n    return ${"RETURN_CODE".cppName};\n}\n" +
        "#endif"

    private val String.splitIndented get() = this.split("\n").joinToString("\n") { "    $it" }
    private val AstNode.Command.isLoop get() =
        this is AstNode.Command.Expression.FunctionCall && identifier.name == "loop"
    private val AstNode.Command.isDecl get() = this is AstNode.Command.Declaration

    private val mainHeader =
        """
#include <functional>

#include "ConstList.h"
#include "ftoa.h"

#if !defined(ARDUINO_AVR_UNO) && !defined(ESP8266)

#include <iostream>

#ifdef _WIN32 //If windows based PC
    #include <windows.h>
#else //If unix based PC
    #include <unistd.h>
#endif //_WIN32

#endif

using namespace std;

#include "builtin.h"
#include "types.h"
"""
}
