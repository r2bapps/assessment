package com.r2b.apps.xuser.domain.usecase

import com.r2b.apps.xuser.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoveUserUseCase (
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    fun execute(userId: Int): Flow<Unit> =
        flow { emit(userRepository.removeUser(userId)) }.flowOn(dispatcher)
}