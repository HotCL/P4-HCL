package parser.symboltable

import parser.AstNode
import java.util.*

/**
 * The generic implementation of the ISymbolTable
 */
class SymbolTable : ISymbolTable {

    private val symbolTable: Deque<MutableMap<String, MutableList<AstNode.Type>>> = ArrayDeque()

    init { openScope() }

    override fun openScope() {
        symbolTable.addLast(mutableMapOf())
    }

    override fun closeScope() {
        symbolTable.removeLast()
    }

    private fun checkFunctionIsAllowed(func: AstNode.Type.Func.ExplicitFunc, name: String): EnterSymbolResult =
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
            if (entryFirst is AstNode.Type.Func.ExplicitFunc && type is AstNode.Type.Func.ExplicitFunc) {
                checkFunctionIsAllowed(type, name).also { if (it == EnterSymbolResult.Success) entry.add(type) }
            } else EnterSymbolResult.IdentifierAlreadyDeclared
        } else {
            val res = if (type is AstNode.Type.Func.ExplicitFunc) checkFunctionIsAllowed(type, name)
                      else EnterSymbolResult.Success
            res.also { if (it == EnterSymbolResult.Success) symbolTable.last[name] = mutableListOf(type) }
        }
    }

    override fun retrieveSymbol(name: String) =
            // We go backwards through the stack of dictionaries in symboltable, and add all elements from each
            // dictionary that matches the name of the symbol. Flat map adds all elements from a list of lists into a
            // Example: single list. ((1, 2, 3), (4, 5, 6), (7, 8, 9) flatMapped == (1, 2, 3, 4, 5, 6, 7, 8, 9)).
            Symbol(symbolTable.reversed().map { it[name] }.flatMap { it ?: mutableListOf() })

    override fun declaredLocally(name: String) = symbolTable.last[name] != null
}
