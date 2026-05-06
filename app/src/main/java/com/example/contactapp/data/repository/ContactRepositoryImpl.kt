package com.example.contactapp.data.repository

import com.example.contactapp.data.local.dao.ContactDao
import com.example.contactapp.data.mapper.toDomain
import com.example.contactapp.data.mapper.toEntity
import com.example.contactapp.data.remote.api.PicsumApi
import com.example.contactapp.domain.model.Contact
import com.example.contactapp.domain.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

class ContactRepositoryImpl @Inject constructor(
    private val dao: ContactDao,
    private val api: PicsumApi
) : ContactRepository {

    override fun getContacts(): Flow<List<Contact>> =
        dao.getAllContacts().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getContactById(id: Int): Result<Contact?> =
        withContext(Dispatchers.IO) {
            runCatching { dao.getById(id)?.toDomain() }
        }

    override suspend fun addContact(contact: Contact): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching { dao.insert(contact.toEntity()) }
        }

    override suspend fun updateContact(contact: Contact): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching { dao.update(contact.toEntity()) }
        }

    override suspend fun deleteContact(id: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching { dao.deleteById(id) }
        }

    override suspend fun deleteAllContacts(): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching { dao.deleteAll() }
        }

    override suspend fun getRandomImageUrl(): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val randomId = Random.nextInt(0, 1000)
                val dto = api.getImageInfo(randomId)
                "https://picsum.photos/id/${dto.id}/400/400"
            }
        }
}

