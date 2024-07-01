package org.maduralang.parser.expr

import org.maduralang.lexer.*
import org.maduralang.parser.stmt.Statement

interface Expression : Statement

data class Constant(val value: Token) : Expression {
    constructor(value: String) : this(StringToken(value))
    constructor(value: Number) : this(NumberToken(value))
    constructor(value: Boolean) : this(KeywordToken.valueOf(value.toString().uppercase()))

    override fun toString(): String = value.data
}

data class Id(val id: WordToken) : Expression {
    constructor(id: String) : this(NameToken(id))

    override fun toString(): String = id.data
}