package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isBanned = 0 ORDER BY id ASC")
    fun getAllActiveUsers(): Flow<List<User>>

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsersIncludingBanned(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users WHERE gender = :gender AND isBanned = 0")
    fun getUsersByGender(gender: String): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET isBanned = :isBanned WHERE id = :userId")
    suspend fun setUserBannedStatus(userId: String, isBanned: Boolean)

    @Query("UPDATE users SET privacyShieldEnabled = :enabled WHERE id = :userId")
    suspend fun updatePrivacyShield(userId: String, enabled: Boolean)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
