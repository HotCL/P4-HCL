package parser

data class AbstractSyntaxTree(val children: MutableList<TreeNode.Command> = mutableListOf())

sealed class TreeNode {
    sealed class Command: TreeNode() {
        data class Declaration(val type: Type, val identifier: Expression.Value.Identifier, val expression: Expression? = null): Command()
        sealed class Expression: Command() {
            sealed class Value: Expression() {
                data class Identifier(val name: String): Value()
                sealed class Literal: Value() {
                    data class Number(val value: Double): Literal()
                    data class Text(val value: String): Literal()
                    data class Bool(val value: Boolean): Literal()
                    data class Tuple(val elements: kotlin.collections.List<Expression>): Literal()
                    data class List(val elements: kotlin.collections.List<Expression>): Literal()
                }
            }
            data class LambdaExpression(val paramDeclarations: List<Declaration>, val returnType: Type, val body: List<Command>): Expression()
            data class FunctionCall(val identifier: Value.Identifier, val parameters: List<Expression>): Expression()
        }
        data class Return(val expression: Expression): Command()
    }
    sealed class Type {
        class Number: Type()
        class Text: Type()
        class Bool: Type()
        class None: Type()
        class Var: Type()
        data class GenericType(val name: String): Type()
        data class List(val elementType: Type): Type()
        data class Func(val paramTypes: kotlin.collections.List<Type>, val returnType: Type): Type()
        data class Tuple(val elementTypes: kotlin.collections.List<Type>): Type()
    }
}
