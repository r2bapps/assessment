package com.r2b.apps.xuser.ui.list.adapter.delegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.r2b.apps.xuser.databinding.LayoutLoadingMoreListItemBinding
import com.r2b.apps.xuser.ui.list.adapter.model.LoadingMoreListItem
import com.r2b.apps.xuser.ui.list.adapter.model.UserListItemView

class LoadingMoreItemDelegate : AdapterDelegate<List<UserListItemView>>() {

    override fun isForViewType(items: List<UserListItemView>, position: Int): Boolean =
        items[position] is LoadingMoreListItem

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflatedView = LayoutLoadingMoreListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingMoreItemViewHolder(
            inflatedView,
        )
    }

    override fun onBindViewHolder(
        items: List<UserListItemView>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>,
    ) = (holder as LoadingMoreItemViewHolder).bind(items[position] as LoadingMoreListItem)

}

class LoadingMoreItemViewHolder(
    private val binding: LayoutLoadingMoreListItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: LoadingMoreListItem) = Unit

}
