package com.r2b.apps.xuser.domain.usecase.currentuser

import com.r2b.apps.xuser.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SetCurrentUserUseCase (
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    fun execute(userId: Int): Flow<Unit> =
        flow { emit(userRepository.setCurrentUser(userId)) }.flowOn(dispatcher)
}