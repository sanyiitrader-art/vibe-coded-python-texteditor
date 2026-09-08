package com.pyth.editor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.pyth.editor.core.EditorViewModel

@Composable
fun UnsavedExitDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onDiscard: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Exit") },
        text = { Text("Changes aren't saved, are you sure to exit?") },
        confirmButton = {
            Row {
                TextButton(onClick = onSave) { Text("Save") }
                TextButton(onClick = onDiscard) { Text("Discard") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun RecoveryDialog(
    viewModel: EditorViewModel,
    state: EditorViewModel.EditorState
) {
    AlertDialog(
        onDismissRequest = { viewModel.restoreRecovery(false) },
        title = { Text("Recovery") },
        text = { Text("Unsaved work recovered") },
        confirmButton = {
            TextButton(onClick = { viewModel.restoreRecovery(true) }) { Text("Restore") }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.restoreRecovery(false) }) { Text("Discard") }
        }
    )
}