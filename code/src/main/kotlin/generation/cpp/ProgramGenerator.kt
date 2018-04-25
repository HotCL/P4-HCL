package generation.cpp

import generation.FilePair
import generation.IFilesPrinter
import parser.AbstractSyntaxTree
import java.io.File

/**
 * Generates a program with all the files needed to make a fully functioning c++ program.
 */
class ProgramGenerator : IFilesPrinter {
    override fun generate(ast: AbstractSyntaxTree): List<FilePair> =  listOf(
                FilePair("types.h",TypeGenerator().generate(ast)),
                FilePair("behaviour.c",CodeGenerator().generate(ast)),
                FilePair("builtin.c",TODO("save builtin to resources so it can be loaded here."))
                )
}