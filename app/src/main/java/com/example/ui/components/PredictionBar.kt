package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.layout.KeyboardLayoutType
import com.example.core.theme.NovaTheme

@Composable
fun PredictionBar(
    currentWord: String,
    layoutType: KeyboardLayoutType,
    onSuggestionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = NovaTheme.colors

    val suggestions = when {
        currentWord.isNotBlank() -> {
            generateCandidates(currentWord, layoutType)
        }
        layoutType == KeyboardLayoutType.ARABIC -> {
            listOf("السلام عليكم", "شكراً", "إن شاء الله", "تمام", "مرحباً", "بالتأكيد")
        }
        else -> {
            listOf("the", "and", "you", "hello", "thanks", "awesome")
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(colors.predictionBarBackground)
            .padding(horizontal = 6.dp, vertical = 3.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        suggestions.forEachIndexed { index, suggestion ->
            val isPrimary = index == 0
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isPrimary) colors.keyAccentBackground.copy(alpha = 0.18f) else colors.keyBackground)
                    .border(
                        width = if (isPrimary) 1.dp else 0.5.dp,
                        color = if (isPrimary) colors.accentPrimary else colors.keyBorder,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { onSuggestionSelected(suggestion) }
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = suggestion,
                    color = if (isPrimary) colors.accentPrimary else colors.keyText,
                    fontSize = 13.sp,
                    fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

private fun generateCandidates(word: String, layoutType: KeyboardLayoutType): List<String> {
    val lower = word.lowercase()
    return if (layoutType == KeyboardLayoutType.ARABIC) {
        val arabicDict = listOf(
            "الله", "السلام", "عليكم", "شكراً", "صباح", "مساء", "الخير", "اليوم", "غداً",
            "نعم", "كلا", "أهلاً", "كيفك", "تمام", "أنا", "أنت", "في", "على", "من", "إلى"
        )
        val matched = arabicDict.filter { it.startsWith(word) }
        (listOf(word) + matched + listOf("$word؟", "$word!")).distinct().take(5)
    } else {
        val engDict = listOf(
            "the", "there", "their", "they're", "this", "that", "these", "those",
            "have", "having", "how", "hello", "happy", "great", "good", "going",
            "what", "when", "where", "why", "who", "will", "would", "with", "work"
        )
        val matched = engDict.filter { it.startsWith(lower) }
        (listOf(word) + matched + listOf("$word!", "$word?")).distinct().take(5)
    }
}
