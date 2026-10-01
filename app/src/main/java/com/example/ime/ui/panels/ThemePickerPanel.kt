package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.KeyboardProApp
import com.example.data.pref.KeyboardPreferences
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.theme.KeyboardThemes

@Composable
fun ThemePickerPanel(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onSelectTheme: ((String) -> Unit)? = null,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember {
        try {
            (context.applicationContext as? KeyboardProApp)?.preferences
                ?: KeyboardPreferences(context)
        } catch (e: Throwable) {
            KeyboardPreferences(context)
        }
    }

    val currentThemeName by prefs.themeState.collectAsState()
    var selectedCategory by remember { mutableStateOf("الكل") }

    val categories = listOf("الكل", "كلاسيكي", "ألوان", "Pink", "Purple", "Dark", "Cyberpunk", "Gradient")

    val filteredThemes = remember(selectedCategory) {
        if (selectedCategory == "الكل") {
            KeyboardThemes.allThemes
        } else {
            KeyboardThemes.allThemes.filter { theme ->
                theme.category.equals(selectedCategory, ignoreCase = true) ||
                        (selectedCategory == "Dark" && theme.isDark)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع",
                    tint = colorScheme.keyText,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "المظاهر والثيمات (${KeyboardThemes.allThemes.size})",
                color = colorScheme.keyText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = colorScheme.keyText.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Category Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.take(5).forEach { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) colorScheme.accent else colorScheme.keyBackground)
                        .clickable { selectedCategory = cat }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else colorScheme.keyText,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Themes Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 6.dp)
        ) {
            items(filteredThemes) { theme ->
                val isCurrent = currentThemeName.equals(theme.name, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(theme.background)
                        .border(
                            width = if (isCurrent) 2.dp else 1.dp,
                            color = if (isCurrent) theme.accent else Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            prefs.theme = theme.name
                            onSelectTheme?.invoke(theme.name)
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = theme.name,
                                color = theme.keyText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )

                            if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(theme.accent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "محدد",
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }

                        // Color Dots Palette Preview
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(theme.accent)
                                    .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(theme.keyBackground)
                                    .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(theme.specialKeyBackground)
                                    .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = theme.category,
                                color = theme.keyText.copy(alpha = 0.6f),
                                fontSize = 9.sp,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}
