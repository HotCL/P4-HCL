package parser.symboltable

import parser.AstNode

/**
 * The interface which all Symbol Table implementations must implement.
 */
interface ISymbolTable {
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
    fun enterSymbol(name: String, type: AstNode.Type): EnterSymbolResult

    /**
     * Returns a list of the symbol table's currently valid declarations for name.
     * If no declaration for name is currently in effect, then an empty list is returned.
     * @param name The name of the identifier.
     */
    fun retrieveSymbol(name: String): Symbol

    /**
     * Enters a generic type in symbol table
     * @param type The generic type
     */
    fun enterType(type: AstNode.Type.GenericType)

    /**
     * Checks if a generic type has been entered in currect scope
     * @param type The generic type that is checked for
     * @return Whether the type has been entered
     */
    fun genericTypeInScope(type: AstNode.Type.GenericType): Boolean

    /**
     * Checks if a generic type has been entered in currect scope
     * @param typeName The name of the generic type that is checked for
     * @return Whether the type has been entered
     */
    fun genericTypeInScope(typeName: String): Boolean
}
