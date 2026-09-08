package com.pyth.editor.core

import android.content.Context
import org.json.JSONArray
import java.io.File

class FileManager(context: Context) {
    private val workspaceDir = File(context.filesDir, "Pythons")
    private val recentsFile = File(context.filesDir, "recents.json")
    private val recoveryDir = File(context.cacheDir, "recovery")

    init {
        workspaceDir.mkdirs()
        recoveryDir.mkdirs()
        // Create a default file if workspace is empty
        if (workspaceDir.listFiles()?.isEmpty() != false) {
            File(workspaceDir, "main.py").writeText("# Welcome to Pyth Editor\nprint(\"Hello, Android!\")\n")
        }
    }

    fun getWorkspaceTree(): List<File> {
        return workspaceDir.walk().filter { it.extension == "py" || it.isDirectory }.toList()
    }

    fun getRootFolder(): File = workspaceDir

    fun getFile(name: String): File {
        return File(workspaceDir, name)
    }

    fun readFile(file: File): String {
        return if (file.exists()) file.readText() else ""
    }

    fun saveFile(file: File, content: String) {
        file.writeText(content)
        // Clear recovery if saved
        getRecoveryFile(file)?.delete()
    }

    fun saveAs(fileName: String, content: String): File {
        val safeName = if (fileName.endsWith(".py")) fileName else "$fileName.py"
        val newFile = File(workspaceDir, safeName)
        newFile.writeText(content)
        return newFile
    }

    // --- Recents ---
    fun getRecents(): List<String> {
        if (!recentsFile.exists()) return emptyList()
        val arr = JSONArray(recentsFile.readText())
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            list.add(arr.getString(i))
        }
        return list
    }

    fun addRecent(fileName: String) {
        val list = getRecents().toMutableList()
        list.remove(fileName)
        list.add(0, fileName)
        // Keep only last 10
        if (list.size > 10) list.subList(10, list.size).clear()
        
        val arr = JSONArray()
        list.forEach { arr.put(it) }
        recentsFile.writeText(arr.toString())
    }

    fun clearRecents() {
        recentsFile.delete()
    }

    // --- Crash Recovery ---
    private fun getRecoveryFile(file: File): File? {
        val safePath = file.nameWithoutExtension + "_rec.py"
        return File(recoveryDir, safePath)
    }

    fun saveRecovery(file: File, content: String) {
        getRecoveryFile(file)?.writeText(content)
    }

    fun checkRecovery(file: File): String? {
        val recFile = getRecoveryFile(file) ?: return null
        if (!recFile.exists()) return null
        
        val recContent = recFile.readText()
        val diskContent = if (file.exists()) file.readText() else ""
        
        return if (recContent != diskContent) recContent else null
    }

    fun clearRecovery(file: File) {
        getRecoveryFile(file)?.delete()
    }
}