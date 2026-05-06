package com.example.contactapp.data.mapper

import com.example.contactapp.data.local.entity.ContactEntity
import com.example.contactapp.domain.model.Contact

fun ContactEntity.toDomain(): Contact = Contact(
    id = id,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    imageUrl = imageUrl
)

fun Contact.toEntity(): ContactEntity = ContactEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    phone = phone,
    imageUrl = imageUrl
)

