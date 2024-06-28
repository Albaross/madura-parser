package org.maduralang.parser

import org.maduralang.lexer.Token
import java.io.EOFException
import kotlin.reflect.KClass

interface TokenSource: Iterator<Token> {

    fun lookahead(): Token

    fun test(possibly: String): Boolean {
        val result = lookahead().data == possibly
        if (result) next()
        return result
    }

    fun match(expected: String): Token {
        val token = next()
        if (token.data == expected) return token
        else throw InvalidSyntaxException("syntax error", token)
    }

    fun <T : Token> match(type: KClass<T>): T {
        val token = next()
        if (type.isInstance(token)) return token as T
        else throw InvalidSyntaxException("syntax error", token)
    }

    fun <T> collect(delimiter: String, separator: String? = null, read: (TokenSource) -> T): List<T> {
        val result = ArrayList<T>()
        var counter = 0

        while (hasNext()) {
            if (test(delimiter))
                return result

            if (separator != null && counter > 0)
                match(separator)

            result.add(read(this))
            counter++
        }

        throw EOFException()
    }
}

class ListTokenSource(private val tokens: List<Token>) : TokenSource {
    private var index = 0

    override fun hasNext(): Boolean =
        index < tokens.size

    override fun lookahead(): Token =
        if (hasNext()) tokens[index] else throw NoSuchElementException()

    override fun next(): Token =
        if (hasNext()) tokens[index++] else throw NoSuchElementException()
}