package seven.collector.aitarotreadingapp.theme.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import java.util.regex.Pattern

@Composable
fun FormattedText(text: String, style: TextStyle, textAlign: TextAlign? = null) {
    val boldPattern = Pattern.compile("\\*\\*(.*?)\\*\\*")
    val italicPattern = Pattern.compile("_(.*?)_")
    val bulletPattern = Regex("^\\* (.*)")

    val annotatedString = buildAnnotatedString {
        val lines = text.split("\n")
        for (line in lines) {
            val bulletMatch = bulletPattern.find(line)
            if (bulletMatch != null) {
                append("•")
                appendFormattedText(bulletMatch.groupValues[1], boldPattern, italicPattern)
            } else {
                appendFormattedText(line, boldPattern, italicPattern)
            }
            append("\n")
        }
    }

    Text(text = annotatedString, style = style, textAlign = textAlign)
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.appendFormattedText(
    text: String,
    boldPattern: Pattern,
    italicPattern: Pattern
) {
    var currentText = text
    var lastIndex = 0

    val matcherBold = boldPattern.matcher(currentText)
    val matcherItalic = italicPattern.matcher(currentText)

    val matches = mutableListOf<Pair<Int, Int>>()

    while (matcherBold.find()) {
        matches.add(Pair(matcherBold.start(), matcherBold.end()))
    }

    while (matcherItalic.find()) {
        matches.add(Pair(matcherItalic.start(), matcherItalic.end()))
    }

    matches.sortBy { it.first }

    lastIndex = 0

    for ((start, end) in matches) {
        append(currentText.substring(lastIndex, start))

        val matchText = currentText.substring(start, end)

        when {
            matchText.startsWith("**") && matchText.endsWith("**") -> {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(matchText.removeSurrounding("**"))
                }
            }
            matchText.startsWith("_") && matchText.endsWith("_") -> {
                withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(matchText.removeSurrounding("_"))
                }
            }
            else -> append(matchText)
        }

        lastIndex = end
    }

    append(currentText.substring(lastIndex))
}