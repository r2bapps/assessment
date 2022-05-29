package com.r2b.apps.xuser.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.domain.usecase.user.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()
    private var requestedUserId: Int? = null
    private var loadJob: Job? = null

    fun onAction(action: UserDetailAction) {
        when (action) {
            is UserDetailAction.Load -> load(action.userId)
            UserDetailAction.Retry -> requestedUserId?.let(::load)
        }
    }

    private fun load(userId: Int) {
        requestedUserId = userId
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = UserUiState.Loading
            getUserUseCase.execute(userId)
                .catch { error -> _uiState.value = UserUiState.Error(error) }
                .collect { user ->
                    _uiState.value =
                        user?.let(UserUiState::Content) ?: UserUiState.NotFound
                }
        }
    }

    sealed class UserUiState {
        object Loading : UserUiState()
        data class Content(val user: User) : UserUiState()
        object NotFound : UserUiState()
        data class Error(val error: Throwable) : UserUiState()
    }
}

sealed interface UserDetailAction {
    data class Load(val userId: Int) : UserDetailAction
    data object Retry : UserDetailAction
}
