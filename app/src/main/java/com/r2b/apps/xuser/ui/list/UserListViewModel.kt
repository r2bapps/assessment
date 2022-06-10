package com.r2b.apps.xuser.ui.list

import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PRIVATE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.domain.usecase.*
import com.r2b.apps.xuser.domain.usecase.currentuser.SetCurrentUserUseCase
import com.r2b.apps.xuser.domain.usecase.filter.FilterUsersUseCase
import com.r2b.apps.xuser.domain.usecase.filter.RemoveUsersFilterUseCase
import com.r2b.apps.xuser.ui.list.adapter.model.UserListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val setUserUseCase: SetCurrentUserUseCase,
    private val removeUserUseCase: RemoveUserUseCase,
    private val filterUsersUseCase: FilterUsersUseCase,
    private val removeUsersFilterUseCase: RemoveUsersFilterUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    private val _event = Channel<Event>()
    val event = _event.receiveAsFlow()

    private var page: Int = 0
    @VisibleForTesting(otherwise = PRIVATE)
    internal var isLastPage: Boolean = false
    @VisibleForTesting(otherwise = PRIVATE)
    internal var isLoadingMoreEnabled: Boolean = false

    fun load() {
        if (!isLastPage) {
            getUsersUseCase.execute(page)
                .onStart {
                    isLoadingMoreEnabled = false
                    _event.send(Event.Loading)
                }
                .onEach {
                    checkLastPage(it)
                    _uiState.value = UserUiState.Load(it.toViewModel())
                }
                .onCompletion {
                    isLoadingMoreEnabled = true
                    _uiState.value = UserUiState.Idle
                }
                .catch {
                    isLoadingMoreEnabled = true
                    _event.send(Event.Error(it))
                }
                .launchIn(viewModelScope)
        }
    }

    fun nextPage() {
        if (!isLastPage && isLoadingMoreEnabled) {
            page += 1
            getUsersUseCase.execute(page)
                .onStart {
                    isLoadingMoreEnabled = false
                    _event.send(Event.LoadingMore)
                }
                .onEach {
                    checkLastPage(it)
                    _uiState.value = UserUiState.LoadMore(it.toViewModel())
                }
                .onCompletion { isLoadingMoreEnabled = true }
                .catch {
                    isLoadingMoreEnabled = true
                    _event.send(Event.Error(it))
                }
                .launchIn(viewModelScope)
        }
    }

    private suspend fun checkLastPage(users: List<User>) {
        if (users.isEmpty()) {
            isLastPage = true
            _event.send(Event.LastPage)
        }
    }

    fun removeUser(position: Int, user: User) {
        removeUserUseCase.execute(user.id)
            .onEach { _event.send(Event.UserDeleted(position)) }
            .catch { _event.send(Event.Error(it)) }
            .launchIn(viewModelScope)
    }

    fun selectUser(userId: Int) {
        setUserUseCase.execute(userId)
            .onEach { _event.send(Event.UserSelected) }
            .catch { _event.send(Event.Error(it)) }
            .launchIn(viewModelScope)
    }

    fun filter(text: String) {
        if (isLoadingMoreEnabled) {
            filterUsersUseCase.execute(text)
                .onEach { _uiState.value = UserUiState.Filter(it.toViewModel()) }
                .catch { _event.send(Event.Error(it)) }
                .launchIn(viewModelScope)
        }
    }

    fun removeFilter() {
        removeUsersFilterUseCase.execute()
            .onEach { _uiState.value = UserUiState.FilterRemoved(it.toViewModel()) }
            .catch { _event.send(Event.Error(it)) }
            .launchIn(viewModelScope)
    }

    private fun List<User>.toViewModel():List<UserListItem> =
        this.map { UserListItem(it) }

    sealed class UserUiState {
        object Idle: UserUiState()
        data class Load(val users: List<UserListItem>): UserUiState()
        data class LoadMore(val users: List<UserListItem>): UserUiState()
        data class Filter(val users: List<UserListItem>): UserUiState()
        data class FilterRemoved(val users: List<UserListItem>): UserUiState()
    }

    sealed class Event {
        object Loading: Event()
        data class UserDeleted(val position: Int): Event()
        object UserSelected: Event()
        object LoadingMore: Event()
        object LastPage: Event()
        data class Error(val error: Throwable): Event()
    }

}