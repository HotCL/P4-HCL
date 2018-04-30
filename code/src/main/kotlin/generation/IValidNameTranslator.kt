package generation

import parser.AstNode

/**
 * Takes a given node and generates the appropriate value in the output language.
 */
interface IValidNameTranslator{

    /**
     * Get a valid, in context of output language identifier name from the given identifier.
     * The source language accepts anything, but output might not accept emojies for-instance.
     * Also important if scopes are problems
     */
    fun getValidIdentifierName(node: AstNode.Command.Expression.Value.Identifier) : String

    /**
     * Generates the valid type from source language to output language.
     * numbers are oftentimes the same as doubles, while a tuple might be typed to a struct or something else.
     */
    fun getValidTypeName(node: AstNode.Type): String
}