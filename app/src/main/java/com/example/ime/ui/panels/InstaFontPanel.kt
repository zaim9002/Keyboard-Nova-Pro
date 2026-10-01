package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme

data class InstaFontStyle(
    val name: String,
    val transform: (String) -> String
)

@Composable
fun InstaFontPanel(
    modifier: Modifier = Modifier,
    draftText: String,
    colorScheme: KeyboardColorScheme,
    onInsertText: (String) -> Unit,
    onClose: () -> Unit
) {
    var sampleText by remember { mutableStateOf(if (draftText.isNotBlank()) draftText else "كيبورد محمد") }

    val styles = listOf(
        InstaFontStyle("عريض (Bold)") { text ->
            val normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val bold = "𝐚𝐛𝐜𝐝𝐞𝐟𝐠𝐡𝐢𝐣𝐤𝐥𝐦𝐧𝐨𝐩𝐪𝐫𝐬𝐭𝐮𝐯𝐰𝐱𝐲𝐳𝐀𝐁𝐂𝐃𝐄𝐅𝐆𝐇𝐈𝐉𝐊𝐋𝐌𝐍𝐎𝐏𝐐𝐑𝐒𝐓𝐔𝐕𝐖𝐗𝐘𝐙𝟎𝟏𝟐𝟑𝟒𝟓𝟔𝟕𝟖𝟗"
            text.map { ch ->
                val idx = normal.indexOf(ch)
                if (idx >= 0) bold.substring(idx * 2, idx * 2 + 2) else ch.toString()
            }.joinToString("")
        },
        InstaFontStyle("مائل (Italic)") { text ->
            val normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            val italic = "𝘢𝘣𝘤𝘥𝘦𝘧𝘨𝘩𝘪𝘫𝘬𝘭𝘮𝘯𝘰𝘱𝘲𝘳𝘴𝘵𝘶𝘷𝘸𝘹𝘺𝘻𝘈𝘉𝘊𝘋𝘌𝘍𝘎𝘏𝘐𝘑𝘒𝘓𝘔𝘕𝘖𝘗𝘘𝘙𝘚𝘛𝘜𝘝𝘞𝘟𝘠𝘡"
            text.map { ch ->
                val idx = normal.indexOf(ch)
                if (idx >= 0) italic.substring(idx * 2, idx * 2 + 2) else ch.toString()
            }.joinToString("")
        },
        InstaFontStyle("دوائر (Bubbles)") { text ->
            val normal = "abcdefghijklmnopqrstuvwxyz0123456789"
            val bubbles = "ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ⓪①②③④⑤⑥⑦⑧⑨"
            text.lowercase().map { ch ->
                val idx = normal.indexOf(ch)
                if (idx >= 0) bubbles.substring(idx * 2, idx * 2 + 2) else ch.toString()
            }.joinToString("")
        },
        InstaFontStyle("مربعات (Squares)") { text ->
            val normal = "abcdefghijklmnopqrstuvwxyz"
            val squares = "🄰🄱🄲🄳🄴🄵🄶🄷🄸🄹🄺🄻🄼🄽🄾🄿🅀🅁🅂🅃🅄🅅🅆🅇🅈🅉"
            text.lowercase().map { ch ->
                val idx = normal.indexOf(ch)
                if (idx >= 0) squares.substring(idx * 2, idx * 2 + 2) else ch.toString()
            }.joinToString("")
        },
        InstaFontStyle("مزخرف بالنجوم") { text -> "★ $text ★" },
        InstaFontStyle("مزخرف بالتاج") { text -> "👑 $text 👑" },
        InstaFontStyle("مزخرف بالزهور") { text -> "🌸 $text 🌸" },
        InstaFontStyle("بين قلوب") { text -> "💖 $text 💖" }
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "✨ خطوط Insta & Bio",
                color = colorScheme.accent,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = colorScheme.keyText, modifier = Modifier.size(18.dp))
            }
        }

        TextField(
            value = sampleText,
            onValueChange = { sampleText = it },
            placeholder = { Text("اكتب النص هنا...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.keyBackground,
                unfocusedContainerColor = colorScheme.keyBackground,
                focusedTextColor = colorScheme.keyText,
                unfocusedTextColor = colorScheme.keyText
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(styles) { style ->
                val formatted = style.transform(sampleText)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorScheme.keyBackground)
                        .clickable { onInsertText(formatted) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(style.name, color = colorScheme.accent, fontSize = 10.sp)
                        Text(formatted, color = colorScheme.keyText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Text("إدراج", color = colorScheme.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
