package com.example.contactapp.view.ui.screens.contactlist

import com.example.contactapp.domain.model.Contact

sealed interface ContactListUiState {
    data object Loading : ContactListUiState
    data object Empty : ContactListUiState
    data class Success(val contacts: List<Contact>) : ContactListUiState
    data class Error(val message: String) : ContactListUiState
}

