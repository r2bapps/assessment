package com.r2b.apps.xuser.domain

import com.r2b.apps.xuser.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetCurrentUserUseCase (
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    fun execute(): Flow<User?> =
        flow { emit( userRepository.getCurrentUser() ) }.flowOn(dispatcher)
}
