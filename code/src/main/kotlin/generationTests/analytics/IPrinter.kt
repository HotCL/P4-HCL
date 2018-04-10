package generationTests.analytics

import parser.AbstractSyntaxTree


/**
 * The interface for printing Abstract Syntax Trees.
 */
interface IPrinter {
    /**
     * Generates the output.
     */
    fun generateOutput(ast : AbstractSyntaxTree) : String
}
