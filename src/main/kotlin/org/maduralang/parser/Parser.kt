package org.maduralang.parser

import org.maduralang.lexer.*
import org.maduralang.lexer.KeywordToken.*

class Parser {

    fun parse(input: List<Token>): Node {
        return TokenSource(input.filter { it !is CommentToken }).readFile()
    }

    private fun TokenSource.readFile(): FileNode {
        val definitions = ArrayList<Node>()

        while (hasNext()) {
            val definition = when (val token = lookahead()) {
                is KeywordToken -> readDefinition()
                is AnnotationToken -> continue
                else -> throw InvalidSyntaxException("syntax error", token)
            }

            definitions.add(definition)
        }

        return FileNode(definitions)
    }

    private fun TokenSource.readDefinition(): Node {
        when (val token = lookahead()) {
            FN, FUN, FUNC -> return readFunctions()
            else -> throw InvalidSyntaxException("syntax error", token)
        }
    }

    private fun TokenSource.readFunctions(): FunctionNode {
        skip()
        val name = match(NameToken::class)
        match("(")
        val parameters = collect(delimiter = ")", separator = ",") { readParameter() }
        val type = if (test(":")) readType() else null

        val body = when (next().data) {
            "{" -> collect(delimiter = "}") { readStatement() }
            "=>", "->" -> listOf(readStatement())
            else -> emptyList()
        }

        return FunctionNode(name, parameters, type, body)
    }

    private fun TokenSource.readParameter(): ParameterNode {
        val name = match(NameToken::class)
        match(":")
        val type = readType()
        return ParameterNode(name = name, type = type)
    }

    private fun TokenSource.readType(): NameToken =
        match(NameToken::class)

    private fun TokenSource.readStatement(): Node =
        when (val token = lookahead()) {
            is NameToken -> readCall()
            else -> throw InvalidSyntaxException("syntax error", token)
        }

    private fun TokenSource.readExpression(): Node =
        when (val token = lookahead()) {
            is NumberToken, is StringToken -> ConstantNode(next())
            is NameToken -> readCall()
            else -> throw InvalidSyntaxException("syntax error", token)
        }

    private fun TokenSource.readCall(): Node {
        val name = match(NameToken::class)
        match("(")
        val arguments = collect(delimiter = ")", separator = ",") { readExpression() }
        return CallNode(name, arguments)
    }

    private fun <T> TokenSource.collect(
        delimiter: String,
        separator: String? = null,
        read: () -> T
    ): List<T> {
        val result = ArrayList<T>()
        var counter = 0

        while (hasNext()) {
            if (test(delimiter))
                return result

            if (separator != null && counter > 0)
                match(separator)

            result.add(read())
            counter++
        }

        return result
    }
}