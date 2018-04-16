package parser.symboltable

import parser.TreeNode

/**
 * The interface which all Symbol Table implementations must implement.
 */
interface ISymbolTable{
    /**
     * Opens a new scope in the symbol table.
     * Pushes a new subtable unto the scope stack.
     */
    fun openScope()

    /**
     * Closes the most recently opened scope in the symbol table.
     * Pops the subtable from the stack.
     * Symbol references subsequently revert to outer scopes.
     */
    fun closeScope()

    /**
     * Enters name in the symbol table's current scope.
     * @param name The name of the identifier.
     * @param type The Type attributes of the identifier.
     * @return Whether the symbol could be entered in the symbol table
     * false if the symbol is already declared in current scope, and is not a function declaration
     */
    fun enterSymbol(name: String, type: TreeNode.Type): Boolean

    /**
     * Returns a list of the symbol table's currently valid declarations for name.
     * If no declaration for name is currently in effect, then an empty list is returned.
     * @param name The name of the identifier.
     */
    fun retrieveSymbol(name: String): Symbol

    /**
     * Tests whether name is present in the symbol table's current scope.
     * True is returned if it is.
     * Otherwise False is returned.
     * @param name The name of the identifier.
     * @return Whether the symbol is declared locally
     */
    fun declaredLocally(name: String): Boolean
}
