package com.example.contactapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.contactapp.data.local.dao.ContactDao
import com.example.contactapp.data.local.entity.ContactEntity

@Database(
    entities = [ContactEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}

