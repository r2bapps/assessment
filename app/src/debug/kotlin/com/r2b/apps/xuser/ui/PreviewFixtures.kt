package com.r2b.apps.xuser.ui

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.r2b.apps.lib.imageloader.ImageLoader
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.domain.repository.UserRepository
import com.r2b.apps.xuser.domain.usecase.GetUsersUseCase
import com.r2b.apps.xuser.domain.usecase.RemoveUserUseCase
import com.r2b.apps.xuser.domain.usecase.filter.FilterUsersUseCase
import com.r2b.apps.xuser.domain.usecase.user.GetUserUseCase
import com.r2b.apps.xuser.ui.detail.UserDetailViewModel
import com.r2b.apps.xuser.ui.list.UserListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal val previewUsers = listOf(
    User(
        id = 1,
        serverId = "1",
        name = "Alex",
        surname = "Morgan",
        email = "alex.morgan@example.com",
        phone = "+1 555 0100",
        picture = "",
        gender = "Female",
        location = "New York, USA",
        registeredDate = "2024-01-15T10:00:00Z",
        description = "A sample profile for Compose previews.",
    ),
    User(
        id = 2,
        serverId = "2",
        name = "Jordan",
        surname = "Lee",
        email = "jordan.lee@example.com",
        phone = "+1 555 0101",
        picture = "",
        gender = "Male",
        location = "San Francisco, USA",
        registeredDate = "2023-08-20T08:30:00Z",
        description = "Another sample profile.",
    ),
)

internal fun previewImageLoader(context: Context): ImageLoader {
    val coilImageLoader = coil.ImageLoader.Builder(context).build()
    return object : ImageLoader {
        override fun getImageLoader(): coil.ImageLoader = coilImageLoader
    }
}

internal fun previewUserListViewModel(
    users: List<User> = previewUsers,
    failOnList: Boolean = false,
): UserListViewModel {
    val repository = PreviewUserRepository(users, failOnList = failOnList)
    return UserListViewModel(
        getUsersUseCase = GetUsersUseCase(repository, Dispatchers.Unconfined),
        removeUserUseCase = RemoveUserUseCase(repository, Dispatchers.Unconfined),
        filterUsersUseCase = FilterUsersUseCase(repository, Dispatchers.Unconfined),
        savedStateHandle = androidx.lifecycle.SavedStateHandle(),
    )
}

internal fun previewUserDetailViewModel(
    user: User? = previewUsers.first(),
    fail: Boolean = false,
): UserDetailViewModel {
    val repository = PreviewUserRepository(previewUsers, user, fail)
    return UserDetailViewModel(GetUserUseCase(repository, Dispatchers.Unconfined))
}

private class PreviewUserRepository(
    private val users: List<User>,
    private val detailUser: User? = users.firstOrNull(),
    private val failOnDetail: Boolean = false,
    private val failOnList: Boolean = false,
) : UserRepository {
    override fun list(): Flow<PagingData<User>> =
        if (failOnList) {
            Pager(PagingConfig(pageSize = 20)) {
                PreviewErrorPagingSource()
            }.flow
        } else {
            flowOf(PagingData.from(users))
        }

    override suspend fun getUser(id: Int): User? {
        if (failOnDetail) error("Preview API failure")
        return detailUser?.takeIf { it.id == id }
    }

    override suspend fun removeUser(id: Int) = Unit

    override fun filterUsersBy(text: String): Flow<PagingData<User>> =
        flowOf(PagingData.from(users.filter {
            it.name.contains(text, ignoreCase = true) ||
                it.surname.contains(text, ignoreCase = true) ||
                it.email.contains(text, ignoreCase = true)
        }))
}

private class PreviewErrorPagingSource : PagingSource<Int, User>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> =
        LoadResult.Error(IllegalStateException("HTTP 500: simulated API failure"))

    override fun getRefreshKey(state: PagingState<Int, User>): Int? = null
}
