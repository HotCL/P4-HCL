package hclTestFramework.parser

import parser.AstNode

typealias num = AstNode.Type.Number
typealias txt = AstNode.Type.Text
typealias bool = AstNode.Type.Bool
typealias none = AstNode.Type.None

infix fun String.asIdentifier(type: AstNode.Type) =
        AstNode.Command.Expression.Value.Identifier(this,type)

infix fun String.declaredAs(type: AstNode.Type) =
        AstNode.Command.Declaration(type, asIdentifier(type))
infix fun String.assignedTo(expression: AstNode.Command.Expression) =
        AstNode.Command.Assignment(asIdentifier(expression.type), expression)
infix fun AstNode.Command.Declaration.withValue(value: AstNode.Command.Expression) = copy(expression = value)

fun func(returnType: AstNode.Type, argTypes: List<AstNode.Type> = listOf()) = AstNode.Type.Func.ExplicitFunc(argTypes, returnType)
fun func(returnType: AstNode.Type, argType: AstNode.Type) = func(returnType, listOf(argType))
fun body(vararg commands: AstNode.Command) = AstNode.Command.Expression.LambdaBody(commands.toList())
fun lambda(argTypes: List<AstNode.ParameterDeclaration> = listOf(),
           returnType: AstNode.Type = none,
           body: AstNode.Command.Expression.LambdaBody = AstNode.Command.Expression.LambdaBody(listOf())) =
        AstNode.Command.Expression.LambdaExpression(argTypes, returnType, body)

infix fun AstNode.Command.Expression.LambdaExpression.returning(type: AstNode.Type) = copy(returnType = type)
infix fun AstNode.Command.Expression.LambdaExpression.withArguments(argTypes: List<AstNode.ParameterDeclaration>) =
        copy(paramDeclarations = argTypes)
infix fun AstNode.Command.Expression.LambdaExpression.withArgument(argType: AstNode.ParameterDeclaration) =
        this withArguments listOf(argType)

infix fun AstNode.Command.Expression.LambdaExpression.andBody(body: AstNode.Command.Expression.LambdaBody) = copy(body = body)
infix fun AstNode.Command.Expression.LambdaExpression.andBody(body: AstNode.Command) = copy(body = body(body))

infix fun AstNode.Command.Expression.LambdaExpression.withBody(body: AstNode.Command.Expression.LambdaBody) = copy(body = body)
infix fun AstNode.Command.Expression.LambdaExpression.withBody(body: AstNode.Command) = copy(body = body(body))

val AstNode.Command.Expression.LambdaBody.asExpression get () = lambda(body = this)

infix fun String.asType(type: AstNode.Type) =
        AstNode.ParameterDeclaration(type, asIdentifier(type))

infix fun AstNode.Command.Expression.Value.Identifier.calledWith(params: List<AstNode.Command.Expression>) =
        AstNode.Command.Expression.FunctionCall(this, params)

infix fun AstNode.Command.Expression.Value.Identifier.calledWith(param: AstNode.Command.Expression) =
        this.calledWith(listOf(param))

fun AstNode.Command.Expression.Value.Identifier.called() = this.calledWith(listOf())

fun generic(string: String) = AstNode.Type.GenericType(string)
fun txt(string: String) = AstNode.Command.Expression.Value.Literal.Text(string)
fun num(num: Double) = AstNode.Command.Expression.Value.Literal.Number(num)
fun num(num: Int) = AstNode.Command.Expression.Value.Literal.Number(num.toDouble())
fun bool(value: Boolean) = AstNode.Command.Expression.Value.Literal.Bool(value)

fun tpl(vararg expressions: AstNode.Command.Expression) =
        AstNode.Command.Expression.Value.Literal.Tuple(expressions.toList())

fun tpl(vararg expressions: AstNode.Type) =  AstNode.Type.Tuple(expressions.toList())
fun list(type: AstNode.Type) = AstNode.Type.List(type)
fun list(vararg expressions: AstNode.Command.Expression) =
        AstNode.Command.Expression.Value.Literal.List(expressions.toList(),expressions.first().type)

fun ret(value: AstNode.Command.Expression) = AstNode.Command.Return(value)
fun AstNode.Command.Expression.returned() = AstNode.Command.Return(this)

infix fun String.returning(type: AstNode.Type) = this.asIdentifier(type)
