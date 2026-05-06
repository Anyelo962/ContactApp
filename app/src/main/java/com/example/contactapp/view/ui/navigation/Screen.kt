package com.example.contactapp.view.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object ContactList : Screen

    @Serializable
    data class CreateContact(val contactId: Int = -1) : Screen

    @Serializable
    data class ContactDetail(val contactId: Int) : Screen
}
