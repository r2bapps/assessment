package com.r2b.apps.xuser.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.r2b.apps.utils.Either
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.storage.UserLocalDataSource
import com.r2b.apps.xuser.data.storage.entity.UserDB
import com.r2b.apps.xuser.domain.model.DomainError

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val userDataSource: UserDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) : RemoteMediator<Int, UserDB>() {

    override suspend fun initialize(): InitializeAction =
        InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserDB>,
    ): MediatorResult {
        var page = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> userLocalDataSource.nextPage()
                ?: return MediatorResult.Success(endOfPaginationReached = true)
        }

        repeat(PAGES_PER_LOAD) { pageInBatch ->
            when (val response = userDataSource.list(page)) {
                is Either.Failure ->
                    return MediatorResult.Error(UserSyncException(response.error.toDomainError()))

                is Either.Success -> {
                    val pageSize = userDataSource.pageItems
                    val pageUsers = response.data.dtoToDBEntity().mapIndexed { index, user ->
                        user.copy(sortOrder = page * pageSize + index)
                    }
                    val nextPage = (page + 1).takeIf { pageUsers.size >= pageSize }
                    userLocalDataSource.syncPage(
                        users = pageUsers,
                        refresh = loadType == LoadType.REFRESH && pageInBatch == 0,
                        nextPage = nextPage,
                    )
                    if (nextPage == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    page = nextPage
                }
            }
        }

        return MediatorResult.Success(endOfPaginationReached = false)
    }

    private companion object {
        const val PAGES_PER_LOAD = 2
    }

    class UserSyncException(val domainError: DomainError) :
        Exception(domainError.toExceptionMessage())
}

private fun DomainError.toExceptionMessage(): String = buildString {
    if (code > 0) append("Error $code: ")
    val message = message.ifBlank { cause }
    append(message)
    if (cause.isNotBlank() && cause != message) append(": $cause")
}
