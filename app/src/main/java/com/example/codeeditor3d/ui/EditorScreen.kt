package com.example.codeeditor3d.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeeditor3d.logic.CodeError
import com.example.codeeditor3d.logic.ErrorAnalyzer
import com.example.codeeditor3d.logic.Severity
import com.example.codeeditor3d.model.Language
import com.example.codeeditor3d.model.Languages

private val SampleCode = """
// Welcome to CodeForge 3D
function greet(name) {
  console.log("Hello, " + name + "!");
  return { ok: true, at: Date.now() };
}
greet("Android");
""".trimIndent()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen() {
    var code by remember { mutableStateOf(SampleCode) }
    var language by remember { mutableStateOf(Languages.JS) }
    var tab by remember { mutableIntStateOf(0) }
    var errors by remember { mutableStateOf<List<CodeError>>(emptyList()) }
    var langMenu by remember { mutableStateOf(false) }

    LaunchedEffect(code, language) {
        errors = ErrorAnalyzer.analyze(code, language)
    }

    AppBackground {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            GlassPanel(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                tiltX = 1.5f,
                glow = language.accent
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(42.dp).clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(language.accent, Color(0xFF8B5CF6)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Code, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("CodeForge 3D", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("محرر • محلل • معاينة", color = Color(0xFF8892A6), fontSize = 12.sp)
                    }
                    Box {
                        AssistChip(
                            onClick = { langMenu = true },
                            label = { Text(language.display, color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Language, null, tint = language.accent) }
                        )
                        DropdownMenu(expanded = langMenu, onDismissRequest = { langMenu = false }) {
                            Languages.all.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.display) },
                                    onClick = { language = item; langMenu = false }
                                )
                            }
                        }
                    }
                }
            }

            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                TabPill("المحرر", Icons.Default.Edit, tab == 0, language.accent) { tab = 0 }
                Spacer(Modifier.width(8.dp))
                TabPill("المعاينة", Icons.Default.PlayArrow, tab == 1, language.accent) { tab = 1 }
                Spacer(Modifier.width(8.dp))
                TabPill(
                    "الأخطاء ${if (errors.isEmpty()) "" else "(${errors.size})"}",
                    Icons.Default.Warning, tab == 2,
                    if (errors.isEmpty()) Color(0xFF4CAF50) else Color(0xFFFF5A6D)
                ) { tab = 2 }
            }

            Spacer(Modifier.height(10.dp))

            GlassPanel(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                tiltX = 1.5f,
                glow = language.accent
            ) {
                Crossfade(targetState = tab, label = "content") { selected ->
                    when (selected) {
                        0 -> CodeEditorView(code, { code = it }, language, Modifier.fillMaxSize())
                        1 -> PreviewWebView(
                            code = code,
                            language = language,
                            modifier = Modifier.fillMaxSize(),
                            onRuntimeError = { err ->
                                if (errors.none { it.message == err.message }) errors = errors + err
                            }
                        )
                        else -> ProblemsPanel(errors)
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${code.lines().size} سطر  •  ${code.length} حرف", color = Color(0xFF8892A6), fontSize = 12.sp)
                Row {
                    FilledTonalButton(onClick = { errors = ErrorAnalyzer.analyze(code, language) }) {
                        Icon(Icons.Default.Search, null)
                        Spacer(Modifier.width(6.dp))
                        Text("تحليل")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { tab = 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = language.accent)
                    ) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(Modifier.width(6.dp))
                        Text("تشغيل")
                    }
                }
            }
        }
    }
}

@Composable
private fun TabPill(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    Box(
        Modifier.clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) Brush.linearGradient(listOf(accent, Color(0xFF8B5CF6)))
                else Brush.linearGradient(listOf(Color(0xFF1A1F2E), Color(0xFF141824)))
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (selected) Color.White else Color(0xFF8892A6), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(label, fontSize = 12.sp, color = if (selected) Color.White else Color(0xFFB8C0D0),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
private fun ProblemsPanel(errors: List<CodeError>) {
    if (errors.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(56.dp))
                Spacer(Modifier.height(8.dp))
                Text("لا توجد أخطاء ✓", color = Color(0xFF8892A6))
            }
        }
    } else {
        LazyColumn(Modifier.fillMaxSize()) {
            items(errors) { error ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 4.dp)
                        .clip(RoundedCornerShape(10.dp)).background(Color(0xFF161B27)).padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (icon, tint) = when (error.severity) {
                        Severity.ERROR -> Icons.Default.Error to Color(0xFFFF5A6D)
                        Severity.WARNING -> Icons.Default.Warning to Color(0xFFF7B955)
                        Severity.INFO -> Icons.Default.Info to Color(0xFF6BB6FF)
                    }
                    Icon(icon, null, tint = tint)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(error.message, fontSize = 13.sp, color = Color(0xFFE6E9F0))
                        Text("السطر ${error.line}", fontSize = 11.sp, color = Color(0xFF8892A6))
                    }
                }
            }
        }
    }
}
