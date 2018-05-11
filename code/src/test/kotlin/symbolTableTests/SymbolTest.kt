package symbolTableTests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import parser.AstNode
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
                { assertTrue(true) }
        )
    }

    @Test
    fun testSymbolClassId() {
        val symbol = Symbol(listOf(AstNode.Type.Number))
        assertTrue(!symbol.undeclared)
        assertTrue(symbol.isIdentifier)
        assertTrue(!symbol.isFunctions)
        symbol.handle(
                { assertTrue(false) },
                { id -> assertTrue(id == AstNode.Type.Number) },
                { assertTrue(false) }
        )
    }

    @Test
    fun testSymbolClassFunDeclarations() {
        val symbol = Symbol(listOf(
                AstNode.Type.Func(listOf(AstNode.Type.Number), AstNode.Type.Number),
                AstNode.Type.Func(listOf(AstNode.Type.Text), AstNode.Type.Text)
        ))
        assertTrue(!symbol.undeclared)
        assertTrue(!symbol.isIdentifier)
        assertTrue(symbol.isFunctions)
        symbol.handle(
                { funDeclarations ->
                    assertTrue(funDeclarations[0].paramTypes[0] == AstNode.Type.Number)
                    assertTrue(funDeclarations[0].returnType == AstNode.Type.Number)
                    assertTrue(funDeclarations[1].paramTypes[0] == AstNode.Type.Text)
                    assertTrue(funDeclarations[1].returnType == AstNode.Type.Text)
                },
                { assertTrue(false) },
                { assertTrue(false) }
        )
    }
}