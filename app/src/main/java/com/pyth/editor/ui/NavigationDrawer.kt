package com.pyth.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pyth.editor.core.EditorViewModel
import java.io.File

@Composable
fun NavigationDrawer(
    viewModel: EditorViewModel,
    state: EditorViewModel.EditorState,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.95f)
            .background(Color(0xFF1C2023))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Recent Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text("Recent", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                TextButton(onClick = { /* Clear recents */ }) {
                    Text("Clear", color = Color(0xFFFF5C5C), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Recent List
            state.recents.forEach { rec ->
                TextButton(onClick = {
                    viewModel.openFile(File(rec))
                    onClose()
                }) {
                    Text(rec, color = Color(0xFFE8ECEA), modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Root Folder", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Workspace Tree
            val tree = viewModel.fileManager.getWorkspaceTree()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tree) { file ->
                    val indent = if (file.isDirectory) 0 else 16
                    TextButton(
                        onClick = {
                            if (file.isFile) {
                                viewModel.openFile(file)
                                onClose()
                            }
                        },
                        modifier = Modifier.padding(start = indent.dp)
                    ) {
                        Text(
                            text = if (file.isDirectory) "Pythons >" else file.name,
                            color = Color(0xFFE8ECEA)
                        )
                    }
                }
            }
        }
    }
}