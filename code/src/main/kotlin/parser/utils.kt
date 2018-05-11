package parser

data class Parameter(val identifier: String, val type: AstNode.Type)

fun buildFunction(
    identifier: String,
    parameters: List<Parameter>,
    returnType: AstNode.Type,
    body: String,
    attributes: LambdaExpressionAttributes = BuiltinLambdaAttributes
) =
        AstNode.Command.Declaration(returnType, identifier.asIdentifier(returnType),
                AstNode.Command.Expression.LambdaExpression(
                        paramDeclarations = parameters.map {
                            AstNode.ParameterDeclaration(it.type, it.identifier.asIdentifier(returnType))
                        },
                        returnType = returnType,
                        attributes = attributes,
                        body = body.asRawCppLambdaBody()
                )
        )

private fun String.asIdentifier(type: AstNode.Type) =
        AstNode.Command.Expression.Value.Identifier(this, type)
private fun String.asRawCppLambdaBody() =
        AstNode.Command.Expression.LambdaBody(listOf(AstNode.Command.RawCpp(this)))
