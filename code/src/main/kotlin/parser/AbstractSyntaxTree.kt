package parser

data class AbstractSyntaxTree(val children: MutableList<TreeNode.Command> = mutableListOf())

sealed class TreeNode {
    sealed class Command: TreeNode() {

        data class Declaration(val type: Type, val identifier: Expression.Value.Identifier,
                               val expression: Expression? = null): Command()


        data class Assignment(val identifier: Expression.Value.Identifier, val expression: Expression): Command()

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
            data class LambdaExpression(val paramDeclarations: List<ParameterDeclaration>, val returnType: Type,
                                        val body: List<Command>): Expression()
            data class FunctionCall(val identifier: Value.Identifier, val parameters: List<Expression>): Expression()
        }
        data class Return(val expression: Expression): Command()
    }
    sealed class Type: TreeNode() {
        object Number: Type()
        object Text: Type()
        object Bool: Type()
        object None: Type()
        object Var: Type()
        data class GenericType(val name: String): Type()
        data class List(val elementType: Type): Type()
        sealed class Func: Type() {
            data class ExplicitFunc(val paramTypes: kotlin.collections.List<Type>, val returnType: Type): Func()
            object ImplicitFunc: Func()
        }
        data class Tuple(val elementTypes: kotlin.collections.List<Type>): Type()
    }

    data class ParameterDeclaration(val type: Type, val identifier: Command.Expression.Value.Identifier) : TreeNode()
}
