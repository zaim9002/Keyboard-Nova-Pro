package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.TranslationEngine
import com.example.ime.theme.KeyboardColorScheme
import com.example.ime.util.HapticHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TranslatePanel(
    modifier: Modifier = Modifier,
    initialText: String,
    autoTranslateOnEnter: Boolean = false,
    colorScheme: KeyboardColorScheme,
    onToggleAutoTranslate: (Boolean) -> Unit = {},
    onCommitTranslation: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    var sourceLang by remember { mutableStateOf("ar") }
    var targetLang by remember { mutableStateOf("en") }
    var inputText by remember { mutableStateOf(if (initialText.isNotBlank()) initialText else "") }
    var translatedText by remember { mutableStateOf("") }
    var isTranslating by remember { mutableStateOf(false) }
    var translationJob by remember { mutableStateOf<Job?>(null) }
    var showSourceSelector by remember { mutableStateOf(false) }
    var showTargetSelector by remember { mutableStateOf(false) }

    LaunchedEffect(inputText, sourceLang, targetLang) {
        if (inputText.isBlank()) {
            translatedText = ""
            isTranslating = false
            return@LaunchedEffect
        }
        isTranslating = true
        translationJob?.cancel()
        translationJob = scope.launch {
            delay(150)
            val res = TranslationEngine.translateAsync(inputText, sourceLang, targetLang)
            translatedText = res
            isTranslating = false
        }
    }

    val sourceObj = TranslationEngine.supportedLanguages.find { it.code == sourceLang }
        ?: TranslationEngine.supportedLanguages.first()
    val targetObj = TranslationEngine.supportedLanguages.find { it.code == targetLang }
        ?: TranslationEngine.supportedLanguages[1]

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground)
                    .clickable {
                        HapticHelper.performKeyHaptic(context, view)
                        showSourceSelector = !showSourceSelector
                        showTargetSelector = false
                    }
                    .padding(horizontal = 6.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${sourceObj.flag} ${sourceObj.nameAr}",
                    color = colorScheme.keyText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }

            IconButton(
                onClick = {
                    HapticHelper.performKeyHaptic(context, view)
                    val temp = sourceLang
                    sourceLang = targetLang
                    targetLang = temp
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "تبديل",
                    tint = colorScheme.accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground)
                    .clickable {
                        HapticHelper.performKeyHaptic(context, view)
                        showTargetSelector = !showTargetSelector
                        showSourceSelector = false
                    }
                    .padding(horizontal = 6.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${targetObj.flag} ${targetObj.nameAr}",
                    color = colorScheme.keyText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
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
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        if (showSourceSelector || showTargetSelector) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(TranslationEngine.supportedLanguages) { lang ->
                    val isSelected = if (showSourceSelector) lang.code == sourceLang else lang.code == targetLang
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) colorScheme.accent else colorScheme.keyBackground)
                            .clickable {
                                HapticHelper.performKeyHaptic(context, view)
                                if (showSourceSelector) {
                                    sourceLang = lang.code
                                    showSourceSelector = false
                                } else {
                                    targetLang = lang.code
                                    showTargetSelector = false
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${lang.flag} ${lang.nameAr}",
                            color = if (isSelected) Color.White else colorScheme.keyText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground)
                    .padding(8.dp)
            ) {
                if (inputText.isEmpty()) {
                    Text(
                        text = "اكتب النص للترجمة...",
                        color = colorScheme.keyText.copy(alpha = 0.4f),
                        fontSize = 12.sp
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = colorScheme.keyText,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.accent.copy(alpha = 0.14f))
                    .padding(8.dp),
                contentAlignment = Alignment.TopStart
            ) {
                if (isTranslating) {
                    Text(
                        text = "جاري الترجمة الفورية...",
                        color = colorScheme.accent,
                        fontSize = 12.sp
                    )
                } else {
                    Text(
                        text = if (translatedText.isNotEmpty()) translatedText else "الترجمة ستظهر هنا...",
                        color = if (translatedText.isNotEmpty()) colorScheme.keyText else colorScheme.keyText.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (autoTranslateOnEnter) colorScheme.accent.copy(alpha = 0.25f)
                        else colorScheme.keyBackground
                    )
                    .border(
                        width = if (autoTranslateOnEnter) 1.5.dp else 0.5.dp,
                        color = if (autoTranslateOnEnter) colorScheme.accent else colorScheme.keyText.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        HapticHelper.performKeyHaptic(context, view)
                        onToggleAutoTranslate(!autoTranslateOnEnter)
                    }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (autoTranslateOnEnter) Icons.Default.Bolt else Icons.Default.FlashOff,
                        contentDescription = "ترجمة تلقائية",
                        tint = if (autoTranslateOnEnter) colorScheme.accent else colorScheme.keyText.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (autoTranslateOnEnter) "ترجمة Enter: مفعلة" else "ترجمة Enter",
                        color = if (autoTranslateOnEnter) colorScheme.accent else colorScheme.keyText,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Button(
                onClick = {
                    HapticHelper.performKeyHaptic(context, view)
                    if (translatedText.isNotEmpty()) {
                        onCommitTranslation(translatedText)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.accent),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "إرسال الترجمة",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
