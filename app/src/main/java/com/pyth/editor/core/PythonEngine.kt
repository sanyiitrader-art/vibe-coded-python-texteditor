package com.pyth.editor.core

import android.content.Context
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PythonEngine(context: Context) {
    init {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
        }
    }

    private val py = Python.getInstance()
    private var runningThread: Thread? = null

    private val _output = MutableStateFlow<List<String>>(emptyList())
    val output: StateFlow<List<String>> = _output.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    fun clearOutput() {
        _output.value = emptyList()
    }

    fun appendOutput(text: String) {
        _output.value = _output.value + text.split("\n").filter { it.isNotEmpty() }
    }

    fun compile(code: String) {
        if (_isRunning.value) return
        _isRunning.value = true
        _output.value = emptyList()
        
        Thread {
            try {
                // Use py_compile to check for syntax errors
                val builtins = py.getModule("builtins")
                builtins.callAttr("compile", code, "<string>", "exec")
                appendOutput("Success: Code compiled with no syntax errors.")
            } catch (e: Exception) {
                appendOutput("SyntaxError: ${e.message}")
            } finally {
                _isRunning.value = false
            }
        }.start()
    }

    fun run(code: String) {
        if (_isRunning.value) return
        _isRunning.value = true
        _output.value = emptyList()
        appendOutput("$ python main.py\n")

        runningThread = Thread {
            try {
                val builtins = py.getModule("builtins")
                // Execute the code dynamically
                builtins.callAttr("exec", code)
                appendOutput("\n[Process finished]")
            } catch (e: Exception) {
                appendOutput("\nError: ${e.message}")
            } finally {
                _isRunning.value = false
            }
        }
        runningThread?.start()
    }

    fun stop() {
        if (!_isRunning.value) return
        runningThread?.interrupt()
        _isRunning.value = false
        appendOutput("\n[Process stopped by user]")
    }
}