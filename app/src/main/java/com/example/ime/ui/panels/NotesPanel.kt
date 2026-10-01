package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme

@Composable
fun NotesPanel(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onInsertNote: (String) -> Unit,
    onClose: () -> Unit
) {
    var noteInput by remember { mutableStateOf("") }
    var notesList by remember {
        mutableStateOf(
            listOf(
                "رقم الحساب البنكي / آيبان: SA0380000000608010167519",
                "عنوان التوصيل الرئيسي: الرياض، حي النخيل، شارع الأمير تركي",
                "أفكار منشورات تويتر للمشروع الجديد",
                "قائمة المشتريات: بن، تمر، مكسرات، هيل"
            )
        )
    }

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
                text = "📝 الملاحظات السريعة",
                color = colorScheme.accent,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = colorScheme.keyText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                placeholder = { Text("أضف ملاحظة سريعة...", fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.keyBackground,
                    unfocusedContainerColor = colorScheme.keyBackground,
                    focusedTextColor = colorScheme.keyText,
                    unfocusedTextColor = colorScheme.keyText
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Button(
                onClick = {
                    if (noteInput.isNotBlank()) {
                        notesList = listOf(noteInput.trim()) + notesList
                        noteInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.accent),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(notesList) { note ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorScheme.keyBackground)
                        .clickable { onInsertNote(note) }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = note,
                        color = colorScheme.keyText,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { notesList = notesList.filter { it != note } },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "حذف",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
