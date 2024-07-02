package org.maduralang.parser.expr

import org.maduralang.lexer.Token

data class Arithmetic(
    val op: Token,
    val expr1: Expression,
    val expr2: Expression
) : Expression {

    override fun toString(): String = "$expr1 ${op.data} $expr2"
}