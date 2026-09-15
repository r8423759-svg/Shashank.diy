package com.example.data.repository

import com.example.data.api.GeminiGenerationResult
import com.example.data.api.GeminiService
import com.example.data.local.dao.BoltDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectFileEntity
import com.example.data.local.entity.TerminalEntryEntity
import com.example.data.parser.ActionType
import com.example.data.parser.BoltArtifactParser
import com.example.data.template.DefaultTemplates
import com.example.data.template.TemplateDefinition
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class BoltRepository(
    private val dao: BoltDao,
    private val geminiService: GeminiService = GeminiService()
) {

    val allProjects: Flow<List<ProjectEntity>> = dao.getAllProjects()

    suspend fun initializeDefaultProjectIfEmpty(): ProjectEntity {
        val projects = dao.getAllProjects().firstOrNull() ?: emptyList()
        if (projects.isNotEmpty()) {
            return projects.first()
        }
        return createProjectFromTemplate(DefaultTemplates.REACT_SAAS)
    }

    suspend fun createProjectFromTemplate(template: TemplateDefinition, customName: String? = null): ProjectEntity {
        val projectId = UUID.randomUUID().toString().take(8)
        val project = ProjectEntity(
            id = projectId,
            name = customName ?: template.name,
            description = template.description,
            activeFilePath = template.defaultActiveFile,
            template = template.id,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        dao.insertProject(project)

        val fileEntities = template.initialFiles.map { (path, content) ->
            ProjectFileEntity(
                projectId = projectId,
                filePath = path,
                content = content,
                language = BoltArtifactParser.detectLanguage(path),
                isDirectory = false,
                updatedAt = System.currentTimeMillis()
            )
        }
        dao.insertOrUpdateFiles(fileEntities)

        // Initial welcome chat message
        val welcomeMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            role = "assistant",
            content = "⚡ **Bolt.diy Workspace Initialized**\nProject **${project.name}** scaffolded successfully with ${fileEntities.size} files.\n\nAsk Bolt AI anything to generate new features, full-stack APIs, or modify components! High-Thinking reasoning mode is active (Gemini 3.1 Pro Preview).",
            thoughtProcess = "Project workspace initialized with template '${template.name}'. Virtual file system mounted: ${fileEntities.map { it.filePath }.joinToString(", ")}. Live preview and Node.js mock container ready on port 5173.",
            thinkingDurationSec = 2,
            hasArtifact = true
        )
        dao.insertMessage(welcomeMsg)

        // Initial terminal welcome
        val termEntry = TerminalEntryEntity(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            command = "npm run dev",
            output = """
                > vite dev --port 5173
                
                VITE v5.4.2 ready in 218 ms
                
                ➜  Local:   http://localhost:5173/
                ➜  Network: use --host to expose
                ➜  press h + enter to show help
                
                [Bolt Container] Watching for file changes...
            """.trimIndent(),
            status = "success"
        )
        dao.insertTerminalEntry(termEntry)

        return project
    }

    fun observeProject(projectId: String): Flow<ProjectEntity?> = dao.observeProject(projectId)

    fun getFiles(projectId: String): Flow<List<ProjectFileEntity>> = dao.getFilesForProject(projectId)

    suspend fun getFile(projectId: String, filePath: String): ProjectFileEntity? = dao.getFile(projectId, filePath)

    suspend fun saveFileContent(projectId: String, filePath: String, content: String) {
        val language = BoltArtifactParser.detectLanguage(filePath)
        val fileEntity = ProjectFileEntity(
            projectId = projectId,
            filePath = filePath,
            content = content,
            language = language,
            isDirectory = false,
            updatedAt = System.currentTimeMillis()
        )
        dao.insertOrUpdateFile(fileEntity)
    }

    suspend fun createNewFile(projectId: String, filePath: String, initialContent: String = "") {
        saveFileContent(projectId, filePath, initialContent)
    }

    suspend fun deleteFile(projectId: String, filePath: String) {
        dao.deleteFile(projectId, filePath)
    }

    suspend fun setActiveFile(projectId: String, filePath: String) {
        val project = dao.getProjectById(projectId) ?: return
        dao.updateProject(project.copy(activeFilePath = filePath, updatedAt = System.currentTimeMillis()))
    }

    fun getMessages(projectId: String): Flow<List<ChatMessageEntity>> = dao.getMessagesForProject(projectId)

    fun getTerminalEntries(projectId: String): Flow<List<TerminalEntryEntity>> = dao.getTerminalEntries(projectId)

    suspend fun executeTerminalCommand(projectId: String, command: String): String {
        val trimmed = command.trim()
        val entryId = UUID.randomUUID().toString()

        val output = when {
            trimmed == "help" -> """
                Bolt Container Shell v2.4
                Available commands:
                  npm run dev        - Start local development server on port 5173
                  npm install [pkg]  - Install virtual dependencies
                  node [file]        - Execute virtual Node.js script
                  ls [-la]           - List files in virtual filesystem
                  cat [file]         - View file contents
                  clear              - Clear terminal output
                  git status         - Check current git tree status
                  curl [url]         - Simulated network query
            """.trimIndent()

            trimmed == "clear" -> {
                dao.clearTerminal(projectId)
                return "Cleared"
            }

            trimmed.startsWith("npm install") || trimmed.startsWith("npm i ") -> {
                val pkg = trimmed.removePrefix("npm install").removePrefix("npm i").trim().ifBlank { "dependencies" }
                """
                added 24 packages in 1.42s
                + $pkg (virtual node_modules cached)
                0 vulnerabilities found
                """.trimIndent()
            }

            trimmed == "npm run dev" || trimmed == "npm start" -> {
                """
                > vite dev --port 5173
                VITE v5.4.2 ready in 184 ms
                ➜  Local:   http://localhost:5173/
                [Bolt Live Sync] Hot Module Replacement (HMR) Active.
                """.trimIndent()
            }

            trimmed.startsWith("node ") -> {
                val scriptPath = trimmed.removePrefix("node").trim()
                val file = dao.getFile(projectId, scriptPath)
                if (file != null) {
                    """
                    [Node Runtime v20.12.0] Executing $scriptPath...
                    [Bolt Server] Service initialized. Listening on http://localhost:3000
                    """.trimIndent()
                } else {
                    "Error: Cannot find module '$scriptPath'"
                }
            }

            trimmed == "ls" || trimmed == "ls -la" -> {
                val files = dao.getFilesForProject(projectId).firstOrNull() ?: emptyList()
                val sb = StringBuilder("total ${files.size}\n")
                files.forEach { f ->
                    sb.append("-rw-r--r-- 1 bolt bolt ${f.content.length.toString().padStart(6)} ${f.filePath}\n")
                }
                sb.toString().trim()
            }

            trimmed.startsWith("cat ") -> {
                val targetPath = trimmed.removePrefix("cat").trim()
                val file = dao.getFile(projectId, targetPath)
                file?.content ?: "cat: $targetPath: No such file or directory"
            }

            trimmed == "git status" -> {
                """
                On branch main
                Your branch is up to date with 'origin/main'.
                All virtual files saved to local Room SQLite cache.
                nothing to commit, working tree clean
                """.trimIndent()
            }

            trimmed.startsWith("curl ") -> {
                val target = trimmed.removePrefix("curl").trim()
                """
                HTTP/1.1 200 OK
                Content-Type: application/json
                Date: ${System.currentTimeMillis()}
                
                {"status":"healthy","service":"bolt-virtual-container","endpoint":"$target"}
                """.trimIndent()
            }

            else -> "bash: $trimmed: command not recognized. Type 'help' for available commands."
        }

        val entry = TerminalEntryEntity(
            id = entryId,
            projectId = projectId,
            command = trimmed,
            output = output,
            status = if (output.startsWith("bash:") || output.startsWith("Error:")) "error" else "success"
        )
        dao.insertTerminalEntry(entry)
        return output
    }

    suspend fun askBoltAi(
        projectId: String,
        userPrompt: String,
        model: String = "gemini-3.1-pro-preview",
        enableHighThinking: Boolean = true
    ): GeminiGenerationResult {
        val startTime = System.currentTimeMillis()

        // 1. Record user message
        val userMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            role = "user",
            content = userPrompt,
            timestamp = System.currentTimeMillis()
        )
        dao.insertMessage(userMsg)

        // 2. Prepare file context
        val currentFiles = dao.getFilesForProject(projectId).firstOrNull() ?: emptyList()
        val fileSummary = currentFiles.joinToString("\n---\n") { f ->
            "File: ${f.filePath} (${f.language})\n${f.content.take(800)}"
        }

        // 3. Call Gemini
        val result = geminiService.generateFullStackArtifact(
            prompt = userPrompt,
            currentFilesSummary = fileSummary,
            model = model,
            enableHighThinking = enableHighThinking
        )

        val durationSec = ((System.currentTimeMillis() - startTime) / 1000).toInt().coerceAtLeast(1)

        if (result.isSuccess && result.mainText.isNotBlank()) {
            val parsedArtifact = BoltArtifactParser.parse(result.mainText)

            // Apply files
            val newFileEntities = mutableListOf<ProjectFileEntity>()
            for (action in parsedArtifact.actions) {
                if (action.type == ActionType.FILE && action.filePath.isNotBlank()) {
                    newFileEntities.add(
                        ProjectFileEntity(
                            projectId = projectId,
                            filePath = action.filePath,
                            content = action.content,
                            language = BoltArtifactParser.detectLanguage(action.filePath),
                            isDirectory = false,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                } else if (action.type == ActionType.SHELL && action.content.isNotBlank()) {
                    // Log shell action in terminal
                    dao.insertTerminalEntry(
                        TerminalEntryEntity(
                            id = UUID.randomUUID().toString(),
                            projectId = projectId,
                            command = action.content,
                            output = "[Bolt Autopilot] Executed: ${action.content}\nTask completed successfully.",
                            status = "success"
                        )
                    )
                }
            }

            if (newFileEntities.isNotEmpty()) {
                dao.insertOrUpdateFiles(newFileEntities)
            }

            // Save assistant message
            val assistantMsg = ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                role = "assistant",
                content = parsedArtifact.explanationText.ifBlank {
                    "Generated ${newFileEntities.size} files for **${parsedArtifact.title}**."
                },
                thoughtProcess = result.thoughtProcess,
                thinkingDurationSec = durationSec,
                timestamp = System.currentTimeMillis(),
                hasArtifact = newFileEntities.isNotEmpty()
            )
            dao.insertMessage(assistantMsg)
        } else {
            // Save error or missing key notice
            val errorMsg = ChatMessageEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                role = "assistant",
                content = result.errorMessage ?: "Bolt encountered an issue generating files.",
                thoughtProcess = result.thoughtProcess ?: "Thinking failed: ${result.errorMessage}",
                thinkingDurationSec = durationSec,
                timestamp = System.currentTimeMillis(),
                hasArtifact = false
            )
            dao.insertMessage(errorMsg)
        }

        return result
    }

    suspend fun deleteProject(projectId: String) {
        dao.deleteAllFilesForProject(projectId)
        dao.clearMessagesForProject(projectId)
        dao.clearTerminal(projectId)
        dao.deleteProjectById(projectId)
    }
}
