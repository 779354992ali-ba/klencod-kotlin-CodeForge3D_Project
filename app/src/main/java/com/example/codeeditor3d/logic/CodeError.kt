package com.example.codeeditor3d.logic

enum class Severity { ERROR, WARNING, INFO }

data class CodeError(
    val line: Int,
    val message: String,
    val severity: Severity = Severity.ERROR
)
