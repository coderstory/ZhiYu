package com.zhiyu.app.data.repository

import com.zhiyu.app.data.local.dao.ArticleDao
import com.zhiyu.app.data.local.dao.ArticleWithCategory
import com.zhiyu.app.data.local.dao.CategoryDao
import com.zhiyu.app.data.local.dao.QuickNoteDao
import com.zhiyu.app.data.local.dao.TagDao
import com.zhiyu.app.data.local.entity.ArticleEntity
import com.zhiyu.app.data.local.entity.ArticleTagCrossRef
import com.zhiyu.app.data.local.entity.QuickNoteEntity
import com.zhiyu.app.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for articles, categories, tags, and quick notes.
 *
 * Uses JOIN queries (not @Relation) for article-category and article-tag
 * associations, as mandated by the architecture decision D-05 / PITFALL-10.
 */
class ArticleRepository(
    private val articleDao: ArticleDao,
    private val tagDao: TagDao,
    private val categoryDao: CategoryDao,
    private val quickNoteDao: QuickNoteDao
) {

    // ── Articles with category (JOIN queries) ────────────────────────────

    fun getAllArticlesWithCategory(): Flow<List<ArticleWithCategory>> =
        articleDao.getAllWithCategory()

    fun getArticleWithCategoryById(id: Long): Flow<ArticleWithCategory?> =
        articleDao.getByIdWithCategory(id)

    fun searchArticles(query: String): Flow<List<ArticleWithCategory>> =
        articleDao.searchByFtsWithCategory(query)

    fun getArticlesByCategory(categoryId: Long): Flow<List<ArticleWithCategory>> =
        articleDao.getByCategoryWithCategory(categoryId)

    // ── Tags for article (JOIN query in TagDao) ──────────────────────────

    fun getTagsByArticle(articleId: Long): Flow<List<TagEntity>> =
        tagDao.getTagsByArticle(articleId)

    fun getAllTags(): Flow<List<TagEntity>> = tagDao.getAll()

    suspend fun updateArticleTags(articleId: Long, tagIds: List<Long>) {
        articleDao.deleteCrossRefsByArticle(articleId)
        tagIds.forEach { tagId ->
            articleDao.insertCrossRef(ArticleTagCrossRef(articleId, tagId))
        }
    }

    // ── Article CRUD ─────────────────────────────────────────────────────

    suspend fun createArticle(
        title: String,
        content: String,
        categoryId: Long?,
        tagIds: List<Long> = emptyList()
    ): Long {
        val now = System.currentTimeMillis()
        val entity = ArticleEntity(
            title = title,
            content = content,
            categoryId = categoryId,
            createdAt = now,
            updatedAt = now
        )
        val articleId = articleDao.insert(entity)
        if (tagIds.isNotEmpty()) {
            updateArticleTags(articleId, tagIds)
        }
        return articleId
    }

    suspend fun updateArticle(
        articleId: Long,
        title: String,
        content: String,
        categoryId: Long?,
        tagIds: List<Long> = emptyList()
    ) {
        val entity = articleDao.getById(articleId) ?: return
        val updated = entity.copy(
            title = title,
            content = content,
            categoryId = categoryId,
            updatedAt = System.currentTimeMillis()
        )
        articleDao.update(updated)
        updateArticleTags(articleId, tagIds)
    }

    suspend fun deleteArticle(article: ArticleEntity) {
        articleDao.delete(article)
    }

    suspend fun getArticleEntityById(id: Long): ArticleEntity? =
        articleDao.getById(id)

    // ── Categories ───────────────────────────────────────────────────────

    fun getAllCategories(): Flow<List<com.zhiyu.app.data.local.entity.CategoryEntity>> =
        categoryDao.getAll()

    // ── Quick Notes ──────────────────────────────────────────────────────

    fun getAllQuickNotes(): Flow<List<QuickNoteEntity>> =
        quickNoteDao.getAll()

    suspend fun createQuickNote(content: String): Long {
        val entity = QuickNoteEntity(
            content = content,
            createdAt = System.currentTimeMillis()
        )
        return quickNoteDao.insert(entity)
    }

    suspend fun deleteQuickNote(note: QuickNoteEntity) {
        quickNoteDao.delete(note)
    }
}
