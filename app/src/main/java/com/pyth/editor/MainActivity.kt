package com.pyth.editor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pyth.editor.core.AppTheme
import com.pyth.editor.core.EditorViewModel
import com.pyth.editor.ui.EditorScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val viewModel = EditorViewModel(applicationContext)
        
        setContent {
            AppTheme {
                EditorScreen(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF111315))
                )
            }
        }
    }
}