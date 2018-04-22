package symbolTableTests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import parser.TreeNode
import parser.symboltable.Symbol

class SymbolTest {

    @Test
    fun testSymbolClassError() {
        val symbol = Symbol(listOf())
        assertTrue(symbol.undeclared)
        assertTrue(!symbol.isIdentifier)
        assertTrue(!symbol.isFunctions)
        symbol.handle(
                { assertTrue(false) },
                { assertTrue(false) },
                { assertTrue(true)  }
        )
    }

    @Test
    fun testSymbolClassId() {
        val symbol = Symbol(listOf(TreeNode.Type.Number))
        assertTrue(!symbol.undeclared)
        assertTrue(symbol.isIdentifier)
        assertTrue(!symbol.isFunctions)
        symbol.handle(
                { assertTrue(false) },
                { id -> assertTrue(id == TreeNode.Type.Number) },
                { assertTrue(false) }
        )
    }

    @Test
    fun testSymbolClassFunDeclarations() {
        val symbol = Symbol(listOf(
                TreeNode.Type.Func.ExplicitFunc(listOf(TreeNode.Type.Number), TreeNode.Type.Number),
                TreeNode.Type.Func.ExplicitFunc(listOf(TreeNode.Type.Text), TreeNode.Type.Text)
        ))
        assertTrue(!symbol.undeclared)
        assertTrue(!symbol.isIdentifier)
        assertTrue(symbol.isFunctions)
        symbol.handle(
                { funDeclarations ->
                    assertTrue(funDeclarations[0].paramTypes[0] == TreeNode.Type.Number)
                    assertTrue(funDeclarations[0].returnType == TreeNode.Type.Number)
                    assertTrue(funDeclarations[1].paramTypes[0] == TreeNode.Type.Text)
                    assertTrue(funDeclarations[1].returnType == TreeNode.Type.Text)
                },
                { assertTrue(false) },
                { assertTrue(false) }
        )
    }

}