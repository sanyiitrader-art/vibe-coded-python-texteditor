package com.pyth.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pyth.editor.core.BorderColor
import com.pyth.editor.core.MintPrimary

@Composable
fun TopBar(
    fileName: String,
    isRunning: Boolean,
    onMenuClick: () -> Unit,
    onRunClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF181B1E))
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text("☰", color = Color.White, fontSize = 24.sp)
        }
        
        Text(
            text = fileName,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.Center)
        )
        
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRunClick) {
                Text(
                    text = if (isRunning) "■" else "▶",
                    color = if (isRunning) Color(0xFFFF5C5C) else MintPrimary,
                    fontSize = 20.sp
                )
            }
            IconButton(onClick = onMoreClick) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderColor)
                .align(Alignment.BottomCenter)
        )
    }
}