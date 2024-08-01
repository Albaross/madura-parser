package org.maduralang.parser.expr

import org.maduralang.lexer.Token
import org.maduralang.lexer.WordToken
import org.maduralang.parser.stmt.Statement

interface Expression : Statement

data class Constant(val value: Token) : Expression {
    override fun toString(): String = value.data
}

data class Id(val id: WordToken) : Expression {
    override fun toString(): String = id.data
}