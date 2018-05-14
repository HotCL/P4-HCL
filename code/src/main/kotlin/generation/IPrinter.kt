package generation

import parser.AbstractSyntaxTree

/**
 * The interface for generating text/code from an Abstract Syntax Trees.
 */
interface IPrinter {
    /**
     * Generates the output to a given language
     */
    fun generate(ast: AbstractSyntaxTree): String
}
