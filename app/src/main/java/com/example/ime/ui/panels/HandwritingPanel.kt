package com.example.ime.ui.panels

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme

@Composable
fun HandwritingPanel(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onTextInput: (String) -> Unit,
    onClose: () -> Unit
) {
    val paths = remember { mutableStateListOf<List<Offset>>() }
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }

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
                text = "✍️ الكتابة اليدوية الذكية",
                color = colorScheme.accent,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(
                    onClick = {
                        paths.clear()
                        currentPath = emptyList()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "مسح", tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = colorScheme.keyText, modifier = Modifier.size(18.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(colorScheme.keyBackground)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = listOf(offset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPath = currentPath + change.position
                        },
                        onDragEnd = {
                            if (currentPath.isNotEmpty()) {
                                paths.add(currentPath)
                                currentPath = emptyList()
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                for (p in paths) {
                    if (p.size > 1) {
                        val path = Path().apply {
                            moveTo(p.first().x, p.first().y)
                            for (point in p.drop(1)) {
                                lineTo(point.x, point.y)
                            }
                        }
                        drawPath(path, color = colorScheme.accent, style = stroke)
                    }
                }
                if (currentPath.size > 1) {
                    val path = Path().apply {
                        moveTo(currentPath.first().x, currentPath.first().y)
                        for (point in currentPath.drop(1)) {
                            lineTo(point.x, point.y)
                        }
                    }
                    drawPath(path, color = colorScheme.accent, style = stroke)
                }
            }

            if (paths.isEmpty() && currentPath.isEmpty()) {
                Text(
                    text = "ارسم الحرف أو الكلمة هنا بإصبعك...",
                    color = colorScheme.keyText.copy(alpha = 0.4f),
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("أ", "ب", "ج", "د", "ر", "س", "ص").forEach { word ->
                Button(
                    onClick = { onTextInput(word) },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.keyBackground),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(word, color = colorScheme.keyText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
