package org.maduralang.parser

import org.maduralang.lexer.*
import org.maduralang.lexer.KeywordToken.*

class Parser {

    fun parse(tokens: List<Token>): Node =
        parse(ListTokenSource(tokens))

    fun parse(tokens: TokenSource): Node =
        readFile(tokens)

    private fun readFile(tokens: TokenSource): FileNode {
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

    private fun readVariableDeclaration(tokens: TokenSource): VariableDeclarationNode {
        val mutable = (tokens.match(KeywordToken::class) != CONST)
        val name = tokens.match(NameToken::class)
        val type = if (tokens.test(":")) readType(tokens) else null
        val value = if (tokens.test("=")) readExpression(tokens) else null
        tokens.test(";")
        return VariableDeclarationNode(name, type, value, mutable)
    }

    private fun readFunctionDefinition(tokens: TokenSource): FunctionDefinitionNode {
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

    private fun readStatement(tokens: TokenSource): StatementNode =
        when (val token = tokens.lookahead()) {
            is NameToken -> readAccessOrCall(tokens)
            RETURN -> readExpression(tokens)
            else -> throw InvalidSyntaxException("syntax error", token)
        }

    private fun readExpression(tokens: TokenSource): ExpressionNode =
        when (val token = tokens.lookahead()) {
            is NumberToken, is StringToken, TRUE, FALSE -> ConstantNode(tokens.next())
            THIS, SUPER -> AccessNode(tokens.next())
            is NameToken -> readAccessOrCall(tokens)
            else -> throw InvalidSyntaxException("syntax error", token)
        }

    private fun readAccessOrCall(tokens: TokenSource): ExpressionNode {
        val name = tokens.match(NameToken::class)
        if (tokens.test("(")) {
            val arguments = tokens.collect(delimiter = ")", separator = ",") { readExpression(it) }
            tokens.test(";")
            return CallNode(name, arguments)
        }
        tokens.test(";")
        return AccessNode(name)
    }
}