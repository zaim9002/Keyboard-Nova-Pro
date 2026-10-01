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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme

data class MenuItemData(
    val id: String,
    val title: String,
    val icon: ImageVector? = null,
    val textIcon: String? = null,
    val hasBadge: Boolean = false
)

@Composable
fun KeyboardMenuPanel(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onOpenThemes: () -> Unit,
    onOpenVoice: () -> Unit,
    onOpenMiniGame: () -> Unit,
    onOpenTranslate: () -> Unit,
    onOpenQuickText: () -> Unit,
    onOpenTextEditing: () -> Unit,
    onOpenCalculator: () -> Unit,
    onOpenNotes: () -> Unit,
    onToggleOneHanded: () -> Unit,
    onOpenNews: () -> Unit,
    onOpenFonts: () -> Unit,
    onOpenAi: () -> Unit = {},
    onOpenHandwriting: () -> Unit,
    onOpenInstaFonts: () -> Unit,
    onToggleTashkeel: () -> Unit = {},
    onOpenResize: () -> Unit = {},
    onOpenNumpad: () -> Unit = {},
    onToggleNumberRow: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenToolbarEditor: () -> Unit,
    onSwitchIme: (() -> Unit)? = null,
    onClose: () -> Unit
) {
    val menuItems = listOf(
        // Row 1 (Matches user screenshot)
        MenuItemData("switch_ime", "تبديل الكيبورد", icon = Icons.Default.Keyboard, hasBadge = true),
        MenuItemData("theme", "المظاهر والثيمات", icon = Icons.Default.Checkroom),
        MenuItemData("voice", "الكتابة بالصوت", icon = Icons.Default.Mic),
        MenuItemData("game", "لعبة الكيبورد", icon = Icons.Default.SportsEsports, hasBadge = true),

        // Row 2 (Matches user screenshot)
        MenuItemData("translate", "ترجمة فورية", icon = Icons.Default.Translate),
        MenuItemData("quick_text", "الحافظة المشفرة", icon = Icons.Default.Assignment),
        MenuItemData("text_edit", "تحريك المؤشر", icon = Icons.Default.OpenWith),
        MenuItemData("calc", "آلة حاسبة", icon = Icons.Default.Calculate),

        // Row 3 (Matches user screenshot)
        MenuItemData("notes", "الملاحظات", icon = Icons.Default.Notes),
        MenuItemData("one_handed", "وضع اليد الواحدة", icon = Icons.Default.StayCurrentPortrait),
        MenuItemData("news", "أخبار وترند", icon = Icons.Default.Newspaper),
        MenuItemData("font", "زخرفة النصوص", textIcon = "Aa"),

        // Row 4 (Extended features of Mohammed v.1)
        MenuItemData("ai", "مساعد الذكاء", icon = Icons.Default.AutoAwesome, hasBadge = true),
        MenuItemData("handwriting", "الرسم باليد", icon = Icons.Default.Draw),
        MenuItemData("insta_font", "خطوط إنستغرام", textIcon = "✨"),
        MenuItemData("tashkeel", "شريط التشكيل", textIcon = "ـَـُـِ"),

        // Row 5
        MenuItemData("resize", "حجم اللوحة", icon = Icons.Default.AspectRatio),
        MenuItemData("numpad", "لوحة الأرقام", textIcon = "#123", hasBadge = true),
        MenuItemData("numbers", "صف الأرقام", textIcon = "123"),
        MenuItemData("settings", "الإعدادات الشاملة", icon = Icons.Default.Settings)
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(colorScheme.background)
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // In RTL: First item is placed on the Right
                IconButton(
                    onClick = onOpenToolbarEditor,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "تعديل شريط الأدوات",
                        tint = colorScheme.keyText,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "قائمة ميزات كيبورد محمد v.1",
                    color = colorScheme.keyText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                // In RTL: Last item is placed on the Left
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Keyboard,
                        contentDescription = "إغلاق والعودة للوحة المفاتيح",
                        tint = colorScheme.keyText,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // 4-Column Feature Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 6.dp)
            ) {
                items(menuItems) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.keyBackground)
                            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .clickable {
                                when (item.id) {
                                    "switch_ime" -> {
                                        if (onSwitchIme != null) onSwitchIme() else onOpenSettings()
                                    }
                                    "theme" -> onOpenThemes()
                                    "voice" -> onOpenVoice()
                                    "game" -> onOpenMiniGame()
                                    "translate" -> onOpenTranslate()
                                    "quick_text" -> onOpenQuickText()
                                    "text_edit" -> onOpenTextEditing()
                                    "calc" -> onOpenCalculator()
                                    "notes" -> onOpenNotes()
                                    "one_handed" -> onToggleOneHanded()
                                    "news" -> onOpenNews()
                                    "font" -> onOpenFonts()
                                    "ai" -> onOpenAi()
                                    "handwriting" -> onOpenHandwriting()
                                    "insta_font" -> onOpenInstaFonts()
                                    "tashkeel" -> onToggleTashkeel()
                                    "resize" -> onOpenResize()
                                    "numpad" -> onOpenNumpad()
                                    "numbers" -> onToggleNumberRow()
                                    "settings" -> onOpenSettings()
                                }
                            }
                            .padding(4.dp)
                    ) {
                        // Red notification badge on top-right (in RTL Alignment.TopStart is top-right)
                        if (item.hasBadge) {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF3B30))
                                    .align(Alignment.TopStart)
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (item.icon != null) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = colorScheme.keyText,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else if (item.textIcon != null) {
                                Text(
                                    text = item.textIcon,
                                    color = colorScheme.keyText,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = item.title,
                                color = colorScheme.keyText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
