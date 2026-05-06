package com.example.contactapp.domain.usecase

import com.example.contactapp.domain.model.Contact
import com.example.contactapp.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    operator fun invoke(): Flow<List<Contact>> = repository.getContacts()
}

