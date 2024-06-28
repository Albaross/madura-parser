package org.maduralang.parser

import org.maduralang.lexer.*
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ParserTest {

    private val parser = Parser()

    @Test
    fun `should parse global constants`() {
        val emptyMain = sourceOf(
            KeywordToken.CONST,
            NameToken("PI"),
            SymbolToken('='),
            NumberToken("3.14")
        )
        val result = parser.readVariableDeclaration(emptyMain)
        assertEquals(VariableDeclarationNode(name = "PI", expression = ConstantNode(3.14)), result)
    }

    @Test
    fun `should parse global variables`() {
        val constant = sourceOf(
            KeywordToken.LET,
            NameToken("enabled"),
            SymbolToken(':'),
            NameToken("Bool"),
            SymbolToken('='),
            KeywordToken.TRUE
        )
        val result = parser.readVariableDeclaration(constant)
        assertEquals(
            VariableDeclarationNode(
                name = "enabled",
                type = "Bool",
                expression = ConstantNode(true),
                mutable = true
            ), result
        )
    }

    @Test
    fun `should parse top level function with empty body`() {
        val emptyMain = sourceOf(
            KeywordToken.FN,
            NameToken("main"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken('{'),
            SymbolToken('}')
        )
        val result = parser.readFile(emptyMain)
        assertEquals(FileNode(definitions = listOf(FunctionDefinitionNode("main"))), result)
    }

    @Test
    fun `should parse variable access in function call`() {
        val double = sourceOf(
            NameToken("double"),
            SymbolToken('('),
            NameToken("x"),
            SymbolToken(')'),
        )
        val result = parser.readAccessOrCall(double)
        assertEquals(CallNode("double", arguments = listOf(AccessNode("x"))), result)
    }

    @Test
    fun `should parse semicolon terminated function call`() {
        val run = sourceOf(
            NameToken("run"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken(';')
        )
        val result = parser.readAccessOrCall(run)
        assertEquals(
            CallNode("run"), result
        )
    }

    @Test
    fun `should parse nested function calls`() {
        val nested = sourceOf(
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
        val result = parser.readAccessOrCall(nested)
        assertEquals(CallNode("add", arguments = listOf(CallNode("one"), CallNode("two"))), result)
    }

    @Test
    fun `should parse hello world`() {
        val helloWorld = sourceOf(
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
        val result = parser.readFile(helloWorld)
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

}