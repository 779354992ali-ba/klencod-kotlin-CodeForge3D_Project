package com.example.codeeditor3d.logic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.example.codeeditor3d.model.Language

object SyntaxHighlighter {
    private val keywordColor = Color(0xFFC586F0)
    private val builtinColor = Color(0xFF6BB6FF)
    private val stringColor = Color(0xFF9CE37D)
    private val numberColor = Color(0xFFF7B955)
    private val commentColor = Color(0xFF7A8296)

    fun highlight(code: String, lang: Language): AnnotatedString = buildAnnotatedString {
        append(code)
        var i = 0
        while (i < code.length) {
            val c = code[i]
            val next = code.getOrNull(i + 1) ?: '\u0000'
            if ((c == '/' && next == '/') || (lang.id == "py" && c == '#')) {
                val end = code.indexOf('\n', i).let { if (it < 0) code.length else it }
                addStyle(SpanStyle(color = commentColor, fontStyle = FontStyle.Italic), i, end)
                i = end
                continue
            }
            if (c == '/' && next == '*') {
                val found = code.indexOf("*/", i + 2)
                val end = if (found < 0) code.length else found + 2
                addStyle(SpanStyle(color = commentColor, fontStyle = FontStyle.Italic), i, end)
                i = end
                continue
            }
            if (c == '"' || c == '\'' || c == '`') {
                val quote = c
                var j = i + 1
                while (j < code.length) {
                    if (code[j] == '\\') { j += 2; continue }
                    if (code[j] == quote || code[j] == '\n') { j++; break }
                    j++
                }
                val end = j.coerceAtMost(code.length)
                addStyle(SpanStyle(color = stringColor), i, end)
                i = end
                continue
            }
            if (c.isDigit()) {
                var j = i + 1
                while (j < code.length && (code[j].isLetterOrDigit() || code[j] == '.' || code[j] == '_')) j++
                addStyle(SpanStyle(color = numberColor), i, j)
                i = j
                continue
            }
            if (c.isLetter() || c == '_' || c == '$') {
                var j = i + 1
                while (j < code.length && (code[j].isLetterOrDigit() || code[j] == '_' || code[j] == '$')) j++
                val word = code.substring(i, j)
                when {
                    word in lang.keywords -> addStyle(SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold), i, j)
                    word in lang.builtins -> addStyle(SpanStyle(color = builtinColor), i, j)
                }
                i = j
                continue
            }
            i++
        }
    }
}

class HighlightTransformation(private val lang: Language) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText =
        TransformedText(SyntaxHighlighter.highlight(text.text, lang), OffsetMapping.Identity)
}
