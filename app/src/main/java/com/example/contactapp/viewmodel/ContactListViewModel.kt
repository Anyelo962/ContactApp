package com.example.contactapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactapp.domain.usecase.DeleteAllContactsUseCase
import com.example.contactapp.domain.usecase.DeleteContactUseCase
import com.example.contactapp.domain.usecase.GetContactsUseCase
import com.example.contactapp.view.ui.screens.contactlist.ContactListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ContactListEvent {
    data class ShowSnackbar(val message: String) : ContactListEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val getContactsUseCase: GetContactsUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val deleteAllContactsUseCase: DeleteAllContactsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _events = MutableSharedFlow<ContactListEvent>()
    val events = _events.asSharedFlow()

    val uiState: StateFlow<ContactListUiState> = combine(
        getContactsUseCase(),
        _searchQuery
    ) { contacts, query ->
        val filtered = if (query.isBlank()) {
            contacts
        } else {
            val q = query.trim().lowercase()
            contacts.filter { contact ->
                contact.firstName.lowercase().contains(q) ||
                contact.lastName.lowercase().contains(q) ||
                contact.phone.contains(q) ||
                contact.fullName.lowercase().contains(q)
            }
        }
        when {
            filtered.isEmpty() && query.isBlank() -> ContactListUiState.Empty
            filtered.isEmpty() -> ContactListUiState.Success(emptyList())
            else -> ContactListUiState.Success(filtered)
        }
    }
        .catch { e ->
            emit(ContactListUiState.Error(e.message ?: "Error desconocido"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ContactListUiState.Loading
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun deleteContact(id: Int) {
        viewModelScope.launch {
            deleteContactUseCase(id)
                .onFailure { e ->
                    _events.emit(
                        ContactListEvent.ShowSnackbar("No se pudo eliminar: ${e.message}")
                    )
                }
        }
    }

    fun deleteAllContacts() {
        viewModelScope.launch {
            deleteAllContactsUseCase()
                .onFailure { e ->
                    _events.emit(
                        ContactListEvent.ShowSnackbar(
                            "No se pudo borrar: ${e.message}"
                        )
                    )
                }
        }
    }
}

