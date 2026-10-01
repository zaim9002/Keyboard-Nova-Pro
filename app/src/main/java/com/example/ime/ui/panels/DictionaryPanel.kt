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
import com.example.engine.DictionaryEngine
import com.example.ime.theme.KeyboardColorScheme

@Composable
fun DictionaryPanel(
    modifier: Modifier = Modifier,
    initialWord: String,
    colorScheme: KeyboardColorScheme,
    onReplaceWord: (String) -> Unit,
    onClose: () -> Unit
) {
    var searchQuery by remember { mutableStateOf(if (initialWord.isNotBlank()) initialWord else "سلام") }
    val definition = remember(searchQuery) {
        DictionaryEngine.lookup(searchQuery)
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "📖 المعجم والمترادفات",
                color = colorScheme.accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "ابحث عن كلمة...",
                        color = colorScheme.keyText.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = colorScheme.keyText,
                        fontSize = 12.sp
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = colorScheme.keyText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (definition != null) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorScheme.keyBackground)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "المعنى: ${definition.word}",
                                color = colorScheme.accent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = definition.definition,
                                color = colorScheme.keyText,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colorScheme.keyBackground)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "مثال:",
                                color = colorScheme.accent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "\"${definition.example}\"",
                                color = colorScheme.keyText,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "المترادفات (اضغط للاستخدام):",
                        color = colorScheme.keyText.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(definition.synonyms) { synonym ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(colorScheme.accent.copy(alpha = 0.15f))
                            .clickable { onReplaceWord(synonym) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = synonym,
                                color = colorScheme.keyText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "استبدال",
                                color = colorScheme.accent,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لم يتم العثور على الكلمة في المعجم المدمج",
                    color = colorScheme.keyText.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        }
    }
}
