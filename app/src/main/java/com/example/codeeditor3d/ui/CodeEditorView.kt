package com.example.codeeditor3d.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.codeeditor3d.logic.HighlightTransformation
import com.example.codeeditor3d.model.Language

@Composable
fun CodeEditorView(
    code: String,
    onCodeChange: (String) -> Unit,
    language: Language,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        BasicTextField(
            value = code,
            onValueChange = onCodeChange,
            modifier = Modifier.fillMaxSize(),
            textStyle = TextStyle(
                color = Color(0xFFE6E9F0),
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                lineHeight = 21.sp
            ),
            visualTransformation = HighlightTransformation(language),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Default
            ),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFF4F8BFF)),
            decorationBox = { innerTextField ->
                Box(Modifier.fillMaxSize()) {
                    if (code.isEmpty()) {
                        androidx.compose.material3.Text(
                            "اكتب الكود هنا...",
                            color = Color(0xFF687386),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
