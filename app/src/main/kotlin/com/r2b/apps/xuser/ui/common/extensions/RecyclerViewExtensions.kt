package com.r2b.apps.xuser.ui.common.extensions

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.bottomPullToRefresh(
    linearLayoutManager: LinearLayoutManager,
    onBottomPull: () -> Unit,
) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        val visibleThreshold = 10
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0) {
                val visibleItemCount: Int = linearLayoutManager.childCount
                val totalItemCount: Int = linearLayoutManager.itemCount
                val firstVisibleItemPosition: Int = linearLayoutManager.findFirstVisibleItemPosition()
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount &&
                    firstVisibleItemPosition >= 0 &&
                    totalItemCount >= visibleThreshold) {
                    post { onBottomPull() }
                }
            }
        }
    })
}

fun RecyclerView.onSwipe(onSwipeAction: (position: Int) -> Unit) {
    ItemTouchHelper(object : ItemTouchHelper.Callback() {

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int =
            makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) =
            onSwipeAction(viewHolder.absoluteAdapterPosition)

    }).attachToRecyclerView(this)
}
