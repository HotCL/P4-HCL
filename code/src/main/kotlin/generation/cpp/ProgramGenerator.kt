package generation.cpp

import generation.FilePair
import generation.IFilesPrinter
import parser.AbstractSyntaxTree
import parser.AstNode
import parser.BuiltinLambdaAttributes
import java.io.File

/**
 * Generates a program with all the files needed to make a fully functioning c++ program.
 */
class ProgramGenerator : IFilesPrinter {
    override fun generate(ast: AbstractSyntaxTree): List<FilePair> =  listOf(
                FilePair("types.h", TypeGenerator().generate(ast)),
                FilePair("main.c", MainGenerator().generate(ast.notBuiltins())),
                FilePair("builtin.h", CodeGenerator().generate(ast.builtins()))
    )
}
