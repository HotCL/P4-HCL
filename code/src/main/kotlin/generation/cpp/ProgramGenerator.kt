package generation.cpp

import generation.FilePair
import generation.IFilesPrinter
import parser.AbstractSyntaxTree
import parser.AstNode

/**
 * Generates a program with all the files needed to make a fully functioning c++ program.
 */
class ProgramGenerator : IFilesPrinter {
    override fun generate(ast: AbstractSyntaxTree): List<FilePair> = listOf(
        HelperHeaders.constList,
        HelperHeaders.ftoa,
        FilePair("builtin.h", CodeGenerator().generate(ast.builtins())),
        FilePair("types.h", TypeGenerator().generate(ast)),
        FilePair("main.cpp", mainHeader + MainGenerator().generate(ast.addReturnCode().notBuiltins()))
    )

    private fun AbstractSyntaxTree.addReturnCode() = apply { children.add(0, returnCode) }
    private val returnCode = AstNode.Command.Declaration(
            AstNode.Type.Number,
            AstNode.Command.Expression.Value.Identifier("RETURN_CODE", AstNode.Type.Number),
            AstNode.Command.Expression.Value.Literal.Number(0.0)
    )
    private val mainHeader =
"""
#include <functional>

#include "ConstList.h"
#include "ftoa.h"

using namespace std;

#include "builtin.h"
#include "types.h"
"""
}
