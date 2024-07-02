package org.maduralang.parser.expr

import org.maduralang.lexer.Token

data class ChainCall(val connection: Token, val owner: Expression, val member: Expression) : Expression