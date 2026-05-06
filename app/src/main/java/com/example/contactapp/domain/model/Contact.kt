package com.example.contactapp.domain.model

data class Contact(
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val imageUrl: String
) {
    val fullName: String get() = "$firstName $lastName".trim()
}

