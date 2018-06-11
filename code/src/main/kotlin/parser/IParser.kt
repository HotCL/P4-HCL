package parser

interface IParser {
    fun commandSequence(): Sequence<AstNode.Command>
}
