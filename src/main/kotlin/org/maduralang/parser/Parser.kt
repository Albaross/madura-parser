package org.maduralang.parser

import org.maduralang.lexer.*
import org.maduralang.lexer.KeywordToken.*
import org.maduralang.parser.expr.*
import org.maduralang.parser.stmt.Return
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
            PUBLIC, PRIVATE, PROTECTED, SHARED -> TODO("Access modifier not yet implemented: $token")
            LET, VAR, CONST -> readVariableDeclaration(tokens)
            FN, FUN, FUNC -> readFunctionDefinition(tokens)
            CLASS, ENUM -> TODO("Class definition not yet implemented: $token")
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
            "{" -> MultiStatementNode(tokens.collect(delimiter = "}") { readStatement(it) })
            "=>", "->" -> SingleExpressionNode(readExpression(tokens))
            else -> null
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
            is NameToken, is NumberToken, is StringToken, TRUE, FALSE, THIS, SUPER -> readExpression(tokens)
            IF, ELSE, MATCH, FOR, WHILE, DO, CONTINUE, BREAK -> TODO("Statement not yet implemented: $token")
            RETURN -> {
                tokens.next()
                Return(readExpression(tokens))
            }

            else -> throw InvalidSyntaxException("syntax error", token)
        }

    private fun readExpression(tokens: TokenSource): Expression = handleElvis(tokens)

    private fun handleElvis(tokens: TokenSource):Expression =
        binary(tokens, ::ChainCall, setOf("?:"), ::handleLogicalOr)

    private fun handleLogicalOr(tokens: TokenSource): Expression =
        binary(tokens, ::Or, setOf("||", "|"), ::handleLogicalAnd)

    private fun handleLogicalAnd(tokens: TokenSource): Expression =
        binary(tokens, ::And, setOf("&&", "&"), ::handleEquality)

    private fun handleEquality(tokens: TokenSource): Expression =
        binary(tokens, ::Relation, setOf("==", "!=", "===", "!=="), ::handleComparison)

    private fun handleComparison(tokens: TokenSource): Expression =
        binary(tokens, ::Relation, setOf("<", ">", "<=", ">=", "in", "is", "as"), ::handleShift)

    private fun handleShift(tokens: TokenSource): Expression =
        binary(tokens, ::Arithmetic, setOf("<<", ">>", ">>>"), ::handleAddition)

    private fun handleAddition(tokens: TokenSource): Expression =
        binary(tokens, ::Arithmetic, setOf("+", "-"), ::handleMultiplication)

    private fun handleMultiplication(tokens: TokenSource): Expression =
        binary(tokens, ::Arithmetic, setOf("*", "/", "%"), ::handlePower)

    private fun handlePower(tokens: TokenSource): Expression =
        binary(tokens, ::Arithmetic, setOf("^"), ::handleChainCall)

    private fun handleChainCall(tokens: TokenSource): Expression =
        binary(tokens, ::ChainCall, setOf(".", "?.", "::"), ::handleParentheses)

    private fun handleParentheses(tokens: TokenSource): Expression {
        if (tokens.test("(")) {
            val result = readExpression(tokens)
            tokens.match(")")
            return result
        }

        return readElement(tokens)
    }

    private fun readElement(tokens: TokenSource) = when (val token = tokens.lookahead()) {
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

    private inline fun binary(
        tokens: TokenSource,
        operation: (Token, Expression, Expression) -> Expression,
        symbols: Set<String>,
        element: (TokenSource) -> Expression
    ): Expression {

        var expr = element(tokens)
        while (tokens.test(consume = false) { it.data in symbols })
            expr = operation(tokens.next(), expr, element(tokens))

        return expr
    }
}