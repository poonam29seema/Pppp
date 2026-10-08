package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AppConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface AppConfigDao {
    @Query("SELECT * FROM app_config WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<AppConfig?>

    @Query("SELECT * FROM app_config WHERE id = 1 LIMIT 1")
    suspend fun getConfigSync(): AppConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: AppConfig)

    @Query("UPDATE app_config SET isChatGloballyUnlockedForUser = :unlocked WHERE id = 1")
    suspend fun setChatUnlocked(unlocked: Boolean)

    @Query("UPDATE app_config SET upiId = :upiId, payeeName = :payeeName, unlockAmount = :amount WHERE id = 1")
    suspend fun updatePaymentDetails(upiId: String, payeeName: String, amount: Double)

    @Query("UPDATE app_config SET securityStrictMode = :enabled WHERE id = 1")
    suspend fun setSecurityStrictMode(enabled: Boolean)
}
