package com.r2b.apps.xuser.ui.list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.r2b.apps.lib.imageloader.ImageLoader
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.lib.tracker.Tracker
import com.r2b.apps.xuser.R
import com.r2b.apps.xuser.databinding.FragmentUserListBinding
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.ui.list.adapter.UserAdapter
import com.r2b.apps.xuser.ui.list.adapter.model.LoadingMoreListItem
import com.r2b.apps.xuser.ui.list.adapter.model.UserListItem
import com.r2b.apps.xuser.ui.list.adapter.model.UserListItemView
import com.r2b.apps.xuser.ui.common.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class UserListFragment : Fragment(R.layout.fragment_user_list) {

    // region injected vars

    @Inject lateinit var tracker: Tracker
    @Inject lateinit var logger: Logger
    @Inject lateinit var imageLoader: ImageLoader

    // endregion

    private val binding by viewBinding<FragmentUserListBinding>()
    private val viewModel: UserListViewModel by viewModels()

    // region volatile vars

    private val userAdapter: UserAdapter by lazy {
        UserAdapter({ user -> onUserSelectedAction(user) }, imageLoader)
    }

    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Replace with a ToolbarManager delegate
        setHasOptionsMenu(true)
    }

    // region menu

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
        menu.tintItem(resources, R.id.action_filter, R.color.ic_tint)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showFilterBar()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
        setupObservers()
        viewModel.load()
    }

    // region setupUI

    private fun setupUI() {
        with(binding) {
            recycler.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = userAdapter
                onSwipe { position -> onSwipe(position) }
                bottomPullToRefresh(layoutManager as LinearLayoutManager) { onBottomPullToRefreshAction() }
            }
        }
    }

    private fun onSwipe(position: Int) {
        val userListItemView: UserListItemView? = userAdapter.items?.get(position)
        (userListItemView as UserListItem).apply {
            showConfirm(R.string.confirm_undo, { onConfirmUndo(position) }, { onDismissUndoAction(position, user) })
        }
    }

    private fun onConfirmUndo(position: Int) {
        userAdapter.notifyItemChanged(position)
    }

    private fun showFilterBar() {
        binding.searchBar.visible()
    }

    private fun hideFilterBar() {
        binding.searchBar.gone()
    }

    private fun clearFilterBar() {
        binding.searchBar.editText?.text?.clear()
    }

    // endregion

    // region setupListener

    private fun setupListeners() {
        with(binding) {
            searchInput.textWatcher { onTextChangeAction(it) }
            searchBar.setEndIconOnClickListener { onFilterCloseAction() }
        }
    }

    // endregion

    // region setupObservers

    private fun setupObservers() {
        viewModel.apply {
            uiState.onEach(::renderUiState).launchIn(lifecycleScope)
            event.onEach(::renderEvent).launchIn(lifecycleScope)
        }
    }

    private fun renderUiState(state: UserListViewModel.UserUiState) {
        when (state) {
            UserListViewModel.UserUiState.Idle -> hideLoading()
            is UserListViewModel.UserUiState.Load -> resetUsers(state.users)
            is UserListViewModel.UserUiState.LoadMore -> {
                hideLoadingMore()
                addMoreUsers(state.users)
            }
            is UserListViewModel.UserUiState.Filter -> resetUsers(state.users)
            is UserListViewModel.UserUiState.FilterRemoved -> {
                clearFilterBar()
                hideFilterBar()
                resetUsers(state.users)
            }
        }
    }

    private fun renderEvent(event: UserListViewModel.Event) {
        when(event) {
            is UserListViewModel.Event.Loading -> showLoading()
            is UserListViewModel.Event.UserDeleted -> notifyUserDeleted(event.position)
            is UserListViewModel.Event.LoadingMore -> showLoadingMore()
            is UserListViewModel.Event.LastPage -> showLastPageInfo()
            is UserListViewModel.Event.UserSelected -> navigate(selectedUserAction())
            is UserListViewModel.Event.Error -> {
                hideLoading()
                showError(event.error)
            }
        }
    }
    private fun notifyUserDeleted(position: Int) {
        userAdapter.notifyItemRemoved(position)
    }

    private fun showLoading() {
        with(binding) {
            progress.visible()
        }
    }

    private fun hideLoading() {
        with(binding) {
            progress.gone()
        }
    }

    private fun showLoadingMore() {
        userAdapter.append(listOf(LoadingMoreListItem))
    }

    private fun hideLoadingMore() {
        userAdapter.dropLast()
    }

    private fun addMoreUsers(users: List<UserListItem>) {
        userAdapter.append(users)
    }

    private fun resetUsers(users: List<UserListItem>) {
        userAdapter.reset(users)
    }

    private fun showLastPageInfo() {
        prettyToast(getString(R.string.last_page))
    }

    // endregion

    // region user actions

    private fun onDismissUndoAction(position: Int, user: User) {
        viewModel.removeUser(position, user)
    }

    private fun onBottomPullToRefreshAction() {
        viewModel.nextPage()
    }

    private fun onUserSelectedAction(user: User) {
        viewModel.selectUser(user.id)
    }

    private fun onTextChangeAction(text: String) {
        viewModel.filter(text)
    }

    private fun onFilterCloseAction() {
        viewModel.removeFilter()
    }

    // endregion

    // region utils

    private fun UserListFragment.selectedUserAction() =
        UserListFragmentDirections.selectedUserAction()

    // endregion

}
