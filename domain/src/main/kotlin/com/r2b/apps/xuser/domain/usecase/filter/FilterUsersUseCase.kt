package com.r2b.apps.xuser.domain.usecase.filter

import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class FilterUsersUseCase (
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher,
) {
    fun execute(text: String): Flow<List<User>> =
        flow { emit(userRepository.filterUsersBy(text)) }.flowOn(dispatcher)
}