package org.maduralang.parser

import org.maduralang.lexer.KeywordToken
import org.maduralang.lexer.NameToken
import org.maduralang.lexer.StringToken
import org.maduralang.lexer.SymbolToken
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ParserTest {

    private val parser = Parser()

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
        assertEquals(FileNode(definitions = listOf(FunctionNode("main"))), result)
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
                    FunctionNode(
                        "main",
                        body = listOf(CallNode("println", arguments = listOf(ConstantNode("\"Hello world\""))))
                    )
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
                    FunctionNode(
                        "main",
                        body = listOf(CallNode("add", arguments = listOf(CallNode("one"), CallNode("two"))))
                    )
                )
            ), result
        )
    }

}