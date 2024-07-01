package org.maduralang.parser.stmt

import org.maduralang.parser.expr.Expression

data class While(val condition: Expression, val stmt: Statement) : Statement {

    override fun toString(): String = "while ($condition) { $stmt }"
}

data class Do(val stmt: Statement, val condition: Expression) : Statement {

    override fun toString(): String = "do { $stmt } while ($condition)"
}

object Break : Statement {
    override fun toString(): String = "break"
}