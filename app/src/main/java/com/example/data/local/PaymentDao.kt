package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PaymentRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment_requests ORDER BY submittedAt DESC")
    fun getAllPaymentRequests(): Flow<List<PaymentRequest>>

    @Query("SELECT * FROM payment_requests WHERE status = 'PENDING' ORDER BY submittedAt DESC")
    fun getPendingPaymentRequests(): Flow<List<PaymentRequest>>

    @Query("SELECT * FROM payment_requests WHERE userId = :userId ORDER BY submittedAt DESC LIMIT 1")
    fun getLatestPaymentForUser(userId: String): Flow<PaymentRequest?>

    @Query("SELECT * FROM payment_requests WHERE userId = :userId ORDER BY submittedAt DESC LIMIT 1")
    suspend fun getLatestPaymentForUserSync(userId: String): PaymentRequest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentRequest(paymentRequest: PaymentRequest): Long

    @Update
    suspend fun updatePaymentRequest(paymentRequest: PaymentRequest)

    @Query("UPDATE payment_requests SET status = :status, reviewedAt = :reviewedAt, rejectionReason = :rejectionReason WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, reviewedAt: Long, rejectionReason: String? = null)
}
