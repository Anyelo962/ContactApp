package com.example.contactapp.domain.usecase

import com.example.contactapp.domain.model.Contact
import com.example.contactapp.domain.repository.ContactRepository
import javax.inject.Inject

class GetContactByIdUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(id: Int): Result<Contact?> = repository.getContactById(id)
}

