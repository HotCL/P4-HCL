package generation

import parser.AbstractSyntaxTree

/**
 * The interface for generating text/code from an Abstract Syntax Trees.
 */
interface IFilesPrinter {
    /**
     * Generates the output to a given
     */
    fun generate(ast: AbstractSyntaxTree): List<FilePair>
}
