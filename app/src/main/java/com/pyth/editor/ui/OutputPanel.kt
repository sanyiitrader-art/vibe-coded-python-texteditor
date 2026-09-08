package com.pyth.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pyth.editor.core.OutputBg

@Composable
fun OutputPanel(
    output: List<String>,
    modifier: Modifier = Modifier,
    onDrag: (Float) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(OutputBg)
    ) {
        // Handle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.y)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFF78857F), RoundedCornerShape(2.dp))
            )
        }
        
        Text("OUTPUT", color = Color(0xFF65E6B3), modifier = Modifier.padding(8.dp), fontSize = 14.sp)
        
        LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            items(output) { line ->
                Text(line, color = Color(0xFFE8ECEA), fontSize = 14.sp)
            }
        }
    }
}