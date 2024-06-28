package org.maduralang.parser

import org.maduralang.lexer.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ParserTest {

    private val parser = Parser()

    @Test
    fun `should parse global constants`() {
        val emptyMain = listOf(
            KeywordToken.CONST,
            NameToken("PI"),
            SymbolToken('='),
            NumberToken("3.14")
        )
        val result = parser.parse(emptyMain)
        assertEquals(
            FileNode(
                definitions = listOf(
                    VariableDeclarationNode(
                        name = "PI",
                        expression = ConstantNode(3.14)
                    )
                )
            ), result
        )
    }

    @Test
    fun `should parse global variables`() {
        val emptyMain = listOf(
            KeywordToken.LET,
            NameToken("enabled"),
            SymbolToken(':'),
            NameToken("Bool"),
            SymbolToken('='),
            KeywordToken.TRUE
        )
        val result = parser.parse(emptyMain)
        assertEquals(
            FileNode(
                definitions = listOf(
                    VariableDeclarationNode(
                        name = "enabled",
                        type = "Bool",
                        expression = ConstantNode(true),
                        mutable = true
                    )
                )
            ), result
        )
    }

    @Test
    fun `should parse top level function with empty body`() {
        val emptyMain = listOf(
            KeywordToken.FN,
            NameToken("main"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken('{'),
            SymbolToken('}')
        )
        val result = parser.parse(emptyMain)
        assertEquals(FileNode(definitions = listOf(FunctionDefinitionNode("main"))), result)
    }

    @Test
    fun `should parse variable access in function call`() {
        val emptyMain = listOf(
            KeywordToken.FN,
            NameToken("main"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken("=>"),
            NameToken("double"),
            SymbolToken('('),
            NameToken("x"),
            SymbolToken(')'),
        )
        val result = parser.parse(emptyMain)
        assertEquals(
            FileNode(
                definitions = listOf(
                    FunctionDefinitionNode(
                        "main",
                        body = listOf(CallNode("double", arguments = listOf(AccessNode("x"))))
                    )
                )
            ), result
        )
    }

    @Test
    fun `should parse hello world`() {
        val helloWorld = listOf(
            KeywordToken.FN,
            NameToken("main"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken("=>"),
            NameToken("println"),
            SymbolToken('('),
            StringToken("\"Hello world\""),
            SymbolToken(')')
        )
        val result = parser.parse(helloWorld)
        assertEquals(
            FileNode(
                definitions = listOf(
                    FunctionDefinitionNode(
                        "main",
                        body = listOf(CallNode("println", arguments = listOf(ConstantNode("\"Hello world\""))))
                    )
                )
            ), result
        )
    }

    @Test
    fun `should parse semicolon terminated function call`() {
        val helloWorld = listOf(
            KeywordToken.FN,
            NameToken("main"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken("=>"),
            NameToken("run"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken(';')
        )
        val result = parser.parse(helloWorld)
        assertEquals(
            FileNode(
                definitions = listOf(
                    FunctionDefinitionNode("main", body = listOf(CallNode("run")))
                )
            ), result
        )
    }

    @Test
    fun `should parse nested function calls`() {
        val nested = listOf(
            KeywordToken.FN,
            NameToken("main"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken("=>"),
            NameToken("add"),
            SymbolToken('('),
            NameToken("one"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken(','),
            NameToken("two"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken(')')
        )
        val result = parser.parse(nested)
        assertEquals(
            FileNode(
                definitions = listOf(
                    FunctionDefinitionNode(
                        "main",
                        body = listOf(CallNode("add", arguments = listOf(CallNode("one"), CallNode("two"))))
                    )
                )
            ), result
        )
    }

}