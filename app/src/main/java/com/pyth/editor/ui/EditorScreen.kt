package com.pyth.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.pyth.editor.core.EditorBg
import com.pyth.editor.core.EditorViewModel
import com.pyth.editor.ui.components.CodeEditor
import com.pyth.editor.ui.components.PythonToolbar
import kotlinx.coroutines.launch

@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsState().value
    val output = viewModel.output.collectAsState().value
    val isRunning = viewModel.isRunning.collectAsState().value
    
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    var outputHeightPx by remember { mutableStateOf(screenHeightPx * 0.25f) }
    
    val imeBottomPx = with(density) { WindowInsets.ime.getBottom(density).toFloat() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawer(viewModel = viewModel, state = state, onClose = {
                    scope.launch { drawerState.close() }
                })
            }
        }
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopBar(
                    fileName = state.activeFile?.name ?: "main.py",
                    isRunning = isRunning,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onRunClick = { 
                        if (isRunning) viewModel.stopCode() else viewModel.runCode() 
                    },
                    onMoreClick = { /* TODO: Show Popup */ }
                )
                
                Box(modifier = Modifier.weight(1f).fillMaxWidth().background(EditorBg)) {
                    if (!state.isFullscreenOutput) {
                        CodeEditor(
                            modifier = Modifier.fillMaxSize(),
                            initialText = state.fileContent,
                            onTextChanged = { text, cursor ->
                                viewModel.onTextChanged(text, cursor)
                            }
                        )
                    }
                }
            }
            
            // Python Toolbar above keyboard
            if (imeBottomPx > 0 && !state.isFullscreenOutput) {
                PythonToolbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset { IntOffset(0, -imeBottomPx.toInt()) },
                    onInsert = { /* TODO */ }
                )
            }
            
            // Output Panel
            if (!state.isFullscreenOutput && state.isOutputOpen) {
                OutputPanel(
                    output = output,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(with(density) { outputHeightPx.toDp() })
                        .background(EditorBg),
                    onDrag = { dragAmount ->
                        outputHeightPx -= dragAmount
                        outputHeightPx = outputHeightPx.coerceIn(screenHeightPx * 0.05f, screenHeightPx - 56.dp.toPx())
                    }
                )
            }
            
            // Fullscreen Output / Editor Pager
            if (state.isFullscreenOutput) {
                val pagerState = rememberPagerState(pageCount = { 2 })
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    if (page == 0) {
                        CodeEditor(
                            modifier = Modifier.fillMaxSize(),
                            initialText = state.fileContent,
                            onTextChanged = { text, cursor -> viewModel.onTextChanged(text, cursor) }
                        )
                    } else {
                        OutputPanel(
                            output = output,
                            modifier = Modifier.fillMaxSize(),
                            onDrag = { dragAmount ->
                                if (dragAmount > 10f) {
                                    viewModel.setFullscreenOutput(false)
                                    outputHeightPx = screenHeightPx * 0.25f
                                }
                            }
                        )
                    }
                }
            }
            
            if (state.showRecoveryDialog) {
                RecoveryDialog(viewModel = viewModel, state = state)
            }
        }
    }
}