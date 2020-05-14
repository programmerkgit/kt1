package org.example


private operator fun Regex.contains(char: Char?): Boolean {
    return if (char == null) {
        false
    } else {
        matches(char.toString())
    }
}

/* Tokenize input file */
class Tokenizer(private val input: String) {

    private fun isWhiteSpace(char: Char?): Boolean {
        return if (char == null) false else Regex("\\s").matches(char.toString())
    }

    private val ch: Char?
        get() = input.getOrNull(p)

    /* pointer to current ch */
    private var p: Int = 0

    /* position after tokenized */
    private var position: Int = 0

    private fun incP(): Int {
        if (p < input.length) {
            p++
        }
        return p
    }

    private fun isEnd(): Boolean {
        return p == input.length
    }

    private fun ensureSetPosition(token: Token): Token {
        position = p
        return token
    }

    fun nextToken(): Token {
        if (isEnd()) return EOFToken()
        while (isWhiteSpace(ch)) {
            incP()
            position = p
        }
        val startPosition = position
        return when (ch) {
            /* math */
            '+' -> {
                incP()
                ensureSetPosition(PlusToken())
            }
            '-' -> {
                incP()
                ensureSetPosition(MinusToken())
            }
            '*' -> {
                incP()
                ensureSetPosition(MultiToken())
            }
            '/' -> {
                incP()
                ensureSetPosition(DivToken())
            }
            /* Compare */
            '>' -> {
                incP()
                return when (ch) {
                    '=' -> {
                        p++
                        ensureSetPosition(GrtEqToken())
                    }
                    else -> {
                        ensureSetPosition(GrtToken())
                    }
                }
            }
            '<' -> {
                incP()
                return when (ch) {
                    '=' -> {
                        incP()
                        position += p
                        LssEqToken()
                    }
                    else -> {
                        position += p
                        LssToken()
                    }
                }
            }
            '=' -> {
                incP()
                when (ch) {
                    '=' -> {
                        incP()
                        position += p
                        return EqualToken()
                    }
                    else -> {
                        return AssignToken()
                    }
                }
            }
            '!' -> {
                incP()
                return when (ch) {
                    '=' -> {
                        incP()
                        position = p
                        NotEqToken()
                    }
                    else -> {
                        BangToken()
                    }
                }
            }
            ',' -> {
                incP()
                position = p
                CommaToken()
            }
            ';' -> {
                incP()
                position = p
                SemicolonToken()
            }
            '(' -> {
                incP()
                position = p
                return LParenToken()
            }
            ')' -> {
                incP()
                return RParentToken()
            }
            '{' -> {
                incP()
                return LBraceToken()
            }
            '}' -> {
                RBraceToken()
            }
            /* number */
            in Regex("[1-9]") -> {
                incP()
                while (ch in Regex("[0-9]")) {
                    incP()
                }
                ensureSetPosition(IntToken(input.slice(startPosition until p)))
            }
            else -> {
                return when {
                    isEnd() -> {
                        EOFToken()
                    }
                    ch in Regex("[a-zA-Z_]") -> {
                        while (ch in Regex("[a-zA-Z_]")) {
                            incP()
                        }
                        when (val text = input.slice(startPosition until p)) {
                            "fn" -> {
                                ensureSetPosition(FuncToken())
                            }
                            "return" -> ensureSetPosition(ReturnToken())
                            "let" -> ensureSetPosition(LetToken())
                            "val" -> ensureSetPosition(ValToken())
                            "true" -> ensureSetPosition(TrueToken())
                            "false" -> ensureSetPosition(FalseToken())
                            "if" -> ensureSetPosition(IfToken())
                            "when" -> ensureSetPosition(WhenToken())
                            "const" -> ensureSetPosition(ConstToken())
                            else -> ensureSetPosition(IdentifierToken(text))
                        }
                    }
                    else -> {
                        incP()
                        ensureSetPosition(IllegalToken(input.slice(startPosition until p)))
                    }
                }
            }
            /* Op */
        }
    }

}
