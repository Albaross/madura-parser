package org.maduralang.parser

import org.maduralang.lexer.NameToken
import org.maduralang.lexer.NumberToken
import org.maduralang.lexer.StringToken
import org.maduralang.lexer.Token

interface Node

data class FileNode(val definitions: List<Node>) : Node

data class FunctionNode(
    val name: NameToken,
    val parameters: List<ParameterNode> = emptyList(),
    val type: NameToken? = null,
    val body: List<Node> = emptyList()
) : Node {
    constructor(
        name: String,
        parameters: List<ParameterNode> = emptyList(),
        type: String? = null,
        body: List<Node> = emptyList()
    ) : this(NameToken(name), parameters, type?.let { NameToken(it) }, body)
}

data class ParameterNode(val name: NameToken, val type: NameToken) : Node {
    constructor(name: String, type: String) : this(NameToken(name), NameToken(type))
}

data class CallNode(val name: NameToken, val arguments: List<Node> = emptyList()) : Node {
    constructor(name: String, arguments: List<Node> = emptyList()) : this(NameToken(name), arguments)
}

data class ConstantNode(val value: Token) : Node {
    constructor(value: String) : this(StringToken(value))
    constructor(value: Number) : this(NumberToken("$value"))
}