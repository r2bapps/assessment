package com.r2b.apps.xuser.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.domain.usecase.GetUsersUseCase
import com.r2b.apps.xuser.domain.usecase.RemoveUserUseCase
import com.r2b.apps.xuser.domain.usecase.filter.FilterUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val removeUserUseCase: RemoveUserUseCase,
    private val filterUsersUseCase: FilterUsersUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val searchExpanded = savedStateHandle.get<Boolean>(SEARCH_EXPANDED_KEY) ?: false
    private val searchText = savedStateHandle.get<String>(SEARCH_TEXT_KEY).orEmpty()
    private val query = MutableStateFlow(searchText.takeUnless(String::isBlank))
    private val pagingData = query
        .flatMapLatest { text ->
            if (text.isNullOrBlank()) getUsersUseCase.execute()
            else filterUsersUseCase.execute(text)
        }
        .cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(
        UserListUiState(
            pagingData = pagingData,
            searchExpanded = searchExpanded,
            searchText = searchText,
        ),
    )
    val uiState = _uiState.asStateFlow()

    fun onAction(action: UserListAction) {
        when (action) {
            UserListAction.ToggleSearch -> toggleSearch()
            is UserListAction.SearchQueryChanged -> updateSearchQuery(action.query)
            UserListAction.CloseSearch -> closeSearch()
            is UserListAction.RemovalRequested -> _uiState.update {
                if (it.pendingRemoval == null) it.copy(pendingRemoval = action.user) else it
            }
            UserListAction.UndoRemoval -> _uiState.update { it.copy(pendingRemoval = null) }
            UserListAction.ConfirmRemoval -> confirmRemoval()
            UserListAction.ErrorShown -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun toggleSearch() {
        if (_uiState.value.searchExpanded) {
            closeSearch()
        } else {
            savedStateHandle[SEARCH_EXPANDED_KEY] = true
            _uiState.update { it.copy(searchExpanded = true) }
        }
    }

    private fun updateSearchQuery(text: String) {
        savedStateHandle[SEARCH_TEXT_KEY] = text
        _uiState.update { it.copy(searchText = text) }
        query.value = text.takeUnless(String::isBlank)
    }

    private fun closeSearch() {
        savedStateHandle[SEARCH_EXPANDED_KEY] = false
        savedStateHandle[SEARCH_TEXT_KEY] = ""
        _uiState.update {
            it.copy(searchExpanded = false, searchText = "")
        }
        query.value = null
    }

    private fun confirmRemoval() {
        val user = _uiState.value.pendingRemoval ?: return
        _uiState.update { it.copy(pendingRemoval = null) }
        viewModelScope.launch {
            removeUserUseCase.execute(user.id)
                .catch { error -> _uiState.update { it.copy(error = error) } }
                .collect {}
        }
    }

    private companion object {
        const val SEARCH_EXPANDED_KEY = "search_expanded"
        const val SEARCH_TEXT_KEY = "search_text"
    }
}

data class UserListUiState(
    val pagingData: Flow<PagingData<User>>,
    val searchExpanded: Boolean = false,
    val searchText: String = "",
    val pendingRemoval: User? = null,
    val error: Throwable? = null,
)

sealed interface UserListAction {
    data object ToggleSearch : UserListAction
    data class SearchQueryChanged(val query: String) : UserListAction
    data object CloseSearch : UserListAction
    data class RemovalRequested(val user: User) : UserListAction
    data object UndoRemoval : UserListAction
    data object ConfirmRemoval : UserListAction
    data object ErrorShown : UserListAction
}
