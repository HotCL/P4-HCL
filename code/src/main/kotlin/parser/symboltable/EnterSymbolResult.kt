package parser.symboltable

sealed class EnterSymbolResult {
    object Success : EnterSymbolResult()
    object OverloadAlreadyDeclared: EnterSymbolResult()
    object OverloadDifferentParamNums: EnterSymbolResult()
    object IdentifierAlreadyDeclared: EnterSymbolResult()
}