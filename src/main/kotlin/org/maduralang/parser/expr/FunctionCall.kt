package org.maduralang.parser.expr

import org.maduralang.lexer.NameToken

data class FunctionCall(val name: NameToken, val arguments: List<Expression> = emptyList()) : Expression {
    override fun toString(): String = "${name.data}(${arguments.joinToString(", ")})"
}