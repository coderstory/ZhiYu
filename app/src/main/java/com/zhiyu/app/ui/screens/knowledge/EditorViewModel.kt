package com.zhiyu.app.ui.screens.knowledge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhiyu.app.data.local.entity.CategoryEntity
import com.zhiyu.app.data.local.entity.TagEntity
import com.zhiyu.app.data.repository.ArticleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditorUiState(
    val articleId: Long = 0L,
    val title: String = "",
    val content: String = "",
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val categories: List<CategoryEntity> = emptyList(),
    val allTags: List<TagEntity> = emptyList(),
    val selectedTagIds: Set<Long> = emptySet(),
    val isPreview: Boolean = false,
    val isSaving: Boolean = false,
    val lastSavedAt: Long? = null,
    val isLoading: Boolean = true,
    val isNewArticle: Boolean = true,
    val error: String? = null
)

class EditorViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private var autoSaveJob: Job? = null

    fun loadArticle(articleId: Long) {
        if (articleId == 0L) {
            // New article
            _uiState.update {
                it.copy(isLoading = false, isNewArticle = true)
            }
            loadCategoriesAndTags()
            return
        }

        // Existing article
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getArticleWithCategoryById(articleId)
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = "加载文章失败: ${e.message}")
                    }
                }
                .collect { article ->
                    if (article != null) {
                        _uiState.update {
                            it.copy(
                                articleId = article.id,
                                title = article.title,
                                content = article.content,
                                categoryId = article.categoryId,
                                categoryName = article.categoryName,
                                isNewArticle = false,
                                isLoading = false
                            )
                        }
                        startAutoSave()
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = "文章不存在")
                        }
                    }
                }
        }

        // Load tags for this article
        viewModelScope.launch {
            repository.getTagsByArticle(articleId)
                .catch { /* ignore */ }
                .collect { tags ->
                    _uiState.update {
                        it.copy(selectedTagIds = tags.map { t -> t.id }.toSet())
                    }
                }
        }

        loadCategoriesAndTags()
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateContent(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    fun updateCategory(categoryId: Long?, categoryName: String?) {
        _uiState.update { it.copy(categoryId = categoryId, categoryName = categoryName) }
    }

    fun toggleTagSelection(tagId: Long) {
        _uiState.update {
            val current = it.selectedTagIds
            val updated = if (tagId in current) {
                current - tagId
            } else {
                current + tagId
            }
            it.copy(selectedTagIds = updated)
        }
    }

    fun togglePreview() {
        _uiState.update { it.copy(isPreview = !it.isPreview) }
    }

    fun saveNow(onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch {
            doSave(onSaved)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadCategoriesAndTags() {
        viewModelScope.launch {
            repository.getAllCategories()
                .catch { e ->
                    _uiState.update { it.copy(error = "加载分类失败: ${e.message}") }
                }
                .collect { cats ->
                    _uiState.update { it.copy(categories = cats) }
                }
        }
        viewModelScope.launch {
            repository.getAllTags()
                .catch { e ->
                    _uiState.update { it.copy(error = "加载标签失败: ${e.message}") }
                }
                .collect { tags ->
                    _uiState.update { it.copy(allTags = tags) }
                }
        }
    }

    /**
     * Auto-save every 5 seconds if content has changed.
     * Called once after loading an existing article or creating a new one.
     */
    fun startAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            while (true) {
                delay(5000)
                val state = _uiState.value
                if (state.title.isNotBlank() || state.content.isNotBlank()) {
                    doSave()
                }
            }
        }
    }

    private suspend fun doSave(onSaved: (Long) -> Unit = {}) {
        val state = _uiState.value
        if (state.title.isBlank() && state.content.isBlank()) return

        _uiState.update { it.copy(isSaving = true) }
        try {
            val articleId = state.articleId
            if (articleId == 0L) {
                // New article — insert first
                val newId = repository.createArticle(
                    title = state.title,
                    content = state.content,
                    categoryId = state.categoryId,
                    tagIds = state.selectedTagIds.toList()
                )
                _uiState.update {
                    it.copy(articleId = newId, isNewArticle = false, lastSavedAt = System.currentTimeMillis())
                }
                onSaved(newId)
            } else {
                // Existing — update
                repository.updateArticle(
                    articleId = articleId,
                    title = state.title,
                    content = state.content,
                    categoryId = state.categoryId,
                    tagIds = state.selectedTagIds.toList()
                )
                _uiState.update { it.copy(lastSavedAt = System.currentTimeMillis()) }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "保存失败: ${e.message}") }
        } finally {
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoSaveJob?.cancel()
        // Final save on clear (process death safety)
        viewModelScope.launch {
            doSave()
        }
    }
}
