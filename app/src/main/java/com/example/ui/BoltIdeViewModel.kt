package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BoltDatabase
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectFileEntity
import com.example.data.local.entity.TerminalEntryEntity
import com.example.data.repository.BoltRepository
import com.example.data.template.DefaultTemplates
import com.example.data.template.TemplateDefinition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class IdeViewMode {
    CHAT,
    CODE,
    PREVIEW,
    TERMINAL
}

enum class PreviewDeviceMode {
    MOBILE,
    TABLET,
    DESKTOP
}

data class IdeUiState(
    val currentProject: ProjectEntity? = null,
    val allProjects: List<ProjectEntity> = emptyList(),
    val files: List<ProjectFileEntity> = emptyList(),
    val activeFilePath: String = "src/App.jsx",
    val openTabs: List<String> = listOf("src/App.jsx", "index.html"),
    val activeFileContent: String = "",
    val hasUnsavedChanges: Boolean = false,
    val viewMode: IdeViewMode = IdeViewMode.CODE,
    val isGenerating: Boolean = false,
    val currentThinkingText: String? = null,
    val currentStepText: String? = null,
    val enableHighThinking: Boolean = true,
    val selectedModel: String = "gemini-3.1-pro-preview",
    val previewDeviceMode: PreviewDeviceMode = PreviewDeviceMode.DESKTOP,
    val previewReloadKey: Int = 0,
    val consoleLogs: List<String> = emptyList(),
    val showNewProjectDialog: Boolean = false,
    val showProjectPicker: Boolean = false,
    val showCreateFileDialog: Boolean = false,
    val statusMessage: String? = null
)

class BoltIdeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BoltRepository

    private val _uiState = MutableStateFlow(IdeUiState())
    val uiState: StateFlow<IdeUiState> = _uiState.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _chatMessages.asStateFlow()

    private val _terminalEntries = MutableStateFlow<List<TerminalEntryEntity>>(emptyList())
    val terminalEntries: StateFlow<List<TerminalEntryEntity>> = _terminalEntries.asStateFlow()

    init {
        val database = BoltDatabase.getDatabase(application)
        repository = BoltRepository(database.boltDao())

        // Start observing projects & setup default
        viewModelScope.launch {
            repository.allProjects.collectLatest { projects ->
                _uiState.value = _uiState.value.copy(allProjects = projects)
            }
        }

        viewModelScope.launch {
            val defaultProject = repository.initializeDefaultProjectIfEmpty()
            selectProject(defaultProject.id)
        }
    }

    fun selectProject(projectId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(showProjectPicker = false)

            repository.observeProject(projectId).collectLatest { proj ->
                if (proj != null) {
                    _uiState.value = _uiState.value.copy(
                        currentProject = proj,
                        activeFilePath = proj.activeFilePath
                    )
                }
            }
        }

        // Observe files
        viewModelScope.launch {
            repository.getFiles(projectId).collectLatest { files ->
                val currentActivePath = _uiState.value.activeFilePath
                val activeFile = files.find { it.filePath == currentActivePath } ?: files.firstOrNull()
                val tabs = if (_uiState.value.openTabs.isEmpty() && activeFile != null) {
                    listOf(activeFile.filePath)
                } else _uiState.value.openTabs

                _uiState.value = _uiState.value.copy(
                    files = files,
                    activeFilePath = activeFile?.filePath ?: "index.html",
                    activeFileContent = activeFile?.content ?: "",
                    openTabs = tabs,
                    hasUnsavedChanges = false,
                    previewReloadKey = _uiState.value.previewReloadKey + 1
                )
            }
        }

        // Observe chat
        viewModelScope.launch {
            repository.getMessages(projectId).collectLatest { msgs ->
                _chatMessages.value = msgs
            }
        }

        // Observe terminal
        viewModelScope.launch {
            repository.getTerminalEntries(projectId).collectLatest { entries ->
                _terminalEntries.value = entries
            }
        }
    }

    fun setViewMode(mode: IdeViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = mode)
    }

    fun selectFile(filePath: String) {
        val file = _uiState.value.files.find { it.filePath == filePath }
        val currentTabs = _uiState.value.openTabs.toMutableList()
        if (!currentTabs.contains(filePath)) {
            currentTabs.add(filePath)
        }

        _uiState.value = _uiState.value.copy(
            activeFilePath = filePath,
            activeFileContent = file?.content ?: "",
            openTabs = currentTabs,
            hasUnsavedChanges = false
        )

        val proj = _uiState.value.currentProject ?: return
        viewModelScope.launch {
            repository.setActiveFile(proj.id, filePath)
        }
    }

    fun closeTab(filePath: String) {
        val currentTabs = _uiState.value.openTabs.filter { it != filePath }
        val nextActive = if (_uiState.value.activeFilePath == filePath) {
            currentTabs.lastOrNull() ?: _uiState.value.files.firstOrNull()?.filePath ?: ""
        } else {
            _uiState.value.activeFilePath
        }
        val file = _uiState.value.files.find { it.filePath == nextActive }

        _uiState.value = _uiState.value.copy(
            openTabs = currentTabs,
            activeFilePath = nextActive,
            activeFileContent = file?.content ?: "",
            hasUnsavedChanges = false
        )
    }

    fun updateEditorContent(newContent: String) {
        _uiState.value = _uiState.value.copy(
            activeFileContent = newContent,
            hasUnsavedChanges = true
        )
    }

    fun saveActiveFile() {
        val proj = _uiState.value.currentProject ?: return
        val path = _uiState.value.activeFilePath
        val content = _uiState.value.activeFileContent

        viewModelScope.launch {
            repository.saveFileContent(proj.id, path, content)
            _uiState.value = _uiState.value.copy(
                hasUnsavedChanges = false,
                previewReloadKey = _uiState.value.previewReloadKey + 1,
                statusMessage = "Saved $path"
            )
        }
    }

    fun createNewFile(filePath: String, initialContent: String = "") {
        val proj = _uiState.value.currentProject ?: return
        viewModelScope.launch {
            repository.createNewFile(proj.id, filePath, initialContent)
            selectFile(filePath)
            _uiState.value = _uiState.value.copy(
                showCreateFileDialog = false,
                statusMessage = "Created $filePath"
            )
        }
    }

    fun deleteFile(filePath: String) {
        val proj = _uiState.value.currentProject ?: return
        viewModelScope.launch {
            repository.deleteFile(proj.id, filePath)
            closeTab(filePath)
        }
    }

    fun createNewProject(name: String, template: TemplateDefinition) {
        viewModelScope.launch {
            val project = repository.createProjectFromTemplate(template, name)
            selectProject(project.id)
            _uiState.value = _uiState.value.copy(
                showNewProjectDialog = false,
                statusMessage = "Project '${project.name}' created"
            )
        }
    }

    fun deleteCurrentProject() {
        val proj = _uiState.value.currentProject ?: return
        viewModelScope.launch {
            repository.deleteProject(proj.id)
            val remaining = repository.allProjects.stateIn(viewModelScope).value
            if (remaining.isNotEmpty()) {
                selectProject(remaining.first().id)
            } else {
                val newProj = repository.initializeDefaultProjectIfEmpty()
                selectProject(newProj.id)
            }
        }
    }

    fun setHighThinking(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableHighThinking = enabled)
    }

    fun setModel(model: String) {
        _uiState.value = _uiState.value.copy(selectedModel = model)
    }

    fun setDeviceMode(mode: PreviewDeviceMode) {
        _uiState.value = _uiState.value.copy(previewDeviceMode = mode)
    }

    fun reloadPreview() {
        _uiState.value = _uiState.value.copy(
            previewReloadKey = _uiState.value.previewReloadKey + 1,
            statusMessage = "Preview refreshed"
        )
    }

    fun addConsoleLog(log: String) {
        val updated = (_uiState.value.consoleLogs + log).takeLast(100)
        _uiState.value = _uiState.value.copy(consoleLogs = updated)
    }

    fun clearConsoleLogs() {
        _uiState.value = _uiState.value.copy(consoleLogs = emptyList())
    }

    fun executeTerminalCommand(command: String) {
        val proj = _uiState.value.currentProject ?: return
        viewModelScope.launch {
            repository.executeTerminalCommand(proj.id, command)
        }
    }

    fun sendPromptToBolt(prompt: String) {
        val proj = _uiState.value.currentProject ?: return
        if (prompt.isBlank() || _uiState.value.isGenerating) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGenerating = true,
                currentStepText = "⚡ Bolt AI Reasoning with Gemini 3.1 Pro (Thinking: HIGH)...",
                currentThinkingText = "Analyzing architecture, dependencies, and generating files..."
            )

            val result = repository.askBoltAi(
                projectId = proj.id,
                userPrompt = prompt,
                model = _uiState.value.selectedModel,
                enableHighThinking = _uiState.value.enableHighThinking
            )

            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                currentStepText = null,
                currentThinkingText = null,
                previewReloadKey = _uiState.value.previewReloadKey + 1,
                statusMessage = if (result.isSuccess) "Files generated & applied!" else result.errorMessage
            )
        }
    }

    fun toggleNewProjectDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showNewProjectDialog = show)
    }

    fun toggleProjectPicker(show: Boolean) {
        _uiState.value = _uiState.value.copy(showProjectPicker = show)
    }

    fun toggleCreateFileDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreateFileDialog = show)
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }
}
