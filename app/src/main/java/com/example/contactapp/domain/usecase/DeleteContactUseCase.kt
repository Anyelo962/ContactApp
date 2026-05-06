package com.example.contactapp.domain.usecase

import com.example.contactapp.domain.repository.ContactRepository
import javax.inject.Inject

class DeleteContactUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> = repository.deleteContact(id)
}

