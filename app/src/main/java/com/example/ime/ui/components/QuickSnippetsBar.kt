package com.example.ime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper

@Composable
fun QuickSnippetsBar(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    hapticEnabled: Boolean,
    onInsertText: (String) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current

    val quickItems = listOf(
        "﷽", "ﷺ", "ﷻ", "السلام عليكم ورحمة الله وبركاته",
        "شكراً جزيلاً لك", "إن شاء الله", "ما شاء الله",
        "الحمد لله رب العالمين", "جزاك الله خيراً", "بارك الله فيك",
        "أهلاً وسهلاً", "صباح الخير والسرور", "مساء الورد والياسمين"
    )

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(colorScheme.background)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(quickItems) { item ->
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground)
                    .clickable {
                        if (hapticEnabled) HapticHelper.performKeyHaptic(context, view)
                        onInsertText(item)
                    }
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    color = colorScheme.keyText,
                    fontSize = if (item.length > 2) 13.sp else 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
