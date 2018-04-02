package parser

data class AbstractSyntaxTree(val childs: MutableList<TreeNode.Commands> = mutableListOf())

sealed class TreeNode {
    sealed class Commands {
        data class Declaration(val type: Type, val identifier: Expression.Value.Identifier, val expression: Expression?)
        data class Assignment(val identifier: Expression.Value.Identifier, val expression: Expression)
        sealed class Expression {
            sealed class Value {
                data class Identifier(val name: String)
                sealed class Literal {
                    data class Number(val value: Double)
                    data class Text(val value: String)
                    data class Bool(val value: Boolean)
                    data class Tuple(val elements: kotlin.collections.List<Expression>)
                    data class List(val elements: kotlin.collections.List<Expression>)
                }
            }
            data class LambdaExpression(val paramDeclarations: List<Declaration>, val returnType: Type, val body: List<Commands>)
            data class FunctionCall(val identifier: Value.Identifier, val parameters: List<Expression>)
        }
        data class Return(val expression: Expression)
    }
    sealed class Type {
        class Text
        class Bool
        class None
        class Var
        data class GenericType(val name: String)
        data class List(val elementType: Type)
        data class Func(val paramTypes: kotlin.collections.List<Type>, val returnType: Type)
        data class Tuple(val elementTypes: kotlin.collections.List<Type>)
    }
}
