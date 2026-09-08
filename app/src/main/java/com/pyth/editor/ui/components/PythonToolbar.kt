package com.pyth.editor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pyth.editor.core.BorderColor

@Composable
fun PythonToolbar(
    modifier: Modifier = Modifier,
    onInsert: (String) -> Unit
) {
    val symbols = listOf(
        "↶", "↷", "⇥", "⇤", "(", ")", "[", "]", "{", "}", "'", "\"", 
        ":", "_", "#", "=", "==", "!=", "<", ">", "<=", ">=", "+", "-", 
        "*", "/", "//", "%", "**", "+=", "-=", "*=", "/=", "->", "@", "..."
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color(0xFF181B1E))
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        symbols.forEach { sym ->
            TextButton(
                onClick = { onInsert(sym) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                modifier = Modifier
                    .height(40.dp)
            ) {
                Text(
                    text = sym,
                    color = Color(0xFF65E6B3),
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(1.dp).height(20.dp).background(BorderColor))
        }
    }
}