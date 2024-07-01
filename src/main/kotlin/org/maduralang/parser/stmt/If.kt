package org.maduralang.parser.stmt

import org.maduralang.parser.expr.Expression

data class If(val condition: Expression, val then: Statement) : Statement {

    override fun toString(): String = "if ($condition) { $then }"
}

data class Else(val condition: Expression, val then: Statement, val elseStmt: Statement) : Statement {

    override fun toString(): String = "if ($condition) { $then } else { $elseStmt }"
}