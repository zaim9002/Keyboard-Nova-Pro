package com.example.ime.ui.panels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserWordEntity
import com.example.engine.AiService
import com.example.engine.ProofreadResult
import com.example.engine.SmartReply
import com.example.engine.WritingTone
import com.example.ime.theme.KeyboardColorScheme
import kotlinx.coroutines.launch

enum class AiTab(val title: String, val icon: String) {
    TONE("صياغة النبرات", "🎭"),
    PROOFREAD("التدقيق اللغوي", "✨"),
    REPLIES("ردود ذكية", "💬"),
    CUSTOM_WORDS("قاموسي الشخصي", "📚")
}

@Composable
fun AiAssistantPanel(
    modifier: Modifier = Modifier,
    initialText: String,
    apiKey: String? = null,
    userWords: List<UserWordEntity> = emptyList(),
    colorScheme: KeyboardColorScheme,
    onApplyText: (String) -> Unit,
    onAddWordToDictionary: (String) -> Unit,
    onDeleteUserWord: (Long) -> Unit = {},
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(AiTab.TONE) }
    var draftText by remember { mutableStateOf(initialText) }
    var selectedTone by remember { mutableStateOf(WritingTone.FORMAL) }
    var transformedText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var proofreadResult by remember { mutableStateOf<ProofreadResult?>(null) }
    var smartReplies by remember { mutableStateOf<List<SmartReply>>(emptyList()) }
    var newCustomWord by remember { mutableStateOf("") }
    var notificationMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    fun processCurrentAction() {
        if (draftText.isBlank()) return
        isProcessing = true
        coroutineScope.launch {
            try {
                when (selectedTab) {
                    AiTab.TONE -> {
                        val result = AiService.rewriteWithTone(draftText, selectedTone, apiKey)
                        transformedText = result
                    }
                    AiTab.PROOFREAD -> {
                        val result = AiService.proofreadAndCorrect(draftText, apiKey)
                        proofreadResult = result
                    }
                    AiTab.REPLIES -> {
                        val results = AiService.generateSmartReplies(draftText, apiKey)
                        smartReplies = results
                    }
                    AiTab.CUSTOM_WORDS -> {}
                }
            } finally {
                isProcessing = false
            }
        }
    }

    LaunchedEffect(selectedTab, selectedTone) {
        if (draftText.isNotBlank()) {
            processCurrentAction()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 4.dp)
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
                Text(
                    text = "🤖 مساعد الذكاء الاصطناعي (Gemini AI)",
                    color = colorScheme.accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                val isOnline = !AiService.getResolvedApiKey(apiKey).isNullOrBlank()
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isOnline) colorScheme.accent.copy(alpha = 0.2f) else colorScheme.keyBackground)
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isOnline) "Gemini 2.5 Flash" else "معالج ذكي محلي",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isOnline) colorScheme.accent else colorScheme.keyText.copy(alpha = 0.7f)
                    )
                }
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    tint = colorScheme.keyText.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        notificationMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorScheme.accent.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = msg,
                    color = colorScheme.accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AiTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) colorScheme.accent
                            else colorScheme.keyBackground.copy(alpha = 0.6f)
                        )
                        .clickable { selectedTab = tab }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${tab.icon} ${tab.title}",
                        color = if (isSelected) colorScheme.keyBackground else colorScheme.keyText,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (selectedTab != AiTab.CUSTOM_WORDS) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorScheme.keyBackground)
                    .border(1.dp, colorScheme.accent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicDraftField(
                        value = draftText,
                        onValueChange = { draftText = it },
                        placeholder = "أدخل النص المراد صياغته أو تدقيقه...",
                        textColor = colorScheme.keyText,
                        modifier = Modifier.weight(1f)
                    )
                    if (draftText.isNotBlank()) {
                        IconButton(
                            onClick = { draftText = "" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "مسح",
                                tint = colorScheme.keyText.copy(alpha = 0.5f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = { processCurrentAction() },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "معالجة",
                            tint = colorScheme.accent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            when (selectedTab) {
                AiTab.TONE -> {
                    ToneTabContent(
                        selectedTone = selectedTone,
                        transformedText = transformedText,
                        isProcessing = isProcessing,
                        colorScheme = colorScheme,
                        onSelectTone = { selectedTone = it },
                        onApply = {
                            if (transformedText.isNotBlank()) {
                                onApplyText(transformedText)
                                onClose()
                            }
                        },
                        onCopy = {
                            if (transformedText.isNotBlank()) {
                                clipboardManager.setText(AnnotatedString(transformedText))
                                notificationMessage = "تم نسخ النص المصاغ للحافظة"
                            }
                        }
                    )
                }
                AiTab.PROOFREAD -> {
                    ProofreadTabContent(
                        result = proofreadResult,
                        isProcessing = isProcessing,
                        colorScheme = colorScheme,
                        onApply = { corrected ->
                            onApplyText(corrected)
                            onClose()
                        },
                        onCopy = { corrected ->
                            clipboardManager.setText(AnnotatedString(corrected))
                            notificationMessage = "تم نسخ النص المصحح للحافظة"
                        }
                    )
                }
                AiTab.REPLIES -> {
                    RepliesTabContent(
                        replies = smartReplies,
                        isProcessing = isProcessing,
                        colorScheme = colorScheme,
                        onSelectReply = { text ->
                            onApplyText(text)
                            onClose()
                        }
                    )
                }
                AiTab.CUSTOM_WORDS -> {
                    CustomWordsTabContent(
                        userWords = userWords,
                        newWord = newCustomWord,
                        colorScheme = colorScheme,
                        onWordChange = { newCustomWord = it },
                        onAddWord = { word ->
                            if (word.isNotBlank()) {
                                onAddWordToDictionary(word.trim())
                                newCustomWord = ""
                                notificationMessage = "تمت إضافة الكلمة للقاموس"
                            }
                        },
                        onDeleteWord = onDeleteUserWord
                    )
                }
            }
        }
    }
}

@Composable
private fun ToneTabContent(
    selectedTone: WritingTone,
    transformedText: String,
    isProcessing: Boolean,
    colorScheme: KeyboardColorScheme,
    onSelectTone: (WritingTone) -> Unit,
    onApply: () -> Unit,
    onCopy: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            WritingTone.values().forEach { tone ->
                val isSelected = selectedTone == tone
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isSelected) colorScheme.accent.copy(alpha = 0.25f)
                            else colorScheme.keyBackground.copy(alpha = 0.5f)
                        )
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) colorScheme.accent else androidx.compose.ui.graphics.Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { onSelectTone(tone) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${tone.icon} ${tone.title}",
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) colorScheme.accent else colorScheme.keyText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.keyBackground.copy(alpha = 0.7f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                if (isProcessing) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = colorScheme.accent, strokeWidth = 2.dp)
                            Text(text = "جاري الصياغة بنبرة ${selectedTone.title}...", fontSize = 11.sp, color = colorScheme.keyText)
                        }
                    }
                } else if (transformedText.isNotBlank()) {
                    Text(
                        text = transformedText,
                        fontSize = 12.sp,
                        color = colorScheme.keyText,
                        lineHeight = 17.sp,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onApply,
                            modifier = Modifier.weight(1f).height(32.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.accent),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(text = "استخدام في النص", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colorScheme.keyBackground)
                        }
                        OutlinedButton(
                            onClick = onCopy,
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(13.dp), tint = colorScheme.accent)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "نسخ", fontSize = 11.sp, color = colorScheme.accent)
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "اختر نبرة بالأعلى لصياغة النص فوراً",
                            fontSize = 11.sp,
                            color = colorScheme.keyText.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProofreadTabContent(
    result: ProofreadResult?,
    isProcessing: Boolean,
    colorScheme: KeyboardColorScheme,
    onApply: (String) -> Unit,
    onCopy: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.keyBackground.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            if (isProcessing) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = colorScheme.accent, strokeWidth = 2.dp)
                        Text(text = "جاري التدقيق اللغوي والنحوي...", fontSize = 11.sp, color = colorScheme.keyText)
                    }
                }
            } else if (result != null && result.corrected.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    result.improvements.forEach { imp ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(colorScheme.accent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "✓ $imp", fontSize = 10.sp, color = colorScheme.accent, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = result.corrected,
                    fontSize = 12.sp,
                    color = colorScheme.keyText,
                    lineHeight = 17.sp,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onApply(result.corrected) },
                        modifier = Modifier.weight(1f).height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.accent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(text = "استخدام التصحيح", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colorScheme.keyBackground)
                    }
                    OutlinedButton(
                        onClick = { onCopy(result.corrected) },
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(13.dp), tint = colorScheme.accent)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "نسخ", fontSize = 11.sp, color = colorScheme.accent)
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "أدخل نصاً لتدقيق الإملاء، الهمزات، والتاء المربوطة",
                        fontSize = 11.sp,
                        color = colorScheme.keyText.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun RepliesTabContent(
    replies: List<SmartReply>,
    isProcessing: Boolean,
    colorScheme: KeyboardColorScheme,
    onSelectReply: (String) -> Unit
) {
    if (isProcessing) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = colorScheme.accent, strokeWidth = 2.dp)
                Text(text = "جاري توليد الردود الذكية...", fontSize = 11.sp, color = colorScheme.keyText)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(replies) { reply ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(colorScheme.keyBackground.copy(alpha = 0.7f))
                        .clickable { onSelectReply(reply.text) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = reply.icon, fontSize = 12.sp)
                                Text(text = reply.label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colorScheme.accent)
                            }
                            Text(
                                text = reply.text,
                                fontSize = 11.sp,
                                color = colorScheme.keyText,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "استخدام",
                            tint = colorScheme.accent,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomWordsTabContent(
    userWords: List<UserWordEntity>,
    newWord: String,
    colorScheme: KeyboardColorScheme,
    onWordChange: (String) -> Unit,
    onAddWord: (String) -> Unit,
    onDeleteWord: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colorScheme.keyBackground)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicDraftField(
                    value = newWord,
                    onValueChange = onWordChange,
                    placeholder = "أضف كلمة جديدة للقاموس...",
                    textColor = colorScheme.keyText,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Button(
                onClick = { onAddWord(newWord) },
                enabled = newWord.isNotBlank(),
                modifier = Modifier.height(34.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.accent),
                contentPadding = PaddingValues(horizontal = 12.dp),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(text = "+ حفظ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = colorScheme.keyBackground)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        if (userWords.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "لم تتم إضافة أي كلمات شخصية للقاموس بعد",
                    fontSize = 11.sp,
                    color = colorScheme.keyText.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                items(userWords) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(colorScheme.keyBackground.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.word,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.keyText
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "تكرار: ${item.frequency}",
                                    fontSize = 10.sp,
                                    color = colorScheme.accent
                                )
                                IconButton(
                                    onClick = { onDeleteWord(item.id) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف",
                                        tint = colorScheme.keyText.copy(alpha = 0.4f),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BasicDraftField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    textColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = androidx.compose.ui.text.TextStyle(
            color = textColor,
            fontSize = 12.sp
        ),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = textColor.copy(alpha = 0.4f),
                    fontSize = 12.sp
                )
            }
            innerTextField()
        }
    )
}
