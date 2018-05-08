package generation.cpp

import generation.FilePair
import generation.IFilesPrinter
import parser.AbstractSyntaxTree

/**
 * Generates a program with all the files needed to make a fully functioning c++ program.
 */
class ProgramGenerator : IFilesPrinter {
    override fun generate(ast: AbstractSyntaxTree): List<FilePair> = listOf(
        FilePair("builtin.h", CodeGenerator().generate(ast.builtins())),
        FilePair("types.h", TypeGenerator().generate(ast)),
        FilePair("main.cpp", mainHeader + MainGenerator().generate(ast.notBuiltins()))
    )
    private val mainHeader =
"""
#include <functional>

#if !ARDUINO_AVR_UNO
using namespace std;
#endif

#include "ConstList.h"
#include "builtin.h"
#include "types.h"
"""
}
