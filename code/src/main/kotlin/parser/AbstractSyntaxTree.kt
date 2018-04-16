package parser

data class AbstractSyntaxTree(val children: MutableList<AstNode.Command> = mutableListOf())

typealias AstExpression = AstNode.Command.Expression
typealias AstIdentifier = AstNode.Command.Expression.Value.Identifier
typealias AstLiteral = AstNode.Command.Expression.Value.Literal