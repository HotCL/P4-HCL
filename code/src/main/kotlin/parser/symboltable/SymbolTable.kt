package parser.symboltable

import parser.AstNode
import java.util.* // ktlint-disable no-wildcard-imports

/**
 * The generic implementation of the ISymbolTable
 */
class SymbolTable : ISymbolTable {

    private val symbolTable: Deque<MutableMap<String, MutableList<AstNode.Type>>> = ArrayDeque()
    private val typeTable: Deque<MutableSet<AstNode.Type.GenericType>> = ArrayDeque()

    init { openScope() }

    override fun openScope() {
        symbolTable.addLast(mutableMapOf())
        typeTable.addLast(mutableSetOf())
    }

    override fun closeScope() {
        symbolTable.removeLast()
        typeTable.removeLast()
    }

    private fun checkFunctionIsAllowed(func: AstNode.Type.Func, name: String): EnterSymbolResult =
        retrieveSymbol(name).handle(
                {
                    it.forEach {
                        if (it.paramTypes.size != func.paramTypes.size) {
                            return@handle EnterSymbolResult.OverloadDifferentParamNums
                        } else if (it.paramTypes == func.paramTypes) {
                            return@handle EnterSymbolResult.OverloadAlreadyDeclared
                        }
                    }
                    EnterSymbolResult.Success
                },
                { EnterSymbolResult.Success },
                { EnterSymbolResult.Success }
        )

    override fun enterSymbol(name: String, type: AstNode.Type): EnterSymbolResult {
        val entry = symbolTable.last[name]
        return if (entry != null) {
            val entryFirst = entry.first()
            if (entryFirst is AstNode.Type.Func && type is AstNode.Type.Func) {
                checkFunctionIsAllowed(type, name).also { if (it == EnterSymbolResult.Success) entry.add(type) }
            } else EnterSymbolResult.IdentifierAlreadyDeclared
        } else {
            val res = if (type is AstNode.Type.Func) checkFunctionIsAllowed(type, name)
            else EnterSymbolResult.Success
            res.also {
                if (it == EnterSymbolResult.Success) symbolTable.last[name] = mutableListOf(type)
                if (type is AstNode.Type.Tuple) {
                    type.elementTypes.forEachIndexed { index, element ->
                        enterSymbol("element$index", AstNode.Type.Func(listOf(), element))
                    }
                    enterSymbol("toText", AstNode.Type.Func(listOf(type), AstNode.Type.Text))
                }
            }
        }
    }

    override fun retrieveSymbol(name: String) =
            // We go backwards through the stack of dictionaries in symboltable, and add all elements from each
            // dictionary that matches the name of the symbol. Flat map adds all elements from a list of lists into a
            // Example: single list. ((1, 2, 3), (4, 5, 6), (7, 8, 9) flatMapped == (1, 2, 3, 4, 5, 6, 7, 8, 9)).
            Symbol(symbolTable.reversed().map { it[name] }.flatMap { it ?: mutableListOf() })

    override fun enterType(type: AstNode.Type.GenericType) {
        typeTable.last.add(type)
    }

    override fun genericTypeInScope(type: AstNode.Type.GenericType): Boolean {
        return genericTypeInScope(type.name)
    }
    override fun genericTypeInScope(typeName: String): Boolean {
        return typeTable.any { it.any { typeName == it.name } }
    }
}
