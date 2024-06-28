package org.maduralang.parser

import org.maduralang.lexer.*

interface Node

data class FileNode(val definitions: List<DefinitionNode>) : Node

interface DefinitionNode : Node

data class VariableDeclarationNode(
    val name: NameToken,
    val type: NameToken? = null,
    val expression: ExpressionNode? = null,
    val mutable: Boolean = false
) : DefinitionNode, StatementNode {
    constructor(
        name: String,
        type: String? = null,
        expression: ExpressionNode? = null,
        mutable: Boolean = false
    ) : this(NameToken(name), type?.let { NameToken(it) }, expression, mutable)
}

data class FunctionDefinitionNode(
    val name: NameToken,
    val parameters: List<ParameterNode> = emptyList(),
    val type: NameToken? = null,
    val body: List<StatementNode> = emptyList()
) : DefinitionNode {
    constructor(
        name: String,
        parameters: List<ParameterNode> = emptyList(),
        type: String? = null,
        body: List<StatementNode> = emptyList()
    ) : this(NameToken(name), parameters, type?.let { NameToken(it) }, body)
}

data class ClassDefinitionNode(val definitions: List<DefinitionNode> = emptyList()) : DefinitionNode

data class ParameterNode(
    val name: NameToken, val type: NameToken, val defaultValue: ExpressionNode? = null
) : Node {
    constructor(name: String, type: String, defaultValue: ExpressionNode? = null) : this(
        NameToken(name), NameToken(type), defaultValue
    )
}

interface ExpressionNode : StatementNode

data class AccessNode(val name: Token) : ExpressionNode {
    constructor(name: String) : this(NameToken(name))
}

data class CallNode(val name: NameToken, val arguments: List<ExpressionNode> = emptyList()) : ExpressionNode {
    constructor(name: String, arguments: List<ExpressionNode> = emptyList()) : this(NameToken(name), arguments)
}

data class ConstantNode(val value: Token) : ExpressionNode {
    constructor(value: String) : this(StringToken(value))
    constructor(value: Number) : this(NumberToken("$value"))
    constructor(value: Boolean) : this(KeywordToken.valueOf(value.toString().uppercase()))
}

data class BinaryOperatonNode(val left: Node, val operator: SymbolToken, val right: Node) : ExpressionNode

interface StatementNode : Node