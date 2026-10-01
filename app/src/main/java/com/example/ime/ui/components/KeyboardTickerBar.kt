package com.example.ime.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme
import kotlinx.coroutines.delay

@Composable
fun KeyboardTickerBar(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onOpenMenu: () -> Unit,
    onHeadlineClick: (String) -> Unit = {}
) {
    val headlines = listOf(
        "⚡ مرحباً بك في Nova Keyboard - كيبورد محمد v.1 الأسرع في العالم!",
        "🔥 محرك الترجمة الفورية يدعم أكثر من 40 لغة والترجمة عند ضغط Enter.",
        "✨ محرك الزخرفة يوفر زخارف إسلامية ونصوص وكشيدة ومد كامل.",
        "🔒 الحافظة المشفرة تقوم بحذف البيانات الحساسة تلقائياً بعد 15 دقيقة.",
        "💎 مساعد Gemini AI يقدم صياغة بـ 7 نبرات وتدقيق إملائي ونحوي دقيق."
    )

    var currentHeadlineIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentHeadlineIndex = (currentHeadlineIndex + 1) % headlines.size
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(colorScheme.background.copy(alpha = 0.95f))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val currentHeadline = headlines[currentHeadlineIndex]
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onHeadlineClick(currentHeadline) }
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Newspaper,
                    contentDescription = null,
                    tint = colorScheme.accent,
                    modifier = Modifier.size(15.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clipToBounds(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    AnimatedContent(
                        targetState = currentHeadline,
                        transitionSpec = {
                            (slideInVertically { height -> height } + fadeIn()) togetherWith
                                    (slideOutVertically { height -> -height } + fadeOut())
                        },
                        label = "headline_anim",
                        modifier = Modifier.fillMaxWidth().clipToBounds()
                    ) { text ->
                        Text(
                            text = text,
                            color = colorScheme.keyText.copy(alpha = 0.85f),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorScheme.keyBackground.copy(alpha = 0.8f))
                    .clickable { onOpenMenu() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = "القائمة",
                    tint = colorScheme.keyText,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}
