package com.sassnippet.manager.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/**
 * A utility to format SAS code with syntax highlighting using AnnotatedString.
 */
object SasFormatter {

    // SAS Colors (Inspired by standard SAS Enhanced Editor)
    private val KeywordColor = Color(0xFF0000FF) // Blue
    private val CommentColor = Color(0xFF008000) // Green
    private val StringColor = Color(0xFF800080)  // Purple
    private val NumberColor = Color(0xFF2E8B57)  // SeaGreen
    private val ProcedureColor = Color(0xFF000080) // Navy Blue
    private val MacroColor = Color(0xFF0000FF)   // Blue for %macros

    fun formatSasCode(code: String): AnnotatedString {
        return buildAnnotatedString {
            val patterns = listOf(
                // Block Comments: /* ... */
                Regex("""/\*[\s\S]*?\*/"""),
                // Statement Comments: * ... ; (Simplified)
                Regex("""(?m)^\s*\*.*;"""),
                // Strings: '...' or "..."
                Regex("""'[^']*'|"[^"]*""""),
                // Macro variables and functions: &var, %macro
                Regex("""%[a-zA-Z_]\w*|&[a-zA-Z_]\w*\.?"""),
                // Keywords (Case Insensitive)
                Regex("""(?i)\b(DATA|RUN|QUIT|CARDS|DATALINES|INFILE|INPUT|SET|MERGE|BY|WHERE|KEEP|DROP|RENAME|LABEL|ATTRIB|FORMAT|INFORMAT|IF|THEN|ELSE|DO|END|OUTPUT|STOP|ABORT)\b"""),
                // Procedures
                Regex("""(?i)\b(PROC\s+[a-zA-Z_]\w*)"""),
                // Numbers
                Regex("""\b\d+(\.\d+)?([eE][+-]?\d+)?\b""")
            )

            // Join all patterns with OR
            val combinedPattern = Regex(patterns.joinToString("|") { it.pattern })

            var lastIndex = 0
            combinedPattern.findAll(code).forEach { result ->
                // Add text before the match
                append(code.substring(lastIndex, result.range.first))

                val match = result.value
                val style = when {
                    match.startsWith("/*") || match.startsWith("*") -> SpanStyle(color = CommentColor)
                    match.startsWith("'") || match.startsWith("\"") -> SpanStyle(color = StringColor)
                    match.startsWith("%") || match.startsWith("&") -> SpanStyle(color = MacroColor, fontWeight = FontWeight.Bold)
                    match.uppercase().startsWith("PROC") -> SpanStyle(color = ProcedureColor, fontWeight = FontWeight.Bold)
                    match.uppercase() in listOf("DATA", "RUN", "QUIT", "SET", "MERGE", "BY", "WHERE") ->
                        SpanStyle(color = KeywordColor, fontWeight = FontWeight.Bold)
                    match.first().isDigit() -> SpanStyle(color = NumberColor)
                    else -> SpanStyle(color = KeywordColor)
                }

                withStyle(style) {
                    append(match)
                }
                lastIndex = result.range.last + 1
            }
            // Add remaining text
            if (lastIndex < code.length) {
                append(code.substring(lastIndex))
            }
        }
    }
}