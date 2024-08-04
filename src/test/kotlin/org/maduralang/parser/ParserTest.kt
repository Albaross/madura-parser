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
        val result = parser.readFile(emptyMain)
        assertEquals(
            FileNode(definitions = listOf(variableDeclaration(name = "PI", expression = constant(3.14)))),
            result
        )
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
        val result = parser.readFile(constant)
        assertEquals(
            FileNode(
                definitions = listOf(
                    variableDeclaration(
                        name = "enabled",
                        type = "Bool",
                        expression = constant(true),
                        mutable = true
                    )
                )
            ),
            result
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
        assertEquals(FileNode(definitions = listOf(functionDefinition("main", body = emptyList()))), result)
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
        assertEquals(functionCall("double", arguments = listOf(id("x"))), result)
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
        assertEquals(functionCall("run"), result)
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
        assertEquals(functionCall("add", arguments = listOf(functionCall("one"), functionCall("two"))), result)
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
                    functionDefinition(
                        "main",
                        body = functionCall("println", arguments = listOf(constant("\"Hello world\"")))
                    )
                )
            ), result
        )
    }

    @Test
    fun `should consider operator precedence`() {
        val calculation = sourceOf(
            KeywordToken.FN,
            NameToken("calc"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken("=>"),
            NumberToken("1"),
            SymbolToken('+'),
            NumberToken("2"),
            SymbolToken('*'),
            NumberToken("3")
        )

        val result = parser.readFile(calculation)
        assertEquals(
            FileNode(
                definitions = listOf(
                    functionDefinition(
                        "calc",
                        body = arithmetic('+', constant(1), arithmetic('*', constant(2), constant(3)))
                    )
                )
            ), result
        )
    }

    @Test
    fun `should consider parantheses`() {
        val calculation = sourceOf(
            KeywordToken.FN,
            NameToken("calc"),
            SymbolToken('('),
            SymbolToken(')'),
            SymbolToken("=>"),
            SymbolToken('('),
            NumberToken("1"),
            SymbolToken('+'),
            NumberToken("2"),
            SymbolToken(')'),
            SymbolToken('*'),
            NumberToken("3")
        )

        val result = parser.readFile(calculation)
        assertEquals(
            FileNode(
                definitions = listOf(
                    functionDefinition(
                        "calc",
                        body = arithmetic('*', arithmetic('+', constant(1), constant(2)), constant(3))
                    )
                )
            ), result
        )
    }

}