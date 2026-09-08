package com.pyth.editor.core

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class EditorViewModel(application: Context) : AndroidViewModel(application) {
    private val fileManager = FileManager(application)
    private val pythonEngine = PythonEngine(application)

    data class EditorState(
        val activeFile: File? = null,
        val fileContent: String = "",
        val cursorPosition: Int = 0,
        val recents: List<String> = emptyList(),
        val isDrawerOpen: Boolean = false,
        val isOutputOpen: Boolean = false,
        val isFullscreenOutput: Boolean = false,
        val isRunning: Boolean = false,
        val showRecoveryDialog: Boolean = false,
        val recoveryContent: String? = null,
        val errorLine: Int? = null
    )

    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state.asStateFlow()

    val output = pythonEngine.output
    val isRunning = pythonEngine.isRunning

    init {
        loadRecents()
        // Try to load the first available file initially
        val tree = fileManager.getWorkspaceTree().filter { it.isFile }
        if (tree.isNotEmpty()) {
            openFile(tree.first())
        }
    }

    fun toggleDrawer() {
        _state.value = _state.value.copy(isDrawerOpen = !_state.value.isDrawerOpen)
    }

    fun closeDrawer() {
        _state.value = _state.value.copy(isDrawerOpen = false)
    }

    private fun loadRecents() {
        _state.value = _state.value.copy(recents = fileManager.getRecents())
    }

    fun openFile(file: File) {
        // Check if there's unsaved recovery state
        val recovery = fileManager.checkRecovery(file)
        if (recovery != null) {
            _state.value = _state.value.copy(
                activeFile = file,
                showRecoveryDialog = true,
                recoveryContent = recovery,
                isDrawerOpen = false
            )
        } else {
            val content = fileManager.readFile(file)
            fileManager.addRecent(file.name)
            loadRecents()
            _state.value = _state.value.copy(
                activeFile = file,
                fileContent = content,
                isDrawerOpen = false,
                errorLine = null
            )
        }
    }

    fun restoreRecovery(restore: Boolean) {
        val recContent = _state.value.recoveryContent
        val active = _state.value.activeFile
        if (restore && recContent != null && active != null) {
            _state.value = _state.value.copy(
                fileContent = recContent,
                showRecoveryDialog = false,
                recoveryContent = null
            )
        } else {
            active?.let { 
                val content = fileManager.readFile(it)
                _state.value = _state.value.copy(
                    fileContent = content,
                    showRecoveryDialog = false,
                    recoveryContent = null
                )
            }
        }
    }

    fun onTextChanged(newText: String, cursor: Int) {
        _state.value = _state.value.copy(fileContent = newText, cursorPosition = cursor)
        _state.value.activeFile?.let { 
            fileManager.saveRecovery(it, newText)
        }
    }

    fun saveCurrentFile() {
        _state.value.activeFile?.let { file ->
            fileManager.saveFile(file, _state.value.fileContent)
        }
    }

    fun saveAs(fileName: String) {
        val newFile = fileManager.saveAs(fileName, _state.value.fileContent)
        fileManager.addRecent(newFile.name)
        loadRecents()
        _state.value = _state.value.copy(activeFile = newFile)
    }

    fun runCode() {
        _state.value = _state.value.copy(isOutputOpen = true, errorLine = null)
        pythonEngine.clearOutput()
        pythonEngine.run(_state.value.fileContent)
    }

    fun compileCode() {
        pythonEngine.clearOutput()
        pythonEngine.compile(_state.value.fileContent)
        _state.value = _state.value.copy(isOutputOpen = true)
    }

    fun stopCode() {
        pythonEngine.stop()
    }

    fun toggleOutputOpen() {
        _state.value = _state.value.copy(isOutputOpen = !_state.value.isOutputOpen)
    }
    
    fun setFullscreenOutput(fullscreen: Boolean) {
        _state.value = _state.value.copy(isFullscreenOutput = fullscreen)
    }

    companion object {
        class Factory(private val context: Context) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
                    return EditorViewModel(context.applicationContext as Application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
                return EditorViewModel(context.applicationContext as Application) as T
            }
        }
    }
}