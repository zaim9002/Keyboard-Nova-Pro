package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper

@Composable
fun ResizePanel(
    modifier: Modifier = Modifier,
    currentHeight: String,
    heightPercent: Int = 100,
    widthPercent: Int = 100,
    keyFontSizeSp: Int = 19,
    secondaryFontSizeSp: Int = 9,
    colorScheme: KeyboardColorScheme,
    onSelectHeight: (String) -> Unit,
    onChangeHeightPercent: (Int) -> Unit = {},
    onChangeWidthPercent: (Int) -> Unit = {},
    onChangeKeyFontSize: (Int) -> Unit = {},
    onChangeSecondaryFontSize: (Int) -> Unit = {},
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    var selectedTab by remember { mutableStateOf(0) } // 0 = الأبعاد, 1 = الخطوط

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AspectRatio,
                    contentDescription = "تغيير الحجم",
                    tint = colorScheme.accent,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "تعديل أبعاد وارتفاع اللوحة",
                    color = colorScheme.keyText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(
                onClick = {
                    HapticHelper.performKeyHaptic(context, view)
                    onClose()
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = colorScheme.keyText,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TabButton(
                title = "أبعاد الكيبورد",
                isSelected = selectedTab == 0,
                colorScheme = colorScheme,
                modifier = Modifier.weight(1f)
            ) {
                selectedTab = 0
            }
            TabButton(
                title = "حجم الخطوط",
                isSelected = selectedTab == 1,
                colorScheme = colorScheme,
                modifier = Modifier.weight(1f)
            ) {
                selectedTab = 1
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedTab == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "نسبة الارتفاع: $heightPercent%",
                        color = colorScheme.keyText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        MiniStepButton("-5", colorScheme) {
                            onChangeHeightPercent((heightPercent - 5).coerceIn(70, 140))
                        }
                        MiniStepButton("افتراضي", colorScheme) {
                            onChangeHeightPercent(100)
                            onSelectHeight("Medium")
                        }
                        MiniStepButton("+5", colorScheme) {
                            onChangeHeightPercent((heightPercent + 5).coerceIn(70, 140))
                        }
                    }
                }
                Slider(
                    value = heightPercent.toFloat(),
                    onValueChange = { onChangeHeightPercent(it.toInt()) },
                    valueRange = 70f..140f,
                    colors = SliderDefaults.colors(
                        thumbColor = colorScheme.accent,
                        activeTrackColor = colorScheme.accent,
                        inactiveTrackColor = colorScheme.keyBackground
                    ),
                    modifier = Modifier.fillMaxWidth().height(26.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "نسبة العرض: $widthPercent%",
                        color = colorScheme.keyText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        MiniStepButton("75%", colorScheme) { onChangeWidthPercent(75) }
                        MiniStepButton("85%", colorScheme) { onChangeWidthPercent(85) }
                        MiniStepButton("100%", colorScheme) { onChangeWidthPercent(100) }
                    }
                }
                Slider(
                    value = widthPercent.toFloat(),
                    onValueChange = { onChangeWidthPercent(it.toInt()) },
                    valueRange = 70f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = colorScheme.accent,
                        activeTrackColor = colorScheme.accent,
                        inactiveTrackColor = colorScheme.keyBackground
                    ),
                    modifier = Modifier.fillMaxWidth().height(26.dp)
                )

                Text(
                    text = "نماذج جاهزة سريعة:",
                    color = colorScheme.keyText.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(
                        Triple("Small", "صغير (85%)", 85),
                        Triple("Medium", "متوسط (100%)", 100),
                        Triple("Large", "كبير (115%)", 115),
                        Triple("ExtraLarge", "ضخم (130%)", 130)
                    )
                    presets.forEach { (id, label, pct) ->
                        val isSel = heightPercent == pct
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) colorScheme.accent.copy(alpha = 0.25f) else colorScheme.keyBackground)
                                .border(
                                    width = if (isSel) 1.5.dp else 0.8.dp,
                                    color = if (isSel) colorScheme.accent else colorScheme.keyBackground.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    HapticHelper.performKeyHaptic(context, view)
                                    onSelectHeight(id)
                                    onChangeHeightPercent(pct)
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) colorScheme.accent else colorScheme.keyText
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "حجم الخط الرئيسي: ${keyFontSizeSp}sp",
                        color = colorScheme.keyText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        MiniStepButton("-1", colorScheme) {
                            onChangeKeyFontSize((keyFontSizeSp - 1).coerceIn(14, 26))
                        }
                        MiniStepButton("افتراضي (19)", colorScheme) {
                            onChangeKeyFontSize(19)
                        }
                        MiniStepButton("+1", colorScheme) {
                            onChangeKeyFontSize((keyFontSizeSp + 1).coerceIn(14, 26))
                        }
                    }
                }
                Slider(
                    value = keyFontSizeSp.toFloat(),
                    onValueChange = { onChangeKeyFontSize(it.toInt()) },
                    valueRange = 14f..26f,
                    colors = SliderDefaults.colors(
                        thumbColor = colorScheme.accent,
                        activeTrackColor = colorScheme.accent,
                        inactiveTrackColor = colorScheme.keyBackground
                    ),
                    modifier = Modifier.fillMaxWidth().height(26.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "حجم التلميح الثانوي: ${secondaryFontSizeSp}sp",
                        color = colorScheme.keyText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        MiniStepButton("صغير (8)", colorScheme) { onChangeSecondaryFontSize(8) }
                        MiniStepButton("افتراضي (9)", colorScheme) { onChangeSecondaryFontSize(9) }
                        MiniStepButton("كبير (11)", colorScheme) { onChangeSecondaryFontSize(11) }
                    }
                }
                Slider(
                    value = secondaryFontSizeSp.toFloat(),
                    onValueChange = { onChangeSecondaryFontSize(it.toInt()) },
                    valueRange = 7f..14f,
                    colors = SliderDefaults.colors(
                        thumbColor = colorScheme.accent,
                        activeTrackColor = colorScheme.accent,
                        inactiveTrackColor = colorScheme.keyBackground
                    ),
                    modifier = Modifier.fillMaxWidth().height(26.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorScheme.keyBackground.copy(alpha = 0.5f))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "معاينة الخط: أ ب جـ 1 2 3 @ A B C",
                        fontSize = keyFontSizeSp.sp,
                        color = colorScheme.keyText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    isSelected: Boolean,
    colorScheme: KeyboardColorScheme,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) colorScheme.accent else colorScheme.keyBackground.copy(alpha = 0.7f))
            .clickable(onClick = onClick)
            .padding(vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else colorScheme.keyText
        )
    }
}

@Composable
private fun MiniStepButton(
    text: String,
    colorScheme: KeyboardColorScheme,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(colorScheme.specialKeyBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = colorScheme.specialKeyText,
            fontWeight = FontWeight.Medium
        )
    }
}
