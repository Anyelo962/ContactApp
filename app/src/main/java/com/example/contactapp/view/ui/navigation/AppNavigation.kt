package com.example.contactapp.view.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contactapp.view.ui.screens.contactdetail.ContactDetailScreen
import com.example.contactapp.view.ui.screens.contactlist.ContactListScreen
import com.example.contactapp.view.ui.screens.createcontact.CreateContactScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ContactList,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        }
    ) {
        composable<Screen.ContactList> {
            ContactListScreen(
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateContact())
                },
                onNavigateToDetail = { contactId ->
                    navController.navigate(Screen.ContactDetail(contactId = contactId))
                },
                onNavigateToEdit = { contactId ->
                    navController.navigate(Screen.CreateContact(contactId = contactId))
                }
            )
        }
        composable<Screen.CreateContact> {
            CreateContactScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<Screen.ContactDetail> {
            ContactDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { contactId ->
                    navController.navigate(Screen.CreateContact(contactId = contactId))
                }
            )
        }
    }
}
