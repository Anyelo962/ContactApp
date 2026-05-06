package com.example.contactapp.domain.usecase

import com.example.contactapp.domain.repository.ContactRepository
import javax.inject.Inject

class GetRandomImageUrlUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(): Result<String> = repository.getRandomImageUrl()
}

