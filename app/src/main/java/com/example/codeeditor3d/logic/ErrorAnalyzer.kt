package com.example.codeeditor3d.logic

import com.example.codeeditor3d.model.Language

/**
 * Lightweight editor diagnostics. This is a heuristic checker, not a full compiler.
 */
object ErrorAnalyzer {
    fun analyze(code: String, language: Language): List<CodeError> {
        val result = mutableListOf<CodeError>()
        val lines = code.lines()

        if (language.id == "json") {
            try {
                org.json.JSONObject(code)
            } catch (_: Exception) {
                try {
                    org.json.JSONArray(code)
                } catch (e: Exception) {
                    result += CodeError(1, "JSON غير صالح: ${e.message ?: "تحقق من الصياغة"}")
                }
            }
            return result
        }

        var quote: Char? = null
        var quoteLine = 1
        var escaped = false
        val stack = ArrayDeque<Pair<Char, Int>>()
        var inLineComment = false
        var inBlockComment = false

        lines.forEachIndexed { index, line ->
            val lineNumber = index + 1
            inLineComment = false
            var i = 0
            while (i < line.length) {
                val c = line[i]
                val next = line.getOrNull(i + 1) ?: '\u0000'

                if (inLineComment) break
                if (inBlockComment) {
                    if (c == '*' && next == '/') { inBlockComment = false; i += 2; continue }
                    i++; continue
                }
                if (quote != null) {
                    if (escaped) escaped = false
                    else if (c == '\\') escaped = true
                    else if (c == quote) quote = null
                    i++; continue
                }
                if ((c == '/' && next == '/') || (language.id == "py" && c == '#')) {
                    inLineComment = true
                    break
                }
                if (c == '/' && next == '*') { inBlockComment = true; i += 2; continue }
                if (c == '"' || c == '\'' || (c == '`' && language.id in setOf("js", "ts"))) {
                    quote = c
                    quoteLine = lineNumber
                    i++; continue
                }
                when (c) {
                    '(', '[', '{' -> stack.addLast(c to lineNumber)
                    ')', ']', '}' -> {
                        val expected = when (c) { ')' -> '('; ']' -> '['; else -> '{' }
                        if (stack.lastOrNull()?.first == expected) stack.removeLast()
                        else result += CodeError(lineNumber, "قوس إغلاق غير متطابق: $c")
                    }
                }
                i++
            }
            if (quote != null && quote != '`' && language.id !in setOf("py")) {
                result += CodeError(quoteLine, "نص غير مكتمل أو علامة اقتباس غير مغلقة")
                quote = null
                escaped = false
            }
        }
        if (quote != null) result += CodeError(quoteLine, "علامة اقتباس غير مغلقة")
        if (inBlockComment) result += CodeError(lines.size, "تعليق متعدد الأسطر غير مغلق", Severity.WARNING)
        stack.forEach { (opening, line) ->
            result += CodeError(line, "قوس فتح غير مغلق: $opening")
        }

        return result.distinctBy { Triple(it.line, it.message, it.severity) }
    }
}
