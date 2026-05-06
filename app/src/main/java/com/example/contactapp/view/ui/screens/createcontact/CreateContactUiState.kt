package com.example.contactapp.view.ui.screens.createcontact

data class CreateContactUiState(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val imageUrl: String = "",
    val isLoadingImage: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneError: String? = null,
    val imageError: String? = null
) {
    val hasUnsavedData: Boolean
        get() = firstName.isNotBlank() || lastName.isNotBlank() || phone.isNotBlank()

    val isFormValid: Boolean
        get() = firstNameError == null && lastNameError == null && phoneError == null &&
                firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()
}

sealed interface CreateContactEvent {
    data object NavigateBack : CreateContactEvent
    data class ShowSnackbar(val message: String) : CreateContactEvent
}
