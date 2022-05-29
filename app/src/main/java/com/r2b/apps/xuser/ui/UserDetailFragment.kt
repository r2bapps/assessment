package com.r2b.apps.xuser.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.r2b.apps.lib.imageloader.ImageLoader
import com.r2b.apps.lib.logger.Logger
import com.r2b.apps.lib.tracker.Tracker
import com.r2b.apps.xuser.R
import com.r2b.apps.xuser.databinding.FragmentUserDetailBinding
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.ui.common.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class UserDetailFragment : Fragment(R.layout.fragment_user_detail) {

    @Inject lateinit var tracker: Tracker
    @Inject lateinit var logger: Logger
    @Inject lateinit var imageLoader: ImageLoader

    private val binding by viewBinding<FragmentUserDetailBinding>()
    private val viewModel: UserDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        setupObservers()
        viewModel.load()
    }

    private fun setUI() {
        with (binding) {
            image.gone()
            nameLabel.gone()
            descriptionLabel.gone()
            surnameLabel.gone()
            emailLabel.gone()
            genderLabel.gone()
            addressLabel.gone()
            registeredDateLabel.gone()
            image.gone()
            name.gone()
            description.gone()
            surname.gone()
            email.gone()
            gender.gone()
            address.gone()
            registeredDate.gone()
        }
    }

    private fun setupObservers() {
        viewModel.apply {
            uiState.onEach(::renderUiState).launchIn(lifecycleScope)
            event.onEach(::renderEvent).launchIn(lifecycleScope)
        }
    }

    private fun renderUiState(state: UserDetailViewModel.UserUiState) {
        when (state) {
            UserDetailViewModel.UserUiState.Loading -> Unit
            UserDetailViewModel.UserUiState.Idle -> Unit
            is UserDetailViewModel.UserUiState.Success -> {
                updateUser(state.user)
            }
        }
    }

    private fun renderEvent(event: UserDetailViewModel.Event) {
        when(event) {
            is UserDetailViewModel.Event.Error -> { showError(event.error) }
        }
    }

    private fun updateUser(user: User?) {
        if (user != null) {
            with(binding) {
                showIfNotEmpty(image, user.picture)
                showIfNotEmpty(nameLabel, name, user.name)
                showIfNotEmpty(descriptionLabel, description, user.description)
                showIfNotEmpty(surnameLabel, surname, user.surname)
                showIfNotEmpty(emailLabel, email, user.email)
                showIfNotEmpty(genderLabel, gender, user.gender)
                showIfNotEmpty(addressLabel, address, user.location)
                showIfNotEmpty(registeredDateLabel, registeredDate, user.registeredDate.safeLocalizedDate())
            }
        }
    }

    private fun showIfNotEmpty(label: TextView, item: TextView, text: String) {
        if (text.isNotEmpty()) {
            label.visible()
            item.visible()
            item.text = text
        }
    }

    private fun showIfNotEmpty(image: ImageView, url: String) {
        if (url.isNotEmpty()) {
            image.visible()
            imageLoader.load(url, image, R.drawable.ic_camera)
        }
    }

}
