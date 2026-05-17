package com.zhiyu.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.zhiyu.app.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM tags ORDER BY name")
    fun getAll(): Flow<List<TagEntity>>

    @Insert
    suspend fun insert(entity: TagEntity): Long

    @Delete
    suspend fun delete(entity: TagEntity)

    @Query(
        """
        SELECT t.* FROM tags t
        INNER JOIN article_tag_cross_ref c ON t.id = c.tagId
        WHERE c.articleId = :articleId
        """
    )
    fun getTagsByArticle(articleId: Long): Flow<List<TagEntity>>
}
