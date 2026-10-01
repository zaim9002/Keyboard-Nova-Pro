package com.example.ime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.TranslationEngine
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper

@Composable
fun InlineTranslateBar(
    modifier: Modifier = Modifier,
    sourceLang: String,
    targetLang: String,
    colorScheme: KeyboardColorScheme,
    onSourceLangChange: (String) -> Unit,
    onTargetLangChange: (String) -> Unit,
    onSwapLanguages: () -> Unit,
    onTranslateNow: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    var showSourceMenu by remember { mutableStateOf(false) }
    var showTargetMenu by remember { mutableStateOf(false) }

    val languages = TranslationEngine.supportedLanguages
    val sourceItem = languages.find { it.code == sourceLang } ?: languages[0]
    val targetItem = languages.find { it.code == targetLang } ?: languages[1]

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Close button (red circular badge)
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFE53935).copy(alpha = 0.2f))
                .clickable {
                    HapticHelper.performKeyHaptic(context, view)
                    onClose()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "إغلاق الترجمة",
                tint = Color(0xFFE53935),
                modifier = Modifier.size(16.dp)
            )
        }

        // Source Language Dropdown Selector
        Box(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground)
                    .clickable {
                        HapticHelper.performKeyHaptic(context, view)
                        showSourceMenu = true
                    }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${sourceItem.flag} ${sourceItem.nameAr} ▾",
                    color = colorScheme.keyText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            DropdownMenu(
                expanded = showSourceMenu,
                onDismissRequest = { showSourceMenu = false }
            ) {
                languages.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text("${lang.flag} ${lang.nameAr} (${lang.nameEn})") },
                        onClick = {
                            onSourceLangChange(lang.code)
                            showSourceMenu = false
                        }
                    )
                }
            }
        }

        // Swap button
        IconButton(
            onClick = {
                HapticHelper.performKeyHaptic(context, view)
                onSwapLanguages()
            },
            modifier = Modifier.size(34.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "تبديل اللغات",
                tint = colorScheme.accent,
                modifier = Modifier.size(20.dp)
            )
        }

        // Target Language Dropdown Selector
        Box(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground)
                    .clickable {
                        HapticHelper.performKeyHaptic(context, view)
                        showTargetMenu = true
                    }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${targetItem.flag} ${targetItem.nameAr} ▾",
                    color = colorScheme.keyText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            DropdownMenu(
                expanded = showTargetMenu,
                onDismissRequest = { showTargetMenu = false }
            ) {
                languages.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text("${lang.flag} ${lang.nameAr} (${lang.nameEn})") },
                        onClick = {
                            onTargetLangChange(lang.code)
                            showTargetMenu = false
                        }
                    )
                }
            }
        }

        // Quick Translate action button
        Box(
            modifier = Modifier
                .height(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.accent)
                .clickable {
                    HapticHelper.performKeyHaptic(context, view)
                    onTranslateNow()
                }
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "ترجم",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
