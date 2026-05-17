package com.zhiyu.app.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zhiyu.app.data.local.entity.ArticleEntity
import com.zhiyu.app.data.local.entity.ArticleTagCrossRef
import kotlinx.coroutines.flow.Flow

/**
 * JOIN query result: article with its category name (no @Relation).
 */
data class ArticleWithCategory(
    val id: Long,
    val title: String,
    val content: String,
    val categoryId: Long?,
    val categoryName: String?,
    val createdAt: Long,
    val updatedAt: Long
)

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

    // ── JOIN queries (no @Relation) ──────────────────────────────────────

    @Query(
        """
        SELECT a.id, a.title, a.content, a.categoryId, c.name AS categoryName, a.createdAt, a.updatedAt
        FROM articles a
        LEFT JOIN categories c ON a.categoryId = c.id
        ORDER BY a.updatedAt DESC
        """
    )
    fun getAllWithCategory(): Flow<List<ArticleWithCategory>>

    @Query(
        """
        SELECT a.id, a.title, a.content, a.categoryId, c.name AS categoryName, a.createdAt, a.updatedAt
        FROM articles a
        LEFT JOIN categories c ON a.categoryId = c.id
        WHERE a.id = :id
        """
    )
    fun getByIdWithCategory(id: Long): Flow<ArticleWithCategory?>

    @Query(
        """
        SELECT a.id, a.title, a.content, a.categoryId, c.name AS categoryName, a.createdAt, a.updatedAt
        FROM articles a
        LEFT JOIN categories c ON a.categoryId = c.id
        WHERE a.rowid IN (SELECT rowid FROM articles_fts WHERE articles_fts MATCH :query)
        ORDER BY a.updatedAt DESC
        """
    )
    fun searchByFtsWithCategory(query: String): Flow<List<ArticleWithCategory>>

    @Query(
        """
        SELECT a.id, a.title, a.content, a.categoryId, c.name AS categoryName, a.createdAt, a.updatedAt
        FROM articles a
        LEFT JOIN categories c ON a.categoryId = c.id
        WHERE a.categoryId = :categoryId
        ORDER BY a.updatedAt DESC
        """
    )
    fun getByCategoryWithCategory(categoryId: Long): Flow<List<ArticleWithCategory>>

    // ── Article-Tag cross ref ────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: ArticleTagCrossRef)

    @Query("DELETE FROM article_tag_cross_ref WHERE articleId = :articleId")
    suspend fun deleteCrossRefsByArticle(articleId: Long)
}
