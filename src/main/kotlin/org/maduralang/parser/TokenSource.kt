package org.maduralang.parser

import org.maduralang.lexer.Token
import kotlin.reflect.KClass

class TokenSource(private val tokens: List<Token>) {
    private var index = 0

    fun skip() {
        index++
    }

    fun test(possibly: String): Boolean {
        val result = lookahead().data == possibly
        if (result) index++
        return result
    }

    fun match(expected: String): Token {
        val token = next()
        if (token.data == expected) return token
        else throw InvalidSyntaxException("syntax error", token)
    }

    inline fun <reified T : Token> match(type: KClass<T>): T {
        val token = next()
        if (token is T) return token
        else throw InvalidSyntaxException("syntax error", token)
    }

    fun next(): Token =
        if (hasNext()) tokens[index++] else throw NoSuchElementException()

    fun lookahead(offset: Int = 0): Token =
        if (index + offset < tokens.size) tokens[index + offset] else throw NoSuchElementException()

    fun hasNext(): Boolean =
        index < tokens.size
}