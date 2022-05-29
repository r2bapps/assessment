package com.r2b.apps.xuser.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.r2b.apps.lib.imageloader.ImageLoader
import com.r2b.apps.xuser.ui.detail.UserDetailScreen
import com.r2b.apps.xuser.ui.detail.UserDetailViewModel
import com.r2b.apps.xuser.ui.list.UserListScreen
import com.r2b.apps.xuser.ui.list.UserListViewModel
import kotlinx.serialization.Serializable

@Serializable
data object UserListRoute

@Serializable
data class UserDetailRoute(val userId: Int)

/**
 * Sets up navigation between the user list and detail screens.
 *
 * Accessibility: destinations use the labeled controls and accessible loading/error states of
 * their screens, following WCAG AA-oriented practices.
 */
@Composable
fun XuserNavigation(
    imageLoader: ImageLoader,
    navController: NavHostController = rememberNavController(),
) {
    XuserNavigationContent(
        imageLoader = imageLoader,
        navController = navController,
        listViewModel = { hiltViewModel<UserListViewModel>() },
        detailViewModel = { hiltViewModel<UserDetailViewModel>() },
    )
}

@Composable
internal fun XuserNavigationWithViewModels(
    imageLoader: ImageLoader,
    listViewModel: UserListViewModel,
    detailViewModel: UserDetailViewModel,
    navController: NavHostController = rememberNavController(),
) {
    XuserNavigationContent(
        imageLoader = imageLoader,
        navController = navController,
        listViewModel = { listViewModel },
        detailViewModel = { detailViewModel },
    )
}

@Composable
private fun XuserNavigationContent(
    imageLoader: ImageLoader,
    navController: NavHostController,
    listViewModel: @Composable () -> UserListViewModel,
    detailViewModel: @Composable () -> UserDetailViewModel,
) {
    NavHost(navController = navController, startDestination = UserListRoute) {
        composable<UserListRoute> {
            UserListScreen(
                imageLoader = imageLoader,
                onUserSelected = { userId -> navController.navigate(UserDetailRoute(userId)) },
                viewModel = listViewModel(),
            )
        }
        composable<UserDetailRoute> { entry ->
            val route = entry.toRoute<UserDetailRoute>()
            UserDetailScreen(
                userId = route.userId,
                imageLoader = imageLoader,
                onBack = { navController.popBackStack() },
                viewModel = detailViewModel(),
            )
        }
    }
}
