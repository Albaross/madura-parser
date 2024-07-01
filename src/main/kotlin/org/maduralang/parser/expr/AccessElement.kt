package org.maduralang.parser.expr

data class AccessElement(
    val array: Id,
    val index: Expression
) : Expression {

    override fun toString(): String = "$array[$index]"
}