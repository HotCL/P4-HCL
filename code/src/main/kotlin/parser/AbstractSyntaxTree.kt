package parser

data class AbstractSyntaxTree(val children: MutableList<AstNode.Command> = mutableListOf()){
    inline fun filter(predicate: (AstNode.Command) -> Boolean): AbstractSyntaxTree {
        return AbstractSyntaxTree(children.filter(predicate))
    }
    constructor(children: Iterable<AstNode.Command>) : this(children.toMutableList())
}

typealias AstExpression = AstNode.Command.Expression
typealias AstIdentifier = AstNode.Command.Expression.Value.Identifier
typealias AstLiteral = AstNode.Command.Expression.Value.Literal
