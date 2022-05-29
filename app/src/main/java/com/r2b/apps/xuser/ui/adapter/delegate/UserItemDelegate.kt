package com.r2b.apps.xuser.ui.adapter.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.r2b.apps.lib.imageloader.ImageLoader
import com.r2b.apps.xuser.R
import com.r2b.apps.xuser.databinding.LayoutUserListItemBinding
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.ui.adapter.model.UserListItem
import com.r2b.apps.xuser.ui.adapter.model.UserListItemView

class UserItemDelegate(
    private val onSelectedUser: (user: User) -> Unit,
    private val imageLoader: ImageLoader,
): AdapterDelegate<List<UserListItemView>>() {

    override fun isForViewType(items: List<UserListItemView>, position: Int): Boolean =
        items[position] is UserListItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflatedView = LayoutUserListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserItemViewHolder(
            inflatedView,
            onSelectedUser,
            imageLoader,
        )
    }

    override fun onBindViewHolder(
        items: List<UserListItemView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>,
    ) = (holder as UserItemViewHolder).bind(items[position] as UserListItem)

}

class UserItemViewHolder(
    private val binding: LayoutUserListItemBinding,
    private val onSelectedUser: (user: User) -> Unit,
    private val imageLoader: ImageLoader,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: UserListItem) {
        with(binding) {
            if (item.user.picture.isNotEmpty()) imageLoader.load(item.user.picture, image, R.drawable.ic_profile)
            fullName.text = "${item.user.name.orEmpty()} ${item.user.surname.orEmpty()}"
            email.text = item.user.email.orEmpty()
            phone.text = item.user.phone.orEmpty()
            setListener(onSelectedUser, item.user)
        }
    }

    private fun setListener(onSelectedUser: (user: User) -> Unit, item: User) {
        binding.root.setOnClickListener { onSelectedUser(item) }
    }

}
