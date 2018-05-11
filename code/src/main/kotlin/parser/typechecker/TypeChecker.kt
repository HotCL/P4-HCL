package parser.typechecker

import parser.AstNode

class TypeChecker : ITypeChecker {
    override fun List<AstNode.Type.Func>.getTypeDeclaration(types: List<AstNode.Type>):
            AstNode.Type.Func? {
        val genericTypes = mutableMapOf<String, AstNode.Type>()

        return this.firstOrNull {
            it.paramTypes.zip(types).all {
                if (it.first == it.second) {
                    if (it.first is AstNode.Type.GenericType)
                        TODO("throw ambigous type error")
                    true
                } else it.matchGenerics(genericTypes)
            }
        }
    }

    override fun getTypeOnFuncCall(decl: AstNode.Type.Func, arguments: List<AstNode.Type>) =
            if (decl.returnType is AstNode.Type.GenericType)
            decl.paramTypes.zip(arguments).getTypeFromGenericType(decl.returnType.name) ?: TODO()
    else decl.returnType

    /**
     * Pair = declaredType and argumentType
     * It is certain that argumentType doesn't contain any generics. This is a rule of the language
     */

    private fun List<Pair<AstNode.Type, AstNode.Type>>.getTypeFromGenericType(typeName: String): AstNode.Type? =
            map { it.getTypeFromGenericType(typeName) }.firstOrNull { it != null }

    override fun getTypeFromTypePairs(pairedTypes: List<Pair<AstNode.Type, AstNode.Type>>, typeName: String) =
            pairedTypes.getTypeFromGenericType(typeName)

    private fun Pair<AstNode.Type, AstNode.Type>.getTypeFromGenericType(typeName: String): AstNode.Type? {

        val declaredType = first
        val argumentType = second
        return when (declaredType) {
            is AstNode.Type.GenericType ->
                if (declaredType.name == typeName) argumentType
                else null

            is AstNode.Type.List ->
                    Pair(declaredType.elementType, (argumentType as AstNode.Type.List).elementType)
                            .getTypeFromGenericType(typeName)
            // expecting count to be the same, as matchGenerics should be run before using getTypeFromGenericType
            is AstNode.Type.Func ->
                    declaredType.paramTypes.zip((argumentType as AstNode.Type.Func).paramTypes)
                            .getTypeFromGenericType(typeName)
                    ?: Pair(declaredType.returnType, argumentType.returnType).getTypeFromGenericType(typeName)

            // expecting count to be the same, as matchGenerics should be run before using getTypeFromGenericType
            is AstNode.Type.Tuple ->
                    declaredType.elementTypes.zip((argumentType as AstNode.Type.Tuple).elementTypes)
                            .getTypeFromGenericType(typeName)

            else -> null
        }
    }

    /**
     * Pair = declaredType and argumentType
     */
    private fun Pair<AstNode.Type, AstNode.Type>.matchGenerics(genericTypes: MutableMap<String, AstNode.Type>): Boolean {
        val declaredType = first
        val argumentType = second

        return when (declaredType) {

            is AstNode.Type.List ->
                if (argumentType is AstNode.Type.List)
                    Pair(declaredType.elementType, argumentType.elementType).matchGenerics(genericTypes)
                else false

            is AstNode.Type.Func ->
                if (argumentType is AstNode.Type.Func &&
                        declaredType.paramTypes.count() == argumentType.paramTypes.count())
                    Pair(declaredType.returnType, argumentType.returnType).matchGenerics(genericTypes) &&
                            declaredType.paramTypes.zip(argumentType.paramTypes).all { it.matchGenerics(genericTypes) }
                else false

            is AstNode.Type.Tuple ->
                if (argumentType is AstNode.Type.Tuple &&
                        declaredType.elementTypes.count() == argumentType.elementTypes.count())
                    declaredType.elementTypes.zip(argumentType.elementTypes).all {
                        it.matchGenerics(genericTypes)
                    }
                else false
            else -> {
                if (declaredType is AstNode.Type.GenericType) {
                    if (genericTypes[declaredType.name] == null)
                        genericTypes[declaredType.name] = argumentType
                    genericTypes[declaredType.name] == argumentType
                } else declaredType == argumentType
            }
        }
    }

    // override val AstNode.Command.Expression.type get() = getTypeOfExpression(this)
}
