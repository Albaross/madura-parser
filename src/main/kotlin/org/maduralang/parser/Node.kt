package org.maduralang.parser

import org.maduralang.lexer.NameToken
import org.maduralang.lexer.Token

interface Node

data class File(val definitions: List<Node>) : Node

data class Function(
    val name: NameToken,
    val parameters: List<Parameter> = emptyList(),
    val type: NameToken? = null,
    val body: List<Node> = emptyList()
) : Node

data class Parameter(val name: NameToken, val type: NameToken) : Node

data class Call(val name: NameToken, val arguments: List<Node>) : Node

data class Constant(val value: Token): Node