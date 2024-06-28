package org.maduralang.parser

import org.maduralang.lexer.*
import org.maduralang.lexer.KeywordToken.*

class Parser {

    fun parse(tokens: List<Token>): Node =
        parse(ListTokenSource(tokens))

    fun parse(tokens: TokenSource): Node =
        readFile(tokens)

    private fun readFile(tokens: TokenSource): FileNode {
        val definitions = ArrayList<Node>()

        while (tokens.hasNext()) {
            val definition = when (val token = tokens.lookahead()) {
                is KeywordToken -> readDefinition(tokens)
                is AnnotationToken, is CommentToken -> continue
                else -> throw InvalidSyntaxException("syntax error", token)
            }

            definitions.add(definition)
        }

        return FileNode(definitions)
    }

    private fun readDefinition(tokens: TokenSource): Node {
        when (val token = tokens.lookahead()) {
            FN, FUN, FUNC -> return readFunctions(tokens)
            else -> throw InvalidSyntaxException("syntax error", token)
        }
    }

    private fun readFunctions(tokens: TokenSource): FunctionNode {
        tokens.next()
        val name = tokens.match(NameToken::class)
        tokens.match("(")
        val parameters = tokens.collect(delimiter = ")", separator = ",") { readParameter(it) }
        val type = if (tokens.test(":")) readType(tokens) else null

        val body = when (tokens.next().data) {
            "{" -> tokens.collect(delimiter = "}") { readStatement(it) }
            "=>", "->" -> listOf(readStatement(tokens))
            else -> emptyList()
        }

        return FunctionNode(name, parameters, type, body)
    }

    private fun readParameter(tokens: TokenSource): ParameterNode {
        val name = tokens.match(NameToken::class)
        tokens.match(":")
        val type = readType(tokens)
        return ParameterNode(name = name, type = type)
    }

    private fun readType(tokens: TokenSource): NameToken =
        tokens.match(NameToken::class)

    private fun readStatement(tokens: TokenSource): Node =
        when (val token = tokens.lookahead()) {
            is NameToken -> readCall(tokens)
            else -> throw InvalidSyntaxException("syntax error", token)
        }

    private fun readExpression(tokens: TokenSource): Node =
        when (val token = tokens.lookahead()) {
            is NumberToken, is StringToken -> ConstantNode(tokens.next())
            is NameToken -> readCall(tokens)
            else -> throw InvalidSyntaxException("syntax error", token)
        }

    private fun readCall(tokens: TokenSource): Node {
        val name = tokens.match(NameToken::class)
        tokens.match("(")
        val arguments = tokens.collect(delimiter = ")", separator = ",") { readExpression(it) }
        return CallNode(name, arguments)
    }
}