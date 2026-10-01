package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
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
import kotlinx.coroutines.delay

@Composable
fun MiniGamePanel(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onClose: () -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var highScore by remember { mutableIntStateOf(0) }
    var targetChar by remember { mutableStateOf("⚡") }
    var choices by remember { mutableStateOf(listOf("⚡", "🔥", "✨", "🚀")) }
    var streak by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var isGameOver by remember { mutableStateOf(false) }

    val emojis = listOf("⚡", "🔥", "✨", "🚀", "💎", "⭐", "🎉", "👑", "🍕", "🎮", "👾", "🤖")

    fun nextRound() {
        val picked = emojis.shuffled().take(4)
        choices = picked
        targetChar = picked.random()
    }

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            score = 0
            timeLeft = 30
            nextRound()
            while (timeLeft > 0 && !isGameOver) {
                delay(1000)
                timeLeft--
            }
            isGameOver = true
            if (score > highScore) highScore = score
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🎮 لعبة سرعة البديهة",
                    color = colorScheme.accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "الوقت: $timeLeft ثانية",
                    color = if (timeLeft <= 5) Color.Red else colorScheme.keyText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

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

        if (isGameOver) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "انتهت الجولة!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.accent
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "نقاطك: $score  |  أعلى نتيجة: $highScore",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.keyText
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = { isGameOver = false },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.accent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("العب مرة أخرى", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "اضغط على الرمز المطابق بسرعة:",
                    color = colorScheme.keyText.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(colorScheme.accent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = targetChar, fontSize = 34.sp)
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    choices.forEach { item ->
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colorScheme.keyBackground)
                                .clickable {
                                    if (item == targetChar) {
                                        score += 10
                                        streak++
                                        nextRound()
                                    } else {
                                        if (score > 0) score -= 5
                                        streak = 0
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item, fontSize = 28.sp)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("النقاط: $score", color = colorScheme.keyText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("المتتالية: $streak 🔥", color = colorScheme.accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
