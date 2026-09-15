package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val activeFilePath: String = "src/App.jsx",
    val template: String = "react_fullstack",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "project_files",
    primaryKeys = ["projectId", "filePath"]
)
data class ProjectFileEntity(
    val projectId: String,
    val filePath: String,
    val content: String,
    val language: String,
    val isDirectory: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val thoughtProcess: String? = null,
    val thinkingDurationSec: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val hasArtifact: Boolean = false
)

@Entity(tableName = "terminal_entries")
data class TerminalEntryEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val command: String,
    val output: String,
    val status: String = "success", // "running", "success", "error"
    val timestamp: Long = System.currentTimeMillis()
)
