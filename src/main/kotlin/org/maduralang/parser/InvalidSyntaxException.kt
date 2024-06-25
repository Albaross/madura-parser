package org.maduralang.parser

import org.maduralang.lexer.Token

class InvalidSyntaxException(message: String, token: Token? = null) : RuntimeException("$message: $token")