package SymbolTable

import parser.TreeNode

/**
 * The interface which all Symbol Table implementations must implement.
 */
interface ISymbolTable{
    /**
     * Opens a new scope in the symbol table.
     */
    fun openScope(): Unit

    /**
     * Closes the most recently opened scope in the symbol table.
     * Symbol references subsequently revert to outer scopes.
     */
    fun closeScope(): Unit

    /**
     * Enters name in the symbol table's current scope.
     * @param name The name of the identfier.
     * @param type The Type attributes of the identifier.
     */
    fun enterSymbol(name: String, type: TreeNode.Type)

    /**
     * Returns the symbol table's currently valid declaration for name.
     * If no declaration for name is currently in effect, then null is returned.
     * @param name The name of the identifier.
     */
    fun retrieveSymbol(name: String): Symbol.Symbol?

    /**
     * Tests whether name is present in the symbol table's current scope.
     * True is returned if it is.
     * Otherwise False is returned.
     * @param name The name of the identifier.
     */
    fun DeclaredLocally(name: String): Boolean
}
