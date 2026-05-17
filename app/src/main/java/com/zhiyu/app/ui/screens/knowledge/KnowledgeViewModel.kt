package com.zhiyu.app.ui.screens.knowledge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhiyu.app.data.local.dao.ArticleWithCategory
import com.zhiyu.app.data.local.entity.CategoryEntity
import com.zhiyu.app.data.local.entity.QuickNoteEntity
import com.zhiyu.app.data.repository.ArticleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class KnowledgeUiState(
    val articles: List<ArticleWithCategory> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val quickNotes: List<QuickNoteEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class KnowledgeViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(KnowledgeUiState())
    val uiState: StateFlow<KnowledgeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadCategories()
        loadQuickNotes()
        // Observe search query with debounce
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    _uiState.update { it.copy(searchQuery = query) }
                    loadArticles(query = query)
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(categoryId: Long?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        loadArticles(categoryId = categoryId)
    }

    fun createQuickNote(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            try {
                repository.createQuickNote(content.trim())
                loadQuickNotes()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "创建小记失败: ${e.message}") }
            }
        }
    }

    fun deleteQuickNote(note: QuickNoteEntity) {
        viewModelScope.launch {
            try {
                repository.deleteQuickNote(note)
                loadQuickNotes()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "删除小记失败: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getAllCategories()
                .catch { e ->
                    _uiState.update { it.copy(error = "加载分类失败: ${e.message}") }
                }
                .collect { categories ->
                    _uiState.update { it.copy(categories = categories) }
                }
        }
    }

    private fun loadQuickNotes() {
        viewModelScope.launch {
            repository.getAllQuickNotes()
                .catch { e ->
                    _uiState.update { it.copy(error = "加载小记失败: ${e.message}") }
                }
                .collect { notes ->
                    _uiState.update { it.copy(quickNotes = notes) }
                }
        }
    }

    private fun loadArticles(
        query: String = _uiState.value.searchQuery,
        categoryId: Long? = _uiState.value.selectedCategoryId
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val articleFlow = when {
                query.isNotBlank() -> repository.searchArticles(query)
                categoryId != null -> repository.getArticlesByCategory(categoryId)
                else -> repository.getAllArticlesWithCategory()
            }

            articleFlow
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = "加载文章失败: ${e.message}")
                    }
                }
                .collect { articles ->
                    _uiState.update {
                        it.copy(articles = articles, isLoading = false)
                    }
                }
        }
    }
}
