package com.r2b.apps.xuser.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r2b.apps.xuser.domain.usecase.currentuser.GetCurrentUserUseCase
import com.r2b.apps.xuser.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    private val _event = Channel<Event>()
    val event = _event.receiveAsFlow()

    fun load() {
        getCurrentUserUseCase.execute()
            .onEach { _uiState.value = UserUiState.Success(it) }
            .onCompletion { _uiState.value = UserUiState.Idle }
            .catch { _event.send(Event.Error(it)) }
            .launchIn(viewModelScope)
    }

    sealed class UserUiState {
        object Idle: UserUiState()
        object Loading: UserUiState()
        data class Success(val user: User?): UserUiState()
    }

    sealed class Event {
        data class Error(val error: Throwable): Event()
    }

}