package generation

import parser.AstNode


fun AstNode.Type.makePretty(): String = when(this){
    AstNode.Type.Number -> "num"
    AstNode.Type.Text -> "text"
    AstNode.Type.Bool -> "bool"
    AstNode.Type.None -> "none"
    is AstNode.Type.Func ->
        "func[${
        if (!paramTypes.isEmpty())"${paramTypes.joinToString { it.makePretty() }}, "
        else ""
        }${returnType.makePretty()}]"
    is AstNode.Type.GenericType -> name
    is AstNode.Type.List -> "list[${elementType.makePretty()}]"
    is AstNode.Type.Tuple -> "tuple[${elementTypes.joinToString { it.makePretty() }}]"
}
