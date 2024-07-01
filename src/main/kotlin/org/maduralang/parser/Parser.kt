package org.maduralang.parser

import org.maduralang.lexer.*
import org.maduralang.lexer.KeywordToken.*
import org.maduralang.parser.expr.Constant
import org.maduralang.parser.expr.Expression
import org.maduralang.parser.expr.FunctionCall
import org.maduralang.parser.expr.Id
import org.maduralang.parser.stmt.Statement

class Parser {

    fun readFile(tokens: TokenSource): FileNode {
        val definitions = ArrayList<DefinitionNode>()

        while (tokens.hasNext()) {
            val definition = when (val token = tokens.lookahead()) {
                is KeywordToken -> readDefinition(tokens)
                is AnnotationToken, is CommentToken -> continue
                else -> throw InvalidSyntaxException("syntax error", token)
            }

            definitions.add(definition)
        }

        return FileNode(definitions = definitions)
    }

    private fun readDefinition(tokens: TokenSource): DefinitionNode {
        return when (val token = tokens.lookahead()) {
            PUBLIC, PRIVATE, PROTECTED, SHARED -> TODO("Access modifier '$token' not yet implemented")
            LET, VAR, CONST -> readVariableDeclaration(tokens)
            FN, FUN, FUNC -> readFunctionDefinition(tokens)
            CLASS, ENUM -> TODO("class definition '$token' not yet implemented")
            else -> throw InvalidSyntaxException("syntax error", token)
        }
    }

    fun readVariableDeclaration(tokens: TokenSource): VariableDeclarationNode {
        val mutable = (tokens.match(KeywordToken::class) != CONST)
        val name = tokens.match(NameToken::class)
        val type = if (tokens.test(":")) readType(tokens) else null
        val value = if (tokens.test("=")) readExpression(tokens) else null
        tokens.test(";")
        return VariableDeclarationNode(name, type, value, mutable)
    }

    fun readFunctionDefinition(tokens: TokenSource): FunctionDefinitionNode {
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

        return FunctionDefinitionNode(name, parameters, type, body)
    }

    private fun readParameter(tokens: TokenSource): ParameterNode {
        val name = tokens.match(NameToken::class)
        tokens.match(":")
        val type = readType(tokens)
        return ParameterNode(name = name, type = type)
    }

    private fun readType(tokens: TokenSource): NameToken =
        tokens.match(NameToken::class)

    private fun readStatement(tokens: TokenSource): Statement =
        when (val token = tokens.lookahead()) {
            is NameToken -> readIdOrFunctionCall(tokens)
            RETURN -> readExpression(tokens)
            else -> throw InvalidSyntaxException("syntax error", token)
        }

    fun readExpression(tokens: TokenSource): Expression =
        when (val token = tokens.lookahead()) {
            is NumberToken, is StringToken, TRUE, FALSE -> Constant(tokens.next())
            THIS, SUPER -> Id(tokens.next() as WordToken)
            is NameToken -> readIdOrFunctionCall(tokens)
            else -> throw InvalidSyntaxException("syntax error", token)
        }

    fun readIdOrFunctionCall(tokens: TokenSource): Expression {
        val name = tokens.match(NameToken::class)
        if (tokens.test("(")) {
            val arguments = tokens.collect(delimiter = ")", separator = ",") { readExpression(it) }
            tokens.test(";")
            return FunctionCall(name, arguments)
        }
        tokens.test(";")
        return Id(name)
    }
}