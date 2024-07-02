package org.maduralang.parser

import org.maduralang.lexer.Token
import java.io.EOFException
import kotlin.reflect.KClass

interface TokenSource : Iterator<Token> {

    fun lookahead(): Token

    fun test(consume: Boolean = true, predicate: (Token) -> Boolean): Boolean {
        val result = hasNext() && predicate(lookahead())
        if (consume && result) next()
        return result
    }

    fun test(possibly: String): Boolean =
        test { it.data == possibly }

    fun match(predicate: (Token) -> Boolean): Token {
        val token = next()
        if (predicate(token)) return token
        else throw InvalidSyntaxException("syntax error", token)
    }

    fun match(expected: String): Token =
        match { it.data == expected }

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

fun sourceOf(vararg tokens: Token): TokenSource =
    ListTokenSource(tokens.toList())

class ListTokenSource(private val tokens: List<Token>) : TokenSource {
    private var index = 0

    override fun hasNext(): Boolean =
        index < tokens.size

    override fun lookahead(): Token =
        if (hasNext()) tokens[index] else throw NoSuchElementException()

    override fun next(): Token =
        if (hasNext()) tokens[index++] else throw NoSuchElementException()
}