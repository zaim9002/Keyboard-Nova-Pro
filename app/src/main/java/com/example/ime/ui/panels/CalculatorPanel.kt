package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme

@Composable
fun CalculatorPanel(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onInsertResult: (String) -> Unit,
    onClose: () -> Unit
) {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }

    fun calculate(expr: String): String {
        return try {
            val sanitized = expr.replace("×", "*").replace("÷", "/")
            if (sanitized.isBlank()) return "0"
            val tokens = ArrayList<String>()
            var currentNum = StringBuilder()
            for (ch in sanitized) {
                if (ch in "+-*/") {
                    if (currentNum.isNotEmpty()) {
                        tokens.add(currentNum.toString())
                        currentNum = StringBuilder()
                    }
                    tokens.add(ch.toString())
                } else if (ch.isDigit() || ch == '.') {
                    currentNum.append(ch)
                }
            }
            if (currentNum.isNotEmpty()) tokens.add(currentNum.toString())
            if (tokens.isEmpty()) return "0"
            var acc = tokens[0].toDoubleOrNull() ?: 0.0
            var i = 1
            while (i < tokens.size - 1) {
                val op = tokens[i]
                val nextVal = tokens[i + 1].toDoubleOrNull() ?: 0.0
                when (op) {
                    "+" -> acc += nextVal
                    "-" -> acc -= nextVal
                    "*" -> acc *= nextVal
                    "/" -> if (nextVal != 0.0) acc /= nextVal
                }
                i += 2
            }
            if (acc % 1.0 == 0.0) acc.toLong().toString() else "%.2f".format(acc)
        } catch (e: Exception) {
            "خطأ"
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🧮 آلة حاسبة سريعة",
                color = colorScheme.accent,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = {
                        if (result != "خطأ" && result != "0") {
                            onInsertResult(result)
                        } else if (expression.isNotBlank()) {
                            onInsertResult(expression)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.accent),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        Icons.Default.ContentPaste,
                        contentDescription = "إدراج الناتج",
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("إدراج", fontSize = 11.sp, color = Color.White)
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = colorScheme.keyText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.keyBackground.copy(alpha = 0.8f))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Column(horizontalAlignment = Alignment.End) {
                if (expression.isNotEmpty()) {
                    Text(
                        text = expression,
                        color = colorScheme.keyText.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
                Text(
                    text = result,
                    color = colorScheme.keyText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        val buttonGrid = listOf(
            listOf("C", "(", ")", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "-"),
            listOf("1", "2", "3", "+"),
            listOf("0", ".", "⌫", "=")
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (row in buttonGrid) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (btn in row) {
                        val isOp = btn in listOf("÷", "×", "-", "+", "=")
                        val isSpecial = btn in listOf("C", "⌫", "(", ")")
                        val btnBg = when {
                            btn == "=" -> colorScheme.accent
                            isOp -> colorScheme.accent.copy(alpha = 0.2f)
                            isSpecial -> colorScheme.keyBackground.copy(alpha = 0.6f)
                            else -> colorScheme.keyBackground
                        }
                        val btnText = when {
                            btn == "=" -> Color.White
                            isOp -> colorScheme.accent
                            else -> colorScheme.keyText
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .background(btnBg)
                                .clickable {
                                    when (btn) {
                                        "C" -> {
                                            expression = ""
                                            result = "0"
                                        }
                                        "⌫" -> {
                                            if (expression.isNotEmpty()) {
                                                expression = expression.dropLast(1)
                                                result = if (expression.isEmpty()) "0" else calculate(expression)
                                            }
                                        }
                                        "=" -> {
                                            result = calculate(expression)
                                        }
                                        else -> {
                                            expression += btn
                                            result = calculate(expression)
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = btn,
                                color = btnText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
