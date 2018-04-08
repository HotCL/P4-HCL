package parser.symbolTable

import parser.TreeNode
import java.util.*

class SymbolTable : ISymbolTable {

    private val symbolTable: Deque<MutableMap<String, MutableList<TreeNode.Type>>> = ArrayDeque()

    init { openScope() }

    override fun openScope() {
        symbolTable.addLast(mutableMapOf())
    }

    override fun closeScope() {
        symbolTable.removeLast()
    }

    override fun enterSymbol(name: String, type: TreeNode.Type): Boolean {
        val entry = symbolTable.last[name]
        return if (entry != null) {
            if (entry.first() is TreeNode.Type.Func && type is TreeNode.Type.Func) {
                entry.add(type)
                true
            } else false
        } else {
            symbolTable.last[name] = mutableListOf(type)
            true
        }
    }

    override fun retrieveSymbol(name: String) =
            // Alright, so this line may actually need a comment...
            // We go backwards through the stack of dictionaries in symbolTable, and add all elements from each
            // dictionary that matches the name of the symbol. Flat map adds all elements from a list of lists into a
            // single list. ((1, 2, 3), (4, 5, 6), (7, 8, 9) flatMapped == (1, 2, 3, 4, 5, 6, 7, 8, 9)).
            Symbol(symbolTable.reversed().map { it[name] }.flatMap { it ?: mutableListOf() })

    override fun declaredLocally(name: String) = symbolTable.last[name] != null
}