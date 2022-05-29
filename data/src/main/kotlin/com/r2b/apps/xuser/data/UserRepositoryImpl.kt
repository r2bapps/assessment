package com.r2b.apps.xuser.data

import androidx.paging.Pager
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.r2b.apps.xuser.data.api.UserDataSource
import com.r2b.apps.xuser.data.storage.UserLocalDataSource
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) : UserRepository {

    override fun list(): Flow<PagingData<User>> = usersPager()

    override suspend fun getUser(id: Int): User? {
        return userLocalDataSource.get(id)?.dbEntityToDomain()
    }

    override suspend fun removeUser(id: Int) {
        userLocalDataSource.remove(id)
    }

    override fun filterUsersBy(text: String): Flow<PagingData<User>> =
        usersPager(text)

    private fun usersPager(query: String? = null): Flow<PagingData<User>> =
        Pager(
            config = PagingConfig(
                pageSize = userDataSource.pageItems,
                prefetchDistance = maxOf(1, userDataSource.pageItems / 4),
                initialLoadSize = userDataSource.pageItems,
                enablePlaceholders = false,
            ),
            remoteMediator = if (query == null) {
                UserRemoteMediator(userDataSource, userLocalDataSource)
            } else {
                null
            },
            pagingSourceFactory = { userLocalDataSource.pagingSource(query) },
        ).flow.map { pagingData ->
            pagingData.map { it.dbEntityToDomain() }
        }

}
