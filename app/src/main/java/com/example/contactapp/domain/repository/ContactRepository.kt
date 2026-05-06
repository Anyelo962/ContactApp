package com.example.contactapp.domain.repository

import com.example.contactapp.domain.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getContacts(): Flow<List<Contact>>
    suspend fun getContactById(id: Int): Result<Contact?>
    suspend fun addContact(contact: Contact): Result<Unit>
    suspend fun updateContact(contact: Contact): Result<Unit>
    suspend fun deleteContact(id: Int): Result<Unit>
    suspend fun deleteAllContacts(): Result<Unit>
    suspend fun getRandomImageUrl(): Result<String>
}

