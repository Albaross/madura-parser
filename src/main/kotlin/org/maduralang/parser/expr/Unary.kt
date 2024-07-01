package org.maduralang.parser.expr

import org.maduralang.lexer.Token

data class Unary(val op: Token, val expr: Expression) : Expression { // handles minus, for ! see Not

    override fun toString(): String = "$op $expr"
}