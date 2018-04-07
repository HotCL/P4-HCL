package SymbolTable
import parser.TreeNode

data class Symbol(val name: String, val type: TreeNode.Type){
}