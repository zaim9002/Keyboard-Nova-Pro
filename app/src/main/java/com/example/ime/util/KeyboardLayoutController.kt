package com.example.ime.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object KeyboardLayoutController {
    fun getKeyHeight(heightPref: String, heightPercent: Int = 100): Dp {
        val baseHeight = when (heightPref) {
            "VerySmall" -> 38f
            "Small" -> 42f
            "Medium" -> 48f
            "Large" -> 54f
            "ExtraLarge" -> 60f
            else -> heightPref.toIntOrNull()?.toFloat() ?: 48f
        }
        val effectiveDp = if (heightPercent != 100 && heightPercent in 60..150) {
            (baseHeight * (heightPercent / 100f)).coerceIn(34f, 72f)
        } else {
            baseHeight
        }
        return effectiveDp.dp
    }

    fun getPanelHeight(heightPref: String, showNumberRow: Boolean, heightPercent: Int = 100): Dp {
        val keyHeight = getKeyHeight(heightPref, heightPercent)
        val numberRowHeight = if (showNumberRow) keyHeight * 0.85f else 0.dp
        return (keyHeight * 4) + numberRowHeight + 52.dp
    }

    fun getKeyFontSize(fontSizePref: String, customSp: Int = 19): TextUnit {
        if (customSp in 12..28 && customSp != 19) {
            return customSp.sp
        }
        return when (fontSizePref) {
            "Small" -> 16.sp
            "Large" -> 22.sp
            "ExtraLarge" -> 25.sp
            else -> 19.sp
        }
    }

    fun getSecondaryFontSize(customSp: Int = 9): TextUnit {
        return customSp.coerceIn(7, 14).sp
    }
}
