package com.example.data.repository

import com.example.data.local.AppConfigDao
import com.example.data.local.ChatMessageDao
import com.example.data.local.PaymentDao
import com.example.data.local.SecurityLogDao
import com.example.data.local.UserDao
import com.example.data.model.AppConfig
import com.example.data.model.ChatMessage
import com.example.data.model.PaymentRequest
import com.example.data.model.SecurityLog
import com.example.data.model.User
import com.example.security.PrivacyGuard
import com.example.security.SecurityCheckResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class SafeMeetRepository(
    private val userDao: UserDao,
    private val chatMessageDao: ChatMessageDao,
    private val paymentDao: PaymentDao,
    private val securityLogDao: SecurityLogDao,
    private val appConfigDao: AppConfigDao
) {
    val activeUsers: Flow<List<User>> = userDao.getAllActiveUsers()
    val allUsers: Flow<List<User>> = userDao.getAllUsersIncludingBanned()
    val appConfig: Flow<AppConfig?> = appConfigDao.getConfig()
    val allPaymentRequests: Flow<List<PaymentRequest>> = paymentDao.getAllPaymentRequests()
    val pendingPaymentRequests: Flow<List<PaymentRequest>> = paymentDao.getPendingPaymentRequests()
    val securityLogs: Flow<List<SecurityLog>> = securityLogDao.getAllSecurityLogs()

    suspend fun initializeSampleDataIfNeeded() {
        val userCount = userDao.getUserCount()
        if (userCount == 0) {
            val sampleUsers = listOf(
                User(
                    id = "girl_1",
                    name = "Priya Sharma",
                    gender = "FEMALE",
                    age = 22,
                    city = "Delhi",
                    bio = "Coffee lover ☕ | Architecture student | Music & long walks. Yahan sirf respectful batein karein.",
                    interests = "Books, Art, Travel, Indie Music",
                    avatarBgColor = 0xFFFB7185,
                    isVerified = true,
                    privacyShieldEnabled = true,
                    isOnline = true
                ),
                User(
                    id = "girl_2",
                    name = "Ananya Verma",
                    gender = "FEMALE",
                    age = 24,
                    city = "Mumbai",
                    bio = "Fashion enthusiast ✨ | Loves trying street food | Looking for genuine friendships.",
                    interests = "Photography, Cooking, Bollywood, Dogs",
                    avatarBgColor = 0xFFF43F5E,
                    isVerified = true,
                    privacyShieldEnabled = true,
                    isOnline = true
                ),
                User(
                    id = "girl_3",
                    name = "Sneha Patel",
                    gender = "FEMALE",
                    age = 23,
                    city = "Ahmedabad",
                    bio = "Tech geek 👩‍💻 | Weekend badminton player | Simplicity is key.",
                    interests = "Coding, Badminton, Movies, Road Trips",
                    avatarBgColor = 0xFFE11D48,
                    isVerified = true,
                    privacyShieldEnabled = true,
                    isOnline = false
                ),
                User(
                    id = "boy_1",
                    name = "Rohan Malhotra",
                    gender = "MALE",
                    age = 25,
                    city = "Bangalore",
                    bio = "Software Engineer 💻 | Gym enthusiast | Love cafe hopping and exploring new cities.",
                    interests = "Fitness, Tech, Coffee, Gaming",
                    avatarBgColor = 0xFF3B82F6,
                    isVerified = true,
                    privacyShieldEnabled = false,
                    isOnline = true
                ),
                User(
                    id = "boy_2",
                    name = "Kabir Singh",
                    gender = "MALE",
                    age = 26,
                    city = "Chandigarh",
                    bio = "Musician & Guitarist 🎸 | Sunset chaser | Always up for good conversations.",
                    interests = "Music, Acoustic Guitar, Traveling, Poetry",
                    avatarBgColor = 0xFF6366F1,
                    isVerified = true,
                    privacyShieldEnabled = false,
                    isOnline = true
                ),
                User(
                    id = "boy_3",
                    name = "Aman Gupta",
                    gender = "MALE",
                    age = 24,
                    city = "Jaipur",
                    bio = "Foodie at heart 🥘 | Entrepreneur | Passionate about photography & history.",
                    interests = "Food, History, Photography, Business",
                    avatarBgColor = 0xFF0EA5E9,
                    isVerified = true,
                    privacyShieldEnabled = false,
                    isOnline = false
                )
            )
            userDao.insertUsers(sampleUsers)

            // Initial friendly introductory message in sample chats
            chatMessageDao.insertMessage(
                ChatMessage(
                    conversationUserId = "girl_1",
                    text = "Namaste! Welcome to SafeMeet. Privacy shield is enabled on my profile for safe conversation 🛡️",
                    isFromMe = false,
                    timestamp = System.currentTimeMillis() - 3600000
                )
            )
            chatMessageDao.insertMessage(
                ChatMessage(
                    conversationUserId = "boy_1",
                    text = "Hey! Let me know if you want to connect and chat anytime.",
                    isFromMe = false,
                    timestamp = System.currentTimeMillis() - 7200000
                )
            )
        }

        val config = appConfigDao.getConfigSync()
        if (config == null) {
            appConfigDao.insertConfig(
                AppConfig(
                    id = 1,
                    upiId = "safemeet99@okhdfcbank",
                    payeeName = "SafeMeet Payments",
                    unlockAmount = 49.0,
                    adminPin = "1234",
                    isChatGloballyUnlockedForUser = false,
                    securityStrictMode = true
                )
            )
        }
    }

    fun getMessagesForUser(userId: String): Flow<List<ChatMessage>> {
        return chatMessageDao.getMessagesForUser(userId)
    }

    fun getLatestPaymentForUser(userId: String = "me_user"): Flow<PaymentRequest?> {
        return paymentDao.getLatestPaymentForUser(userId)
    }

    suspend fun isChatUnlocked(): Boolean {
        val config = appConfigDao.getConfigSync()
        return config?.isChatGloballyUnlockedForUser == true
    }

    /**
     * Sends message with strict Data Loss Prevention for female profiles.
     * If personal info is detected on a girl's profile, it blocks the message and records a security incident.
     */
    suspend fun sendMessage(
        targetUser: User,
        messageText: String
    ): SecurityCheckResult {
        val config = appConfigDao.getConfigSync()
        val strictMode = config?.securityStrictMode ?: true

        val check = PrivacyGuard.analyzeMessage(
            rawText = messageText,
            isTargetFemale = targetUser.isFemale,
            strictMode = strictMode && targetUser.privacyShieldEnabled
        )

        if (!check.isSafe) {
            // Log security violation for Admin inspection
            securityLogDao.insertLog(
                SecurityLog(
                    targetUserName = "${targetUser.name} (${targetUser.gender})",
                    attemptedText = messageText,
                    violationType = check.violationType ?: "PERSONAL_INFO_LEAK"
                )
            )

            // Save blocked message marked as blocked so user sees feedback
            chatMessageDao.insertMessage(
                ChatMessage(
                    conversationUserId = targetUser.id,
                    text = check.sanitizedText,
                    isFromMe = true,
                    isBlockedOrMasked = true,
                    securityViolationReason = check.reasonHindi
                )
            )
            return check
        }

        // Safe message: save directly
        chatMessageDao.insertMessage(
            ChatMessage(
                conversationUserId = targetUser.id,
                text = messageText,
                isFromMe = true,
                isBlockedOrMasked = false
            )
        )

        return check
    }

    suspend fun sendSimulatedReply(targetUserId: String, replyText: String) {
        chatMessageDao.insertMessage(
            ChatMessage(
                conversationUserId = targetUserId,
                text = replyText,
                isFromMe = false
            )
        )
    }

    suspend fun submitPaymentRequest(
        utrNumber: String,
        screenshotName: String = "upi_receipt_screenshot.png"
    ): Long {
        val config = appConfigDao.getConfigSync()
        val amount = config?.unlockAmount ?: 49.0

        val request = PaymentRequest(
            userId = "me_user",
            userName = "Current User",
            amount = amount,
            utrNumber = utrNumber.trim(),
            screenshotInfo = screenshotName,
            submittedAt = System.currentTimeMillis(),
            status = "PENDING"
        )
        return paymentDao.insertPaymentRequest(request)
    }

    suspend fun adminApprovePayment(requestId: Long) {
        paymentDao.updateStatus(
            id = requestId,
            status = "APPROVED",
            reviewedAt = System.currentTimeMillis()
        )
        appConfigDao.setChatUnlocked(true)
    }

    suspend fun adminRejectPayment(requestId: Long, reason: String) {
        paymentDao.updateStatus(
            id = requestId,
            status = "REJECTED",
            reviewedAt = System.currentTimeMillis(),
            rejectionReason = reason
        )
    }

    suspend fun adminUpdateConfig(
        upiId: String,
        payeeName: String,
        amount: Double,
        strictMode: Boolean
    ) {
        appConfigDao.updatePaymentDetails(upiId.trim(), payeeName.trim(), amount)
        appConfigDao.setSecurityStrictMode(strictMode)
    }

    suspend fun adminToggleUserBan(userId: String, isBanned: Boolean) {
        userDao.setUserBannedStatus(userId, isBanned)
    }

    suspend fun adminToggleUserPrivacyShield(userId: String, enabled: Boolean) {
        userDao.updatePrivacyShield(userId, enabled)
    }

    suspend fun adminResetOrSimulateUnlock() {
        appConfigDao.setChatUnlocked(true)
    }

    suspend fun adminLockChatAgain() {
        appConfigDao.setChatUnlocked(false)
    }
}
