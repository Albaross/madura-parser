package org.maduralang.parser.expr

import org.maduralang.lexer.NameToken

data class FunctionCall(val name: NameToken, val arguments: List<Expression> = emptyList()) : Expression {
    constructor(name: String, arguments: List<Expression> = emptyList()) : this(NameToken(name), arguments)
}