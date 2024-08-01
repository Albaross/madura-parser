package org.maduralang.parser

import org.maduralang.lexer.NameToken
import org.maduralang.parser.expr.Expression
import org.maduralang.parser.stmt.Statement

interface Node

data class FileNode(val definitions: List<DefinitionNode>) : Node

interface DefinitionNode : Node

data class VariableDeclarationNode(
    val name: NameToken,
    val type: NameToken? = null,
    val expression: Expression? = null,
    val mutable: Boolean = false
) : DefinitionNode, Statement

data class FunctionDefinitionNode(
    val name: NameToken,
    val parameters: List<ParameterNode> = emptyList(),
    val type: NameToken? = null,
    val body: Body? = null
) : DefinitionNode

data class ParameterNode(val name: NameToken, val type: NameToken, val defaultValue: Expression? = null) : Node

interface Body: Node

data class SingleExpressionNode(val expression: Expression) : Body {
    override fun toString(): String = "$expression"
}

data class MultiStatementNode(val statements: List<Statement> = emptyList()) : Body{
    override fun toString(): String = "$statements"
}