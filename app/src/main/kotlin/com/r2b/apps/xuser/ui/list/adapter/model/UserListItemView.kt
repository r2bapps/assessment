package com.r2b.apps.xuser.ui.list.adapter.model

import com.r2b.apps.xuser.domain.model.User

sealed class UserListItemView
data class UserListItem(val user: User): UserListItemView()
object LoadingMoreListItem: UserListItemView()
