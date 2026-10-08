package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val gender: String, // "FEMALE" or "MALE"
    val age: Int,
    val city: String,
    val bio: String,
    val interests: String,
    val avatarBgColor: Long = 0xFFFB7185,
    val isVerified: Boolean = true,
    val privacyShieldEnabled: Boolean = true,
    val isOnline: Boolean = true,
    val isBanned: Boolean = false
) {
    val isFemale: Boolean
        get() = gender.equals("FEMALE", ignoreCase = true)
}
