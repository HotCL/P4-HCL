package parser.symboltable

import parser.AstNode
import java.util.*

class SymbolTable : ISymbolTable {

    private val symbolTable: Deque<MutableMap<String, MutableList<AstNode.Type>>> = ArrayDeque()

    init { openScope() }

    override fun openScope() {
        symbolTable.addLast(mutableMapOf())
    }

    override fun closeScope() {
        symbolTable.removeLast()
    }

    private fun assertFunctionIsAllowed(func: AstNode.Type.Func.ExplicitFunc, name: String) {
        retrieveSymbol(name).handle(
                {
                    it.forEach {
                        if (it.paramTypes.size != func.paramTypes.size) {
                            throw Exception("Unable to overload with different amount of parameters!")
                        } else if (it.paramTypes == func.paramTypes) {
                            throw Exception("Function of same name with these parameters has already been declared!")
                        }
                    }
                },
                {},
                {}
        )
    }

    override fun enterSymbol(name: String, type: AstNode.Type) {
        val entry = symbolTable.last[name]
        if (entry != null) {
            val entryFirst = entry.first()
            if (entryFirst is AstNode.Type.Func.ExplicitFunc && type is AstNode.Type.Func.ExplicitFunc) {
                assertFunctionIsAllowed(type, name)
                entry.add(type)
            } else throw Exception("Identifier $name was already declared!")
        } else {
            if (type is AstNode.Type.Func.ExplicitFunc) assertFunctionIsAllowed(type, name)
            symbolTable.last[name] = mutableListOf(type)
        }
    }

    override fun retrieveSymbol(name: String) =
            // We go backwards through the stack of dictionaries in symboltable, and add all elements from each
            // dictionary that matches the name of the symbol. Flat map adds all elements from a list of lists into a
            // Example: single list. ((1, 2, 3), (4, 5, 6), (7, 8, 9) flatMapped == (1, 2, 3, 4, 5, 6, 7, 8, 9)).
            Symbol(symbolTable.reversed().map { it[name] }.flatMap { it ?: mutableListOf() })

    override fun declaredLocally(name: String) = symbolTable.last[name] != null
}
