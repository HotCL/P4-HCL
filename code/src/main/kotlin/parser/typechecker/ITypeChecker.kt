package parser.typechecker

import parser.AstNode
import parser.symboltable.ISymbolTable

interface ITypeChecker {
    fun List<AstNode.Type.Func.ExplicitFunc>.getTypeDeclaration(types: List<AstNode.Type>):
            AstNode.Type.Func.ExplicitFunc?

    fun getTypeOnFuncCall(decl: AstNode.Type.Func.ExplicitFunc, arguments:List<AstNode.Type>): AstNode.Type
    fun getTypeFromTypePairs(pairedTypes: List<Pair<AstNode.Type, AstNode.Type>>, typeName: String): AstNode.Type?
}
