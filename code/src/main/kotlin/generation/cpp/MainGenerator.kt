package generation.cpp

import generation.IPrinter
import parser.AbstractSyntaxTree
import parser.AstNode

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
        val loop = ast.genFromFilter { it.isLoop }
        stringBuilder.appendln(declarations)
        stringBuilder.appendln(setup.wrapSetup())
        stringBuilder.appendln(loop.wrapLoop())

        stringBuilder.appendln(("setup();\n" +
                (if (loop.isNotBlank()) "while(1) { loop(); }\n" else "")).wrapMain()
        )
        return stringBuilder.toString()
    }

    private fun String.prefixSerialBegin() = "\n#if ARDUINO_AVR_UNO\n" +
            "Serial.begin(9600);\n" +
            "#endif\n" + this

    private fun String.wrapLoop() = "void loop() {\n${this.splitIndented}\n}"
    private fun String.wrapSetup() = "void setup() { \n${this.prefixSerialBegin().splitIndented}\n}"
    private fun String.wrapMain() = "" +
            "#ifndef ARDUINO_AVR_UNO\n" +
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

#ifndef ARDUINO_AVR_UNO

#include <iostream>

#endif

#ifdef _WIN32 //If windows based PC
    #include <windows.h>
#else //If unix based PC
    #include <unistd.h>
#endif //_WIN32


using namespace std;

#include "builtin.h"
#include "types.h"
"""
}
