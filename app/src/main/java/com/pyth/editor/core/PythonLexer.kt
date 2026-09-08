package com.pyth.editor.core

import android.graphics.Color

object PythonLexer {
    
    val KEYWORDS = setOf(
        "False", "None", "True", "and", "as", "assert", "async", "await",
        "break", "class", "continue", "def", "del", "elif", "else", "except",
        "finally", "for", "from", "global", "if", "import", "in", "is",
        "lambda", "nonlocal", "not", "or", "pass", "raise", "return", "try",
        "while", "with", "yield", "match", "case"
    )
    
    val BUILTINS = setOf(
        "print", "len", "range", "str", "int", "float", "list", "dict",
        "set", "tuple", "bool", "input", "open", "type", "isinstance",
        "super", "self", "cls"
    )

    enum class TokenType { KEYWORD, BUILTIN, STRING, COMMENT, NUMBER, OPERATOR, DEFAULT }

    data class Token(val start: Int, val end: Int, val type: TokenType) {
        val color: Int
            get() = when (type) {
                TokenType.KEYWORD -> Color.parseColor("#65E6B3")
                TokenType.BUILTIN -> Color.parseColor("#58D68D")
                TokenType.STRING -> Color.parseColor("#FFC857")
                TokenType.COMMENT -> Color.parseColor("#78857F")
                TokenType.NUMBER -> Color.parseColor("#FF5C5C")
                TokenType.OPERATOR -> Color.parseColor("#65E6B3")
                TokenType.DEFAULT -> Color.parseColor("#E8ECEA")
            }
    }

    fun tokenize(text: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        val len = text.length

        while (i < len) {
            val c = text[i]
            
            // Whitespace
            if (c.isWhitespace()) {
                i++
                continue
            }
            
            // Comments
            if (c == '#') {
                val start = i
                while (i < len && text[i] != '\n') i++
                tokens.add(Token(start, i, TokenType.COMMENT))
                continue
            }
            
            // Strings
            if (c == '"' || c == '\'') {
                val start = i
                val initial = c
                i++
                // Handle f-strings or multiline implicitly by just scanning to next matching quote
                while (i < len && text[i] != initial) {
                    if (text[i] == '\\') i++ // Skip escape chars
                    i++
                }
                if (i < len) i++ // Include closing quote
                tokens.add(Token(start, i, TokenType.STRING))
                continue
            }
            
            // Numbers
            if (c.isDigit()) {
                val start = i
                while (i < len && (text[i].isDigit() || text[i] == '.' || text[i] == 'x' || text[i] == 'X' || (text[i] in 'a'..'f') || (text[i] in 'A'..'F'))) {
                    i++
                }
                tokens.add(Token(start, i, TokenType.NUMBER))
                continue
            }
            
            // Identifiers / Keywords / Builtins
            if (c.isLetter() || c == '_') {
                val start = i
                while (i < len && (text[i].isLetterOrDigit() || text[i] == '_')) {
                    i++
                }
                val word = text.substring(start, i)
                val type = when (word) {
                    in KEYWORDS -> TokenType.KEYWORD
                    in BUILTINS -> TokenType.BUILTIN
                    else -> TokenType.DEFAULT
                }
                tokens.add(Token(start, i, type))
                continue
            }
            
            // Operators
            if (c in "+-*/%=<>!&|") {
                val start = i
                i++
                if (i < len && text[i] == '=') i++ // handle ==, <=, >=, !=, +=, etc
                tokens.add(Token(start, i, TokenType.OPERATOR))
                continue
            }
            
            // Default punctuation
            i++
        }
        
        return tokens
    }
}