package org.maduralang.parser

import org.maduralang.lexer.*
import org.maduralang.lexer.KeywordToken.*

class Parser {

    fun parse(input: List<Token>): Node {
        val tokens = input.filter { it !is CommentToken }.iterator()
        return readFile(tokens)
    }

    private fun readFile(tokens: Iterator<Token>): FileNode {
        val definitions = ArrayList<Node>()

        while (tokens.hasNext()) {
            val definition = when (val token = tokens.next()) {
                is KeywordToken -> readDefinition(token, tokens)
                is AnnotationToken -> continue
                else -> throw InvalidSyntaxException("syntax error", token)
            }

            definitions.add(definition)
        }

        return FileNode(definitions)
    }

    private fun readDefinition(token: KeywordToken, tokens: Iterator<Token>): Node {
        when (token) {
            FN, FUN, FUNC -> return readFunctions(token, tokens)
            else -> throw InvalidSyntaxException("syntax error", token)
        }
    }

    private fun readFunctions(token: KeywordToken, tokens: Iterator<Token>): FunctionNode {
        val name = matchType(tokens.next(), NameToken::class.java)
        match(tokens.next(), "(")
        val parameters = collect(tokens, delimiter = ")", separator = ",", ::readParameter)
        var token = tokens.next()
        var type: NameToken? = null

        if (token.data == ":") {
            type = readType(tokens)
            token = tokens.next()
        }

        val body = when (token.data) {
            "{" -> collect(tokens, "}", null, ::readStatement)
            "=>", "->" -> listOf(readStatement(tokens.next(), tokens))
            else -> emptyList()
        }

        return FunctionNode(name, parameters, type, body)
    }

    private fun readParameter(token: Token, tokens: Iterator<Token>): ParameterNode {
        val name = matchType(token, NameToken::class.java)
        match(tokens.next(), ":")
        val type = readType(tokens)
        return ParameterNode(name = name, type = type)
    }

    private fun readType(tokens: Iterator<Token>): NameToken =
        matchType(tokens.next(), NameToken::class.java)

    private fun readStatement(token: Token, tokens: Iterator<Token>): Node =
        when (token) {
            is NameToken -> readCall(token, tokens)
            else -> throw InvalidSyntaxException("syntax error", token)
        }

    private fun readExpression(token: Token, tokens: Iterator<Token>): Node =
        when (token) {
            is NumberToken, is StringToken -> ConstantNode(token)
            is NameToken -> readCall(token, tokens)
            else -> throw InvalidSyntaxException("syntax error", token)
        }

    private fun readCall(name: NameToken, tokens: Iterator<Token>): Node {
        match(tokens.next(), "(")
        val arguments = collect(tokens, delimiter = ")", separator = ",", ::readExpression)
        return CallNode(name, arguments)
    }

    private fun match(token: Token, expected: String): Token =
        if (token.data == expected) token else throw InvalidSyntaxException("syntax error", token)

    private inline fun <reified T : Token> matchType(token: Token, type: Class<T>): T =
        if (token is T) token else throw InvalidSyntaxException("syntax error", token)

    private fun <T> collect(
        tokens: Iterator<Token>,
        delimiter: String,
        separator: String?,
        read: (Token, Iterator<Token>) -> T
    ): List<T> {
        val result = ArrayList<T>()
        var counter = 0

        while (tokens.hasNext()) {
            var token = tokens.next()
            if (token.data == delimiter) return result
            if (separator != null && counter > 0) {
                match(token, separator)
                token = tokens.next()
            }
            result.add(read(token, tokens))
            counter++
        }

        return result
    }
}