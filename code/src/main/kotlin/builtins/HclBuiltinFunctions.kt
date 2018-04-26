package builtins

import parser.AstNode
import parser.AstNode.Type

private data class Parameter(val identifier: String, val type: Type)

object HclBuiltinFunctions {
    val functions = listOf(
            buildOperator("+"),
            buildOperator("plus", "+"),
            buildOperator("-"),
            buildOperator("*"),
            buildOperator("/")
    )
}

private fun buildOperator(functionName: String, operator: String = functionName) = buildFunction(
        identifier = functionName,
        parameters = listOf(
                Parameter("leftHand", Type.Number),
                Parameter("rightHand", Type.Number)
        ),
        returnType = Type.Number,
        body = "return leftHand $operator rightHand;",
        inLine = true
)

private fun buildFunction(identifier: String, parameters: List<Parameter>, returnType: Type,
                          body: String, inLine: Boolean) =
        AstNode.Command.Declaration(returnType, identifier.asIdentifier(),
                AstNode.Command.Expression.LambdaExpression(
                        paramDeclarations = parameters.map {
                            AstNode.ParameterDeclaration(it.type, it.identifier.asIdentifier())
                        },
                        returnType = returnType,
                        inLine = inLine,
                        body = body.asRawCppLambdaBody()
                )
        )

private fun String.asIdentifier() = AstNode.Command.Expression.Value.Identifier(this)
private fun String.asRawCppLambdaBody() =
        AstNode.Command.Expression.LambdaBody(listOf(AstNode.Command.RawCpp(this)))

