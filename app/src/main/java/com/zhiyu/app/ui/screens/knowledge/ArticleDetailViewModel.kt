package com.zhiyu.app.ui.screens.knowledge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhiyu.app.data.local.dao.ArticleWithCategory
import com.zhiyu.app.data.local.entity.TagEntity
import com.zhiyu.app.data.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArticleDetailUiState(
    val article: ArticleWithCategory? = null,
    val tags: List<TagEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ArticleDetailViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    private var articleId: Long = 0L

    fun loadArticle(id: Long) {
        articleId = id
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Load article with category
            repository.getArticleWithCategoryById(id)
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = "加载文章失败: ${e.message}")
                    }
                }
                .collect { article ->
                    _uiState.update { it.copy(article = article) }
                }
        }

        // Load tags
        viewModelScope.launch {
            repository.getTagsByArticle(id)
                .catch { e ->
                    _uiState.update {
                        it.copy(error = "加载标签失败: ${e.message}")
                    }
                }
                .collect { tags ->
                    _uiState.update { it.copy(tags = tags, isLoading = false) }
                }
        }
    }

    fun deleteArticle(onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                val entity = repository.getArticleEntityById(articleId)
                if (entity != null) {
                    repository.deleteArticle(entity)
                }
                onDeleted()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "删除文章失败: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
