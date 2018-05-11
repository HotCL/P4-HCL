package parser.typechecker

import parser.AstNode

interface ITypeChecker {
    fun List<AstNode.Type.Func>.getTypeDeclaration(types: List<AstNode.Type>):
            AstNode.Type.Func?

    fun getTypeOnFuncCall(decl: AstNode.Type.Func, arguments:List<AstNode.Type>): AstNode.Type
    fun getTypeFromTypePairs(pairedTypes: List<Pair<AstNode.Type, AstNode.Type>>, typeName: String): AstNode.Type?
}
