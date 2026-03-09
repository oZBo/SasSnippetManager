package com.sassnippet.manager.ui.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/**
 * A utility to format R code with syntax highlighting using AnnotatedString.
 */
object RFormatter {

    private val CommentColor = Color(0xFF008000)   // Green
    private val StringColor = Color(0xFF800080)    // Purple
    private val KeywordColor = Color(0xFF0000FF)   // Blue
    private val BuiltinColor = Color(0xFF000080)   // Navy Blue
    private val OperatorColor = Color(0xFFB8860B)  // DarkGoldenrod
    private val NumberColor = Color(0xFF2E8B57)    // SeaGreen

    fun formatRCode(code: String): AnnotatedString {
        return buildAnnotatedString {
            val patterns = listOf(
                // Line comments: # to end of line
                Regex("""#[^\n]*"""),
                // Backtick-quoted identifiers and strings
                Regex("""`[^`]*`"""),
                // Single and double quoted strings
                Regex("""'[^']*'|"[^"]*""""),
                // Special values (must come before keywords to avoid partial matches)
                Regex("""\b(TRUE|FALSE|T|F|NULL|NA|NA_integer_|NA_real_|NA_complex_|NA_character_|Inf|NaN)\b"""),
                // Keywords
                Regex("""\b(function|if|else|for|while|repeat|return|next|break|in)\b"""),
                // Common built-in functions
                Regex("""\b(library|require|source|print|cat|message|warning|stop|paste|paste0|sprintf|format|formatC|c|list|data\.frame|tibble|matrix|array|vector|numeric|integer|character|logical|complex|factor|ordered|str|summary|head|tail|nrow|ncol|dim|length|names|colnames|rownames|class|typeof|is\.numeric|is\.character|is\.logical|is\.null|is\.na|is\.finite|is\.infinite|is\.nan|as\.numeric|as\.integer|as\.character|as\.logical|as\.factor|as\.data\.frame|setwd|getwd|read\.csv|write\.csv|read\.table|write\.table|readRDS|saveRDS|load|save|rm|ls|exists|Sys\.time|Sys\.sleep|proc\.time|system\.time|apply|lapply|sapply|vapply|tapply|mapply|Map|Reduce|Filter|Find|Position|do\.call|which|any|all|sum|prod|mean|median|var|sd|min|max|range|abs|sqrt|exp|log|log2|log10|ceiling|floor|round|trunc|sign|cumsum|cumprod|cummax|cummin|diff|rev|sort|order|unique|duplicated|table|prop\.table|seq|seq_len|seq_along|rep|append|match|which\.min|which\.max|nchar|substr|substring|toupper|tolower|trimws|gsub|sub|grep|grepl|regexpr|regmatches|strsplit|startsWith|endsWith|Reduce|identity)\b"""),
                // Pipe and special operators
                Regex("""%>%|%\|>%|%in%|%\*%|%%|%/%|%o%|%x%"""),
                // Assignment operators
                Regex("""<<-|<-|->|->>|:="""),
                // Numbers: integer (1L), hex (0x1F), complex (1i), doubles
                Regex("""\b0x[0-9a-fA-F]+\b|\b\d+(\.\d+)?([eE][+-]?\d+)?[iL]?\b""")
            )

            val combinedPattern = Regex(patterns.joinToString("|") { it.pattern })

            var lastIndex = 0
            combinedPattern.findAll(code).forEach { result ->
                append(code.substring(lastIndex, result.range.first))

                val match = result.value
                val style = when {
                    match.startsWith("#") -> SpanStyle(color = CommentColor)
                    match.startsWith("`") || match.startsWith("'") || match.startsWith("\"") ->
                        SpanStyle(color = StringColor)
                    match in setOf("TRUE", "FALSE", "T", "F", "NULL", "NA", "NA_integer_",
                        "NA_real_", "NA_complex_", "NA_character_", "Inf", "NaN") ->
                        SpanStyle(color = KeywordColor, fontWeight = FontWeight.Bold)
                    match in setOf("function", "if", "else", "for", "while", "repeat",
                        "return", "next", "break", "in") ->
                        SpanStyle(color = KeywordColor, fontWeight = FontWeight.Bold)
                    match.startsWith("%") -> SpanStyle(color = OperatorColor, fontWeight = FontWeight.Bold)
                    match == "<-" || match == "->" || match == "<<-" || match == "->>" || match == ":=" ->
                        SpanStyle(color = OperatorColor)
                    match.first().isDigit() || match.startsWith("0x") -> SpanStyle(color = NumberColor)
                    else -> SpanStyle(color = BuiltinColor, fontWeight = FontWeight.Bold)
                }

                withStyle(style) { append(match) }
                lastIndex = result.range.last + 1
            }

            if (lastIndex < code.length) {
                append(code.substring(lastIndex))
            }
        }
    }
}
