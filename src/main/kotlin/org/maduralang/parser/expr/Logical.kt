package org.maduralang.parser.expr

import org.maduralang.lexer.Token

interface Logical : Expression {
    val op: Token
    val expr1: Expression
    val expr2: Expression
}

data class And(
    override val op: Token,
    override val expr1: Expression,
    override val expr2: Expression
) : Logical {

    override fun toString(): String = "$expr1 ${op.data} $expr2"
}

data class Or(
    override val op: Token,
    override val expr1: Expression,
    override val expr2: Expression
) : Logical {

    override fun toString(): String = "$expr1 ${op.data} $expr2"
}

data class Not(override val op: Token, val expr: Expression) : Logical {

    override val expr1: Expression get() = expr
    override val expr2: Expression get() = expr

    override fun toString(): String = "${op.data} $expr"
}

data class Relation(
    override val op: Token,
    override val expr1: Expression,
    override val expr2: Expression
) : Logical {

    override fun toString(): String = "$expr1 ${op.data} $expr2"
}