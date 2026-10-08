package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_requests")
data class PaymentRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "me_user",
    val userName: String = "You (User)",
    val amount: Double = 49.0,
    val utrNumber: String,
    val screenshotInfo: String = "payment_proof_receipt.jpg",
    val submittedAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val rejectionReason: String? = null,
    val reviewedAt: Long? = null
) {
    val isPending: Boolean get() = status == "PENDING"
    val isApproved: Boolean get() = status == "APPROVED"
    val isRejected: Boolean get() = status == "REJECTED"

    // 24 hours in milliseconds = 24 * 60 * 60 * 1000 = 86,400,000L
    val expiryDeadlineMillis: Long get() = submittedAt + 86_400_000L
}
