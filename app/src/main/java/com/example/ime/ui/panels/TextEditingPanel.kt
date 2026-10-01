package com.example.ime.ui.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.theme.KeyboardColorScheme

@Composable
fun TextEditingPanel(
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onMoveCursor: (Int) -> Unit,
    onSelectAll: () -> Unit,
    onCut: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onHome: () -> Unit,
    onEnd: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = " تحريك المؤشر والتحكم في النص",
                color = colorScheme.accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            EditActionButton("تحديد الكل", Icons.Default.SelectAll, Modifier.weight(1f), colorScheme, onSelectAll)
            EditActionButton("قص", Icons.Default.ContentCut, Modifier.weight(1f), colorScheme, onCut)
            EditActionButton("نسخ", Icons.Default.ContentCopy, Modifier.weight(1f), colorScheme, onCopy)
            EditActionButton("لصق", Icons.Default.ContentPaste, Modifier.weight(1f), colorScheme, onPaste)
            EditActionButton("تراجع", Icons.Default.Undo, Modifier.weight(1f), colorScheme, onUndo)
            EditActionButton("إعادة", Icons.Default.Redo, Modifier.weight(1f), colorScheme, onRedo)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onHome,
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.keyBackground),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("بداية السطر", color = colorScheme.keyText, fontSize = 11.sp)
                }
                Button(
                    onClick = onEnd,
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.keyBackground),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("نهاية السطر", color = colorScheme.keyText, fontSize = 11.sp)
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DirectionButton(Icons.Default.KeyboardArrowUp, colorScheme) { onMoveCursor(-50) }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DirectionButton(Icons.Default.KeyboardArrowLeft, colorScheme) { onMoveCursor(-1) }
                    DirectionButton(Icons.Default.KeyboardArrowRight, colorScheme) { onMoveCursor(1) }
                }
                DirectionButton(Icons.Default.KeyboardArrowDown, colorScheme) { onMoveCursor(50) }
            }
        }
    }
}

@Composable
private fun DirectionButton(
    icon: ImageVector,
    colorScheme: KeyboardColorScheme,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colorScheme.keyBackground)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.accent,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun EditActionButton(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    colorScheme: KeyboardColorScheme,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colorScheme.keyBackground)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = colorScheme.accent,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            color = colorScheme.keyText,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
