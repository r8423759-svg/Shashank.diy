package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectFileEntity
import com.example.data.local.entity.TerminalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoltDao {

    // Projects
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectById(projectId: String): ProjectEntity?

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    fun observeProject(projectId: String): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: String)

    // Files
    @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY filePath ASC")
    fun getFilesForProject(projectId: String): Flow<List<ProjectFileEntity>>

    @Query("SELECT * FROM project_files WHERE projectId = :projectId AND filePath = :filePath LIMIT 1")
    suspend fun getFile(projectId: String, filePath: String): ProjectFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFile(file: ProjectFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFiles(files: List<ProjectFileEntity>)

    @Query("DELETE FROM project_files WHERE projectId = :projectId AND filePath = :filePath")
    suspend fun deleteFile(projectId: String, filePath: String)

    @Query("DELETE FROM project_files WHERE projectId = :projectId")
    suspend fun deleteAllFilesForProject(projectId: String)

    // Chat
    @Query("SELECT * FROM chat_messages WHERE projectId = :projectId ORDER BY timestamp ASC")
    fun getMessagesForProject(projectId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE projectId = :projectId")
    suspend fun clearMessagesForProject(projectId: String)

    // Terminal
    @Query("SELECT * FROM terminal_entries WHERE projectId = :projectId ORDER BY timestamp ASC")
    fun getTerminalEntries(projectId: String): Flow<List<TerminalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTerminalEntry(entry: TerminalEntryEntity)

    @Query("DELETE FROM terminal_entries WHERE projectId = :projectId")
    suspend fun clearTerminal(projectId: String)
}
