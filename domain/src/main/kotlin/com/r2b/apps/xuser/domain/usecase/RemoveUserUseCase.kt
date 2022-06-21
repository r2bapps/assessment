package com.r2b.apps.xuser.domain.usecase

import com.r2b.apps.xuser.domain.di.qualifier.BackgroundScope
import com.r2b.apps.xuser.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RemoveUserUseCase @Inject constructor (
    private val userRepository: UserRepository,
    @BackgroundScope private val dispatcher: CoroutineDispatcher,
) {
    fun execute(userId: Int): Flow<Unit> =
        flow { emit(userRepository.removeUser(userId)) }.flowOn(dispatcher)
}