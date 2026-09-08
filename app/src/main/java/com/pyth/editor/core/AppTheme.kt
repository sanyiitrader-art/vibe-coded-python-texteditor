package com.pyth.editor.core

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val MintPrimary = Color(0xFF65E6B3)
val TopBarColor = Color(0xFF181B1E)
val EditorBg = Color(0xFF111315)
val GutterBg = Color(0xFF0D0F10)
val OutputBg = Color(0xFF0B0D0E)
val PopupBg = Color(0xFF1C2023)
val BorderColor = Color(0xFF292F2D)
val NormalText = Color(0xFFE8ECEA)
val CommentColor = Color(0xFF78857F)
val ErrorColor = Color(0xFFFF5C5C)
val WarningColor = Color(0xFFFFC857)
val SuccessColor = Color(0xFF58D68D)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = MintPrimary,
        onPrimary = Color.Black,
        background = EditorBg,
        surface = TopBarColor,
        onBackground = NormalText,
        onSurface = NormalText,
        surfaceVariant = PopupBg,
        onSurfaceVariant = NormalText,
        error = ErrorColor,
        onError = Color.Black
    )
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}