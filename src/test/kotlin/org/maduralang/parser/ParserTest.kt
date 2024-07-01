package org.maduralang.parser

import org.maduralang.lexer.*
import org.maduralang.parser.expr.Constant
import org.maduralang.parser.expr.FunctionCall
import org.maduralang.parser.expr.Id
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
        assertEquals(VariableDeclarationNode(name = "PI", expression = Constant(3.14)), result)
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
                expression = Constant(true),
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
        val result = parser.readIdOrFunctionCall(double)
        assertEquals(FunctionCall("double", arguments = listOf(Id("x"))), result)
    }

    @Test
    fun `should parse semicolon terminated function call`() {
        val run = sourceOf(
            NameToken("run"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken(';')
        )
        val result = parser.readIdOrFunctionCall(run)
        assertEquals(
            FunctionCall("run"), result
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
        val result = parser.readIdOrFunctionCall(nested)
        assertEquals(FunctionCall("add", arguments = listOf(FunctionCall("one"), FunctionCall("two"))), result)
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
                        body = listOf(FunctionCall("println", arguments = listOf(Constant("\"Hello world\""))))
                    )
                )
            ), result
        )
    }

}