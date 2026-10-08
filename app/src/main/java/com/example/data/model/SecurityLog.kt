package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_logs")
data class SecurityLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetUserName: String,
    val attemptedText: String,
    val violationType: String, // "PHONE_NUMBER", "INSTAGRAM", "EMAIL", "WHATSAPP_LINK"
    val timestamp: Long = System.currentTimeMillis()
)
