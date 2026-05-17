package com.zhiyu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zhiyu.app.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getById(id: Long): ArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ArticleEntity): Long

    @Update
    suspend fun update(entity: ArticleEntity)

    @Delete
    suspend fun delete(entity: ArticleEntity)

    @Query(
        """
        SELECT * FROM articles
        WHERE rowid IN (SELECT rowid FROM articles_fts WHERE content MATCH :query)
        """
    )
    fun searchByFts(query: String): Flow<List<ArticleEntity>>
}
