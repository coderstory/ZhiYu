package com.zhiyu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.zhiyu.app.data.local.entity.QuickNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickNoteDao {

    @Query("SELECT * FROM quick_notes ORDER BY createdAt DESC")
    fun getAll(): Flow<List<QuickNoteEntity>>

    @Insert
    suspend fun insert(entity: QuickNoteEntity): Long

    @Delete
    suspend fun delete(entity: QuickNoteEntity)
}
