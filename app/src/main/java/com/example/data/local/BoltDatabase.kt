package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.BoltDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectFileEntity
import com.example.data.local.entity.TerminalEntryEntity

@Database(
    entities = [
        ProjectEntity::class,
        ProjectFileEntity::class,
        ChatMessageEntity::class,
        TerminalEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BoltDatabase : RoomDatabase() {

    abstract fun boltDao(): BoltDao

    companion object {
        @Volatile
        private var INSTANCE: BoltDatabase? = null

        fun getDatabase(context: Context): BoltDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BoltDatabase::class.java,
                    "bolt_diy_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
