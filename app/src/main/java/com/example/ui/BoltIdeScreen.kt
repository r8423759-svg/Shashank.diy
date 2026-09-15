package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BoltHeaderBar
import com.example.ui.components.ChatAssistantView
import com.example.ui.components.CodeEditorView
import com.example.ui.components.CreateFileDialog
import com.example.ui.components.FileTreeDrawer
import com.example.ui.components.LivePreviewView
import com.example.ui.components.NewProjectDialog
import com.example.ui.components.ProjectPickerDialog
import com.example.ui.components.TerminalView
import com.example.ui.theme.BoltCyanLight
import com.example.ui.theme.BoltDarkBg
import com.example.ui.theme.BoltDarkBorder
import com.example.ui.theme.BoltDarkElevated
import com.example.ui.theme.BoltDarkSurface
import com.example.ui.theme.BoltEmerald
import com.example.ui.theme.BoltTextPrimary
import com.example.ui.theme.BoltTextSecondary

@Composable
fun BoltIdeScreen(viewModel: BoltIdeViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val terminalEntries by viewModel.terminalEntries.collectAsStateWithLifecycle()

    var showExplorerDrawer by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BoltDarkBg,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BoltDarkBg)
        ) {
            // Header Bar with project switcher & navigation
            BoltHeaderBar(
                currentProject = uiState.currentProject,
                currentViewMode = uiState.viewMode,
                isGenerating = uiState.isGenerating,
                enableHighThinking = uiState.enableHighThinking,
                onViewModeChange = { viewModel.setViewMode(it) },
                onOpenProjectPicker = { viewModel.toggleProjectPicker(true) },
                onOpenNewProjectDialog = { viewModel.toggleNewProjectDialog(true) }
            )

            // Main Active View Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (uiState.viewMode) {
                    IdeViewMode.CHAT -> {
                        ChatAssistantView(
                            messages = chatMessages,
                            isGenerating = uiState.isGenerating,
                            currentStepText = uiState.currentStepText,
                            currentThinkingText = uiState.currentThinkingText,
                            enableHighThinking = uiState.enableHighThinking,
                            selectedModel = uiState.selectedModel,
                            onHighThinkingToggle = { viewModel.setHighThinking(it) },
                            onModelSelect = { viewModel.setModel(it) },
                            onSendPrompt = { viewModel.sendPromptToBolt(it) },
                            onNavigateToCode = { viewModel.setViewMode(IdeViewMode.CODE) }
                        )
                    }

                    IdeViewMode.CODE -> {
                        Row(modifier = Modifier.fillMaxSize()) {
                            // Collapsible File Tree Explorer
                            AnimatedVisibility(
                                visible = showExplorerDrawer,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                FileTreeDrawer(
                                    files = uiState.files,
                                    activeFilePath = uiState.activeFilePath,
                                    onSelectFile = { viewModel.selectFile(it) },
                                    onAddNewFile = { viewModel.toggleCreateFileDialog(true) },
                                    onDeleteFile = { viewModel.deleteFile(it) },
                                    modifier = Modifier.width(200.dp)
                                )
                            }

                            // Code Editor Column
                            Column(modifier = Modifier.weight(1f)) {
                                // Explorer toggle strip
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BoltDarkSurface)
                                        .padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { showExplorerDrawer = !showExplorerDrawer },
                                        modifier = Modifier.size(26.dp).testTag("btn_toggle_explorer")
                                    ) {
                                        Icon(
                                            imageVector = if (showExplorerDrawer) Icons.Default.ChevronLeft else Icons.Default.Folder,
                                            contentDescription = "Toggle Explorer",
                                            tint = if (showExplorerDrawer) BoltCyanLight else BoltTextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (showExplorerDrawer) "Hide Files" else "Files (${uiState.files.size})",
                                        fontSize = 11.sp,
                                        color = BoltTextSecondary
                                    )
                                }

                                CodeEditorView(
                                    activeFilePath = uiState.activeFilePath,
                                    openTabs = uiState.openTabs,
                                    content = uiState.activeFileContent,
                                    hasUnsavedChanges = uiState.hasUnsavedChanges,
                                    onContentChange = { viewModel.updateEditorContent(it) },
                                    onSelectTab = { viewModel.selectFile(it) },
                                    onCloseTab = { viewModel.closeTab(it) },
                                    onSaveFile = { viewModel.saveActiveFile() },
                                    onAskBoltAboutFile = {
                                        viewModel.setViewMode(IdeViewMode.CHAT)
                                        viewModel.sendPromptToBolt("Explain and review ${uiState.activeFilePath}")
                                    }
                                )
                            }
                        }
                    }

                    IdeViewMode.PREVIEW -> {
                        LivePreviewView(
                            files = uiState.files,
                            reloadKey = uiState.previewReloadKey,
                            deviceMode = uiState.previewDeviceMode,
                            consoleLogs = uiState.consoleLogs,
                            onDeviceModeChange = { viewModel.setDeviceMode(it) },
                            onReload = { viewModel.reloadPreview() },
                            onLogReceived = { viewModel.addConsoleLog(it) },
                            onClearLogs = { viewModel.clearConsoleLogs() }
                        )
                    }

                    IdeViewMode.TERMINAL -> {
                        TerminalView(
                            entries = terminalEntries,
                            onExecuteCommand = { viewModel.executeTerminalCommand(it) }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (uiState.showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { viewModel.toggleNewProjectDialog(false) },
            onCreateProject = { name, template ->
                viewModel.createNewProject(name, template)
            }
        )
    }

    if (uiState.showProjectPicker) {
        ProjectPickerDialog(
            projects = uiState.allProjects,
            currentProjectId = uiState.currentProject?.id,
            onDismiss = { viewModel.toggleProjectPicker(false) },
            onSelectProject = { viewModel.selectProject(it) },
            onDeleteProject = { viewModel.deleteCurrentProject() },
            onNewProject = { viewModel.toggleNewProjectDialog(true) }
        )
    }

    if (uiState.showCreateFileDialog) {
        CreateFileDialog(
            onDismiss = { viewModel.toggleCreateFileDialog(false) },
            onCreateFile = { filePath ->
                viewModel.createNewFile(filePath)
            }
        )
    }
}
