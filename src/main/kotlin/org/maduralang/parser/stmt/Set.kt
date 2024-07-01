package org.maduralang.parser.stmt

import org.maduralang.parser.expr.AccessElement
import org.maduralang.parser.expr.Expression
import org.maduralang.parser.expr.Id

data class Set(val id: Id, val expr: Expression) : Statement {

    override fun toString(): String = "$id = $expr"
}

data class SetElemement(val access: AccessElement, val expr: Expression) : Statement {

    val array: Id get() = access.array
    val index: Expression get() = access.index

    override fun toString(): String = "$array[$index] = $expr"
}