package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme

data class ToolbarItemConfig(
    val id: String,
    val title: String,
    val icon: ImageVector? = null,
    val textIcon: String? = null
)

@Composable
fun ToolbarEditorPanel(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onClose: () -> Unit
) {
    var activeItems by remember {
        mutableStateOf(
            listOf(
                ToolbarItemConfig("game", "ألعاب", Icons.Default.SportsEsports),
                ToolbarItemConfig("voice", "صوت", Icons.Default.Mic),
                ToolbarItemConfig("translate", "ترجمة", Icons.Default.Translate),
                ToolbarItemConfig("quick_text", "حافظة", Icons.Default.FlashOn),
                ToolbarItemConfig("theme", "ثيمات", Icons.Default.Checkroom),
                ToolbarItemConfig("emoji", "إيموجي", Icons.Default.Mood)
            )
        )
    }

    val allAvailableTools = listOf(
        ToolbarItemConfig("emoji", "إيموجي", Icons.Default.Mood),
        ToolbarItemConfig("handwriting", "كتابة يدوية", Icons.Default.Draw),
        ToolbarItemConfig("voice", "صوت", Icons.Default.Mic),
        ToolbarItemConfig("text_edit", "تعديل النص", Icons.Default.OpenWith),
        ToolbarItemConfig("quick_text", "حافظة سريعة", Icons.Default.FlashOn),
        ToolbarItemConfig("game", "ألعاب مصغرة", Icons.Default.SportsEsports),
        ToolbarItemConfig("emoticons", "تعبيرات :-)", textIcon = ":-)"),
        ToolbarItemConfig("translate", "ترجمة", Icons.Default.Translate),
        ToolbarItemConfig("notes", "ملاحظات", Icons.Default.EditNote),
        ToolbarItemConfig("numbers", "أرقام", textIcon = "123"),
        ToolbarItemConfig("news", "أخبار", Icons.Default.Newspaper),
        ToolbarItemConfig("theme", "مظاهر", Icons.Default.Checkroom),
        ToolbarItemConfig("font", "خطوط", textIcon = "Aa"),
        ToolbarItemConfig("one_handed", "يد واحدة", Icons.Default.Smartphone),
        ToolbarItemConfig("settings", "إعدادات", Icons.Default.Settings),
        ToolbarItemConfig("insta_font", "خطوط إنستا", textIcon = "✨"),
        ToolbarItemConfig("calc", "حاسبة", Icons.Default.Calculate)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF161618))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.HelpOutline, contentDescription = "مساعدة", tint = Color.LightGray)
            }
            Text(
                text = "تخصيص شريط الأدوات",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "رجوع", tint = Color.White)
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "الأدوات النشطة في الشريط (اضغط للحذف):",
            color = Color.Gray,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF222226))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(activeItems) { item ->
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2E2E34)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopStart)
                            .clip(CircleShape)
                            .background(Color(0xFFFF3B30))
                            .clickable {
                                if (activeItems.size > 2) {
                                    activeItems = activeItems.filter { it.id != item.id }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    if (item.icon != null) {
                        Icon(item.icon, contentDescription = item.title, tint = Color.White, modifier = Modifier.size(20.dp))
                    } else if (item.textIcon != null) {
                        Text(item.textIcon, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "الأدوات المتوفرة (اضغط للإضافة):",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    allAvailableTools.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowItems.forEach { tool ->
                                val isAdded = activeItems.any { it.id == tool.id }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isAdded) Color(0xFF1E293B) else Color(0xFF242428))
                                        .clickable {
                                            if (!isAdded && activeItems.size < 7) {
                                                activeItems = activeItems + tool
                                            }
                                        }
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        if (tool.icon != null) {
                                            Icon(
                                                tool.icon,
                                                contentDescription = tool.title,
                                                tint = if (isAdded) Color(0xFF818CF8) else Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else if (tool.textIcon != null) {
                                            Text(
                                                tool.textIcon,
                                                color = if (isAdded) Color(0xFF818CF8) else Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            tool.title,
                                            color = if (isAdded) Color(0xFF818CF8) else Color.LightGray,
                                            fontSize = 9.sp,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            repeat(3 - rowItems.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
