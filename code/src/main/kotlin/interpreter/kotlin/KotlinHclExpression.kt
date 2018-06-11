package interpreter.kotlin

import parser.AstNode

sealed class KotlinHclExpression
data class KotlinIdentifier(val value: String) : KotlinHclExpression()
data class KotlinList(val value: List<KotlinHclExpression>) : KotlinHclExpression() {
    override fun toString() = "[${value.joinToString()}]"
}
data class KotlinNumber(val value: Double) : KotlinHclExpression() {
    override fun toString() = value.toString().removeSuffix(".0")
}
data class KotlinText(val value: String) : KotlinHclExpression() {
    override fun toString() = value
}
data class KotlinBoolean(val value: Boolean) : KotlinHclExpression() {
    override fun toString() = value.toString()
}
data class KotlinTuple(val value: List<KotlinHclExpression>) : KotlinHclExpression() {
    override fun toString() = "(${value.joinToString()})"
}
data class KotlinLambdaExpression(
    val args: List<AstNode.ParameterDeclaration>,
    val returnType: AstNode.Type,
    val body: KotlinLambdaBody,
    val lambdaMemory: Memory
) : KotlinHclExpression()
data class KotlinLambdaCollection(val lambdas: MutableList<KotlinLambdaExpression>) : KotlinHclExpression()
data class KotlinLambdaBody(val value: List<AstNode.Command>) : KotlinHclExpression()
data class KotlinFunctionCall(val identifier: KotlinIdentifier, val arguments: KotlinTuple) : KotlinHclExpression()
object KotlinUnit : KotlinHclExpression()

fun AstNode.Command.Expression.asKotlinExpression(memory: Memory): KotlinHclExpression = when (this) {
    is AstNode.Command.Expression.Value.Identifier -> KotlinIdentifier(name)
    is AstNode.Command.Expression.Value.Literal.Number -> KotlinNumber(value)
    is AstNode.Command.Expression.Value.Literal.Text -> KotlinText(value)
    is AstNode.Command.Expression.Value.Literal.Bool -> KotlinBoolean(value)
    is AstNode.Command.Expression.Value.Literal.Tuple -> KotlinTuple(elements.map { it.asKotlinExpression(memory) })
    is AstNode.Command.Expression.Value.Literal.List -> KotlinList(elements.map { it.asKotlinExpression(memory) })
    is AstNode.Command.Expression.LambdaBody -> KotlinLambdaBody(commands)
    is AstNode.Command.Expression.FunctionCall ->
        KotlinFunctionCall(
                KotlinIdentifier(identifier.name),
                KotlinTuple(arguments.map { it.asKotlinExpression(memory) })
        )
    is AstNode.Command.Expression.LambdaExpression ->
        KotlinLambdaExpression(paramDeclarations, returnType, KotlinLambdaBody(body.commands), memory.shallowCopy())
}