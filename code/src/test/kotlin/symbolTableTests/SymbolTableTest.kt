package symbolTableTests

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import parser.TreeNode
import parser.symboltable.SymbolTable

class SymbolTableTest {
    @Test
    fun testIdentifierSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("x", TreeNode.Type.Number)
        st.enterSymbol("y", TreeNode.Type.Text)
        assertTrue(st.retrieveSymbol("x").identifier == TreeNode.Type.Number)
        assertTrue(st.retrieveSymbol("y").identifier == TreeNode.Type.Text)
    }

    @Test
    fun testIdentifierMultipleScopesSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("x", TreeNode.Type.Number)
        st.openScope()
        st.enterSymbol("x", TreeNode.Type.Text)
        assertTrue(st.retrieveSymbol("x").identifier == TreeNode.Type.Text)
        st.closeScope()
        assertTrue(st.retrieveSymbol("x").identifier == TreeNode.Type.Number)
    }

    @Test
    fun testIdentifierUndeclaredSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("x", TreeNode.Type.Number)
        assertTrue(st.retrieveSymbol("y").undeclared)
    }

    @Test
    fun testFunctionDeclarationsSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("myFun", TreeNode.Type.Func(listOf(TreeNode.Type.Number), TreeNode.Type.Bool))
        val symbol = st.retrieveSymbol("myFun").functions
        assertTrue(symbol[0].paramTypes[0] == TreeNode.Type.Number)
        assertTrue(symbol[0].returnType == TreeNode.Type.Bool)
    }

    @Test
    fun testFunctionDeclarationsMultipleScopesSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("myFun", TreeNode.Type.Func(listOf(TreeNode.Type.Number), TreeNode.Type.Bool))
        st.enterSymbol("myFun", TreeNode.Type.Func(listOf(TreeNode.Type.Bool), TreeNode.Type.Number))
        st.openScope()
        val symbol = st.retrieveSymbol("myFun").functions
        assertTrue(symbol[0].paramTypes[0] == TreeNode.Type.Number)
        assertTrue(symbol[0].returnType == TreeNode.Type.Bool)
        assertTrue(symbol[1].paramTypes[0] == TreeNode.Type.Bool)
        assertTrue(symbol[1].returnType == TreeNode.Type.Number)
        st.closeScope()
    }

    @Test
    fun testFunctionDeclarationsMultipleScopesWithIdentifierInSeparateScopeSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("myFun", TreeNode.Type.Number)
        st.openScope()
        st.enterSymbol("myFun", TreeNode.Type.Func(listOf(TreeNode.Type.Number), TreeNode.Type.Bool))
        st.enterSymbol("myFun", TreeNode.Type.Func(listOf(TreeNode.Type.Bool), TreeNode.Type.Number))
        st.openScope()
        val symbol = st.retrieveSymbol("myFun").functions
        assertTrue(symbol[0].paramTypes[0] == TreeNode.Type.Number)
        assertTrue(symbol[0].returnType == TreeNode.Type.Bool)
        assertTrue(symbol[1].paramTypes[0] == TreeNode.Type.Bool)
        assertTrue(symbol[1].returnType == TreeNode.Type.Number)
        st.closeScope()
        st.closeScope()
        assertTrue(st.retrieveSymbol("myFun").identifier == TreeNode.Type.Number)
    }

    @Test
    fun testIdentifierAlreadyDeclaredSymbolTable() {
        val st = SymbolTable()
        st.enterSymbol("x", TreeNode.Type.Number)
        assertFalse(st.enterSymbol("x", TreeNode.Type.Text))
        assertTrue(st.retrieveSymbol("x").identifier == TreeNode.Type.Number)
    }
}