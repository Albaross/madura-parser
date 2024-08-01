package org.maduralang.parser

import org.maduralang.lexer.KeywordToken
import org.maduralang.lexer.NameToken
import org.maduralang.lexer.NumberToken
import org.maduralang.lexer.StringToken
import org.maduralang.parser.expr.Constant
import org.maduralang.parser.expr.Expression
import org.maduralang.parser.expr.FunctionCall
import org.maduralang.parser.expr.Id
import org.maduralang.parser.stmt.Statement

fun constant(value: String) = Constant(StringToken(value))

fun constant(value: Number) = Constant(NumberToken(value))

fun constant(value: Boolean) = Constant(KeywordToken.valueOf(value.toString().uppercase()))

fun id(id: String) = Id(NameToken(id))

fun functionCall(name: String, arguments: List<Expression> = emptyList()) = FunctionCall(NameToken(name), arguments)

fun variableDeclaration(
    name: String,
    type: String? = null,
    expression: Expression? = null,
    mutable: Boolean = false
) = VariableDeclarationNode(NameToken(name), type?.let { NameToken(it) }, expression, mutable)

fun functionDefinition(
    name: String,
    parameters: List<ParameterNode> = emptyList(),
    type: String? = null,
    body: List<Statement> = emptyList()
) = FunctionDefinitionNode(NameToken(name), parameters, type?.let { NameToken(it) }, MultiStatementNode(body))

fun functionDefinition(
    name: String,
    parameters: List<ParameterNode> = emptyList(),
    type: String? = null,
    body: Expression
) = FunctionDefinitionNode(NameToken(name), parameters, type?.let { NameToken(it) }, SingleExpressionNode(body))