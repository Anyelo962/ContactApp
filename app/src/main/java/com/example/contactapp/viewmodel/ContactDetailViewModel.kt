package com.example.contactapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactapp.domain.model.Contact
import com.example.contactapp.domain.usecase.GetContactByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ContactDetailUiState {
    data object Loading : ContactDetailUiState
    data class Success(val contact: Contact) : ContactDetailUiState
    data class Error(val message: String) : ContactDetailUiState
}

@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    private val getContactByIdUseCase: GetContactByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contactId: Int = savedStateHandle["contactId"] ?: -1

    private val _uiState = MutableStateFlow<ContactDetailUiState>(ContactDetailUiState.Loading)
    val uiState: StateFlow<ContactDetailUiState> = _uiState.asStateFlow()

    init {
        loadContact()
    }

    private fun loadContact() {
        viewModelScope.launch {
            getContactByIdUseCase(contactId)
                .onSuccess { contact ->
                    _uiState.value = if (contact != null) {
                        ContactDetailUiState.Success(contact)
                    } else {
                        ContactDetailUiState.Error("Contacto no encontrado")
                    }
                }
                .onFailure {
                    _uiState.value = ContactDetailUiState.Error("No se pudo cargar el contacto")
                }
        }
    }
}
