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
) : DefinitionNode, Statement {
    constructor(
        name: String,
        type: String? = null,
        expression: Expression? = null,
        mutable: Boolean = false
    ) : this(NameToken(name), type?.let { NameToken(it) }, expression, mutable)
}

data class FunctionDefinitionNode(
    val name: NameToken,
    val parameters: List<ParameterNode> = emptyList(),
    val type: NameToken? = null,
    val body: List<Statement> = emptyList()
) : DefinitionNode {
    constructor(
        name: String,
        parameters: List<ParameterNode> = emptyList(),
        type: String? = null,
        body: List<Statement> = emptyList()
    ) : this(NameToken(name), parameters, type?.let { NameToken(it) }, body)
}

data class ParameterNode(
    val name: NameToken, val type: NameToken, val defaultValue: Expression? = null
) : Node