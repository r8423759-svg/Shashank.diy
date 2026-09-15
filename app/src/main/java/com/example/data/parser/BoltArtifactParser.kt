package com.example.data.parser

enum class ActionType {
    FILE,
    SHELL
}

enum class ActionStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED
}

data class BoltAction(
    val type: ActionType,
    val filePath: String = "",
    val content: String = "",
    var status: ActionStatus = ActionStatus.PENDING
)

data class ParsedArtifact(
    val id: String,
    val title: String,
    val actions: List<BoltAction>,
    val explanationText: String
)

object BoltArtifactParser {

    fun parse(rawText: String): ParsedArtifact {
        val actions = mutableListOf<BoltAction>()

        // 1. Look for <boltArtifact> tags
        val artifactRegex = Regex("<boltArtifact\\s+id=\"([^\"]+)\"\\s+title=\"([^\"]+)\">([\\s\\S]*?)</boltArtifact>", RegexOption.IGNORE_CASE)
        val artifactMatch = artifactRegex.find(rawText)

        var title = "Bolt Project Build"
        var id = "project-build"
        val artifactBody: String

        if (artifactMatch != null) {
            id = artifactMatch.groupValues[1]
            title = artifactMatch.groupValues[2]
            artifactBody = artifactMatch.groupValues[3]
        } else {
            artifactBody = rawText
        }

        // 2. Parse <boltAction> tags
        val actionRegex = Regex("<boltAction\\s+type=\"([^\"]+)\"(?:\\s+filePath=\"([^\"]+)\")?>([\\s\\S]*?)</boltAction>", RegexOption.IGNORE_CASE)
        val matches = actionRegex.findAll(artifactBody).toList()

        if (matches.isNotEmpty()) {
            for (m in matches) {
                val actionTypeStr = m.groupValues[1].lowercase()
                val filePath = m.groupValues[2].trim()
                val content = m.groupValues[3].trim()

                when (actionTypeStr) {
                    "file" -> {
                        if (filePath.isNotBlank()) {
                            actions.add(
                                BoltAction(
                                    type = ActionType.FILE,
                                    filePath = filePath,
                                    content = content,
                                    status = ActionStatus.COMPLETED
                                )
                            )
                        }
                    }
                    "shell" -> {
                        actions.add(
                            BoltAction(
                                type = ActionType.SHELL,
                                content = content,
                                status = ActionStatus.COMPLETED
                            )
                        )
                    }
                }
            }
        } else {
            // Fallback: Parse markdown code blocks with file path comments or headers
            // e.g. ```javascript:src/App.jsx or // src/App.jsx or ### src/App.jsx
            val codeBlockRegex = Regex("```(?:[a-zA-Z0-9]+:)?([a-zA-Z0-9_./\\-]+\\.[a-zA-Z0-9]+)?\\n([\\s\\S]*?)```")
            val codeMatches = codeBlockRegex.findAll(rawText).toList()

            for (m in codeMatches) {
                var path = m.groupValues[1].trim()
                val blockContent = m.groupValues[2].trim()

                if (path.isBlank()) {
                    // Try to inspect first line for comment like // src/App.jsx or <!-- index.html -->
                    val firstLine = blockContent.lines().firstOrNull() ?: ""
                    val commentPathMatch = Regex("(?:\\/\\/|#|<!--|/\\*)\\s*([a-zA-Z0-9_./\\-]+\\.[a-zA-Z0-9]+)").find(firstLine)
                    if (commentPathMatch != null) {
                        path = commentPathMatch.groupValues[1].trim()
                    }
                }

                if (path.isNotBlank()) {
                    actions.add(
                        BoltAction(
                            type = ActionType.FILE,
                            filePath = path,
                            content = blockContent,
                            status = ActionStatus.COMPLETED
                        )
                    )
                }
            }
        }

        // Clean explanation text
        val cleanedText = rawText
            .replace(artifactRegex, "")
            .replace(Regex("<boltArtifact[\\s\\S]*?</boltArtifact>", RegexOption.IGNORE_CASE), "")
            .trim()

        return ParsedArtifact(
            id = id,
            title = title,
            actions = actions,
            explanationText = cleanedText
        )
    }

    fun detectLanguage(filePath: String): String {
        return when {
            filePath.endsWith(".jsx", ignoreCase = true) -> "javascript"
            filePath.endsWith(".tsx", ignoreCase = true) -> "typescript"
            filePath.endsWith(".js", ignoreCase = true) -> "javascript"
            filePath.endsWith(".ts", ignoreCase = true) -> "typescript"
            filePath.endsWith(".html", ignoreCase = true) -> "html"
            filePath.endsWith(".css", ignoreCase = true) -> "css"
            filePath.endsWith(".json", ignoreCase = true) -> "json"
            filePath.endsWith(".md", ignoreCase = true) -> "markdown"
            filePath.endsWith(".py", ignoreCase = true) -> "python"
            filePath.endsWith(".sql", ignoreCase = true) -> "sql"
            else -> "text"
        }
    }
}
