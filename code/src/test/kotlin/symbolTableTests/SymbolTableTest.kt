package symbolTableTests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import parser.AstNode
import parser.symboltable.EnterSymbolResult
import parser.symboltable.SymbolTable

class SymbolTableTest {
    @Test
    fun testIdentifierSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("x", AstNode.Type.Number)
        st.enterSymbol("y", AstNode.Type.Text)
        assertTrue(st.retrieveSymbol("x").identifier == AstNode.Type.Number)
        assertTrue(st.retrieveSymbol("y").identifier == AstNode.Type.Text)
    }

    @Test
    fun testIdentifierMultipleScopesSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("x", AstNode.Type.Number)
        st.openScope()
        st.enterSymbol("x", AstNode.Type.Text)
        assertTrue(st.retrieveSymbol("x").identifier == AstNode.Type.Text)
        st.closeScope()
        assertTrue(st.retrieveSymbol("x").identifier == AstNode.Type.Number)
    }

    @Test
    fun testIdentifierUndeclaredSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("x", AstNode.Type.Number)
        assertTrue(st.retrieveSymbol("y").undeclared)
    }

    @Test
    fun testFunctionDeclarationsSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("myFun", AstNode.Type.Func.ExplicitFunc(listOf(AstNode.Type.Number), AstNode.Type.Bool))
        val symbol = st.retrieveSymbol("myFun").functions
        assertTrue(symbol[0].paramTypes[0] == AstNode.Type.Number)
        assertTrue(symbol[0].returnType == AstNode.Type.Bool)
    }

    @Test
    fun testFunctionDeclarationsMultipleScopesSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("myFun", AstNode.Type.Func.ExplicitFunc(listOf(AstNode.Type.Number), AstNode.Type.Bool))
        st.enterSymbol("myFun", AstNode.Type.Func.ExplicitFunc(listOf(AstNode.Type.Bool), AstNode.Type.Number))
        st.openScope()
        val symbol = st.retrieveSymbol("myFun").functions
        assertTrue(symbol[0].paramTypes[0] == AstNode.Type.Number)
        assertTrue(symbol[0].returnType == AstNode.Type.Bool)
        assertTrue(symbol[1].paramTypes[0] == AstNode.Type.Bool)
        assertTrue(symbol[1].returnType == AstNode.Type.Number)
        st.closeScope()
    }

    @Test
    fun testFunctionDeclarationsMultipleScopesWithIdentifierInSeparateScopeSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("myFun", AstNode.Type.Number)
        st.openScope()
        st.enterSymbol("myFun", AstNode.Type.Func.ExplicitFunc(listOf(AstNode.Type.Number), AstNode.Type.Bool))
        st.enterSymbol("myFun", AstNode.Type.Func.ExplicitFunc(listOf(AstNode.Type.Bool), AstNode.Type.Number))
        st.openScope()
        val symbol = st.retrieveSymbol("myFun").functions
        assertTrue(symbol[0].paramTypes[0] == AstNode.Type.Number)
        assertTrue(symbol[0].returnType == AstNode.Type.Bool)
        assertTrue(symbol[1].paramTypes[0] == AstNode.Type.Bool)
        assertTrue(symbol[1].returnType == AstNode.Type.Number)
        st.closeScope()
        st.closeScope()
        assertTrue(st.retrieveSymbol("myFun").identifier == AstNode.Type.Number)
    }

    @Test
    fun testIdentifierAlreadyDeclaredSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("x", AstNode.Type.Number)
        assertTrue(st.retrieveSymbol("x").identifier == AstNode.Type.Number)
        assertTrue(st.enterSymbol("x", AstNode.Type.Text) == EnterSymbolResult.IdentifierAlreadyDeclared)
    }
}
