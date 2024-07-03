package org.maduralang.parser.stmt

import org.maduralang.parser.Node
import org.maduralang.parser.expr.Expression

interface Statement : Node

data class Return(val value: Expression) : Statement {

    override fun toString(): String = "return $value"
}

object Null : Statement {
    override fun toString(): String = "null"
}