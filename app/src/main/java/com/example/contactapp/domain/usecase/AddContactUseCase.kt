package com.example.contactapp.domain.usecase

import com.example.contactapp.domain.model.Contact
import com.example.contactapp.domain.repository.ContactRepository
import javax.inject.Inject

class AddContactUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(contact: Contact): Result<Unit> =
        repository.addContact(contact)
}

