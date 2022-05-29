package com.r2b.apps.xuser.ui.adapter

import android.annotation.SuppressLint
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate
import com.r2b.apps.lib.imageloader.ImageLoader
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.ui.adapter.delegate.LoadingMoreItemDelegate
import com.r2b.apps.xuser.ui.adapter.delegate.UserItemDelegate
import com.r2b.apps.xuser.ui.adapter.model.UserListItemView

class UserAdapter(
    onSelectedUser: (user: User) -> Unit,
    imageLoader: ImageLoader,
): ListDelegationAdapter<List<UserListItemView>>() {

    init {
        items = listOf()
        delegatesManager
            .addDelegate(LoadingMoreItemDelegate())
            .addDelegate(UserItemDelegate(onSelectedUser, imageLoader))
    }

    fun dropLast() {
        val positionStart = items!!.size - 1
        val list = mutableListOf<UserListItemView>()
        list.addAll(items!!.dropLast(1))
        items = list.toList()
        notifyItemChanged(positionStart)
    }

    fun append(newItems: List<UserListItemView>) {
        val list = mutableListOf<UserListItemView>()
        val positionStart = items!!.size
        list.addAll(items!! + newItems)
        items = list.toList()
        notifyItemRangeInserted(positionStart, newItems.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reset(newItems: List<UserListItemView>) {
        items = newItems
        notifyDataSetChanged()
    }

}
