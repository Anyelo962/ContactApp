package com.example.contactapp.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactapp.domain.model.Contact
import com.example.contactapp.domain.usecase.AddContactUseCase
import com.example.contactapp.domain.usecase.GetContactByIdUseCase
import com.example.contactapp.domain.usecase.GetRandomImageUrlUseCase
import com.example.contactapp.domain.usecase.UpdateContactUseCase
import com.example.contactapp.view.ui.screens.createcontact.CreateContactEvent
import com.example.contactapp.view.ui.screens.createcontact.CreateContactUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val PHONE_REGEX = Regex("^\\+?[0-9]{10,15}$")

@HiltViewModel
class CreateContactViewModel @Inject constructor(
    private val addContactUseCase: AddContactUseCase,
    private val updateContactUseCase: UpdateContactUseCase,
    private val getContactByIdUseCase: GetContactByIdUseCase,
    private val getRandomImageUrlUseCase: GetRandomImageUrlUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contactId: Int = savedStateHandle["contactId"] ?: -1
    private val isEditMode: Boolean = contactId != -1

    private val _uiState = MutableStateFlow(CreateContactUiState(isEditMode = isEditMode))
    val uiState: StateFlow<CreateContactUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateContactEvent>()
    val events = _events.asSharedFlow()

    init {
        if (isEditMode) {
            loadContactForEdit(contactId)
        } else {
            loadRandomImage()
        }
    }

    private fun loadContactForEdit(id: Int) {
        viewModelScope.launch {
            getContactByIdUseCase(id)
                .onSuccess { contact ->
                    if (contact != null) {
                        _uiState.update {
                            it.copy(
                                firstName = contact.firstName,
                                lastName = contact.lastName,
                                phone = contact.phone,
                                imageUrl = contact.imageUrl,
                                isEditMode = true
                            )
                        }
                    }
                }
                .onFailure {
                    _events.emit(
                        CreateContactEvent.ShowSnackbar("No se pudo cargar el contacto")
                    )
                }
        }
    }

    fun loadRandomImage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingImage = true, imageError = null) }
            getRandomImageUrlUseCase()
                .onSuccess { url ->
                    _uiState.update { it.copy(imageUrl = url, isLoadingImage = false) }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoadingImage = false,
                            imageError = "No se pudo cargar la imagen. Toca para reintentar."
                        )
                    }
                }
        }
    }

    fun onFirstNameChanged(value: String) =
        _uiState.update { it.copy(firstName = value, firstNameError = validateName(value)) }

    fun onLastNameChanged(value: String) =
        _uiState.update { it.copy(lastName = value, lastNameError = validateName(value)) }

    fun onPhoneChanged(value: String) =
        _uiState.update { it.copy(phone = value, phoneError = validatePhone(value)) }


    fun saveContact() {
        val state = _uiState.value

        val firstNameError = validateName(state.firstName)
        val lastNameError = validateName(state.lastName)
        val phoneError = validatePhone(state.phone)

        if (firstNameError != null || lastNameError != null || phoneError != null) {
            _uiState.update {
                it.copy(
                    firstNameError = firstNameError,
                    lastNameError = lastNameError,
                    phoneError = phoneError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val contact = Contact(
                id = if (isEditMode) contactId else 0,
                firstName = state.firstName.trim(),
                lastName = state.lastName.trim(),
                phone = state.phone.trim(),
                imageUrl = state.imageUrl
            )

            val result = if (isEditMode) {
                updateContactUseCase(contact)
            } else {
                addContactUseCase(contact)
            }

            result
                .onSuccess {
                    val message = if (isEditMode) "Contacto actualizado" else "Contacto guardado"
                    _events.emit(CreateContactEvent.ShowSnackbar(message))
                    _events.emit(CreateContactEvent.NavigateBack)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false) }
                    _events.emit(
                        CreateContactEvent.ShowSnackbar("Error al guardar: ${e.message}")
                    )
                }
        }
    }


    private fun validateName(value: String): String? = when {
        value.isBlank() -> "Este campo es obligatorio"
        value.trim().length < 2 -> "Ingresa al menos 2 caracteres"
        else -> null
    }

    private fun validatePhone(value: String): String? = when {
        value.isBlank() -> "Este campo es obligatorio"
        !value.matches(PHONE_REGEX) -> "Teléfono inválido (10–15 dígitos)"
        else -> null
    }
}
