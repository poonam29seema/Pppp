package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_config")
data class AppConfig(
    @PrimaryKey val id: Int = 1,
    val upiId: String = "safemeet99@okhdfcbank",
    val payeeName: String = "SafeMeet Payments",
    val unlockAmount: Double = 49.0,
    val adminPin: String = "1234",
    val isChatGloballyUnlockedForUser: Boolean = false,
    val securityStrictMode: Boolean = true
)
