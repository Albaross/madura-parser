package org.maduralang.parser.stmt

import org.maduralang.parser.Node

interface Statement : Node

object Null : Statement {
    override fun toString(): String = "null"
}