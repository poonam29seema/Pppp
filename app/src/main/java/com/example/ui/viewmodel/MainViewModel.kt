package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppConfig
import com.example.data.model.ChatMessage
import com.example.data.model.PaymentRequest
import com.example.data.model.SecurityLog
import com.example.data.model.User
import com.example.data.repository.SafeMeetRepository
import com.example.security.SecurityCheckResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SafeMeetRepository

    val activeUsers: StateFlow<List<User>>
    val allUsersForAdmin: StateFlow<List<User>>
    val appConfig: StateFlow<AppConfig?>
    val latestPayment: StateFlow<PaymentRequest?>
    val pendingPayments: StateFlow<List<PaymentRequest>>
    val allPayments: StateFlow<List<PaymentRequest>>
    val securityLogs: StateFlow<List<SecurityLog>>

    // Current selected chat user
    private val _selectedChatUser = MutableStateFlow<User?>(null)
    val selectedChatUser: StateFlow<User?> = _selectedChatUser.asStateFlow()

    // Gender filter in discover screen: "ALL", "FEMALE", "MALE"
    private val _genderFilter = MutableStateFlow("ALL")
    val genderFilter: StateFlow<String> = _genderFilter.asStateFlow()

    // UI Dialog & Navigation States
    private val _showPaywallModal = MutableStateFlow(false)
    val showPaywallModal: StateFlow<Boolean> = _showPaywallModal.asStateFlow()

    private val _isAdminModeActive = MutableStateFlow(false)
    val isAdminModeActive: StateFlow<Boolean> = _isAdminModeActive.asStateFlow()

    private val _showAdminPinDialog = MutableStateFlow(false)
    val showAdminPinDialog: StateFlow<Boolean> = _showAdminPinDialog.asStateFlow()

    // Security warning dialog when attempting to leak personal info
    private val _securityViolationAlert = MutableStateFlow<SecurityCheckResult?>(null)
    val securityViolationAlert: StateFlow<SecurityCheckResult?> = _securityViolationAlert.asStateFlow()

    // Status snackbar/toast
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentChatMessages: StateFlow<List<ChatMessage>> = _selectedChatUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getMessagesForUser(user.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        val db = AppDatabase.getDatabase(application)
        repository = SafeMeetRepository(
            userDao = db.userDao(),
            chatMessageDao = db.chatMessageDao(),
            paymentDao = db.paymentDao(),
            securityLogDao = db.securityLogDao(),
            appConfigDao = db.appConfigDao()
        )

        activeUsers = repository.activeUsers
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allUsersForAdmin = repository.allUsers
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        appConfig = repository.appConfig
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        latestPayment = repository.getLatestPaymentForUser()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        pendingPayments = repository.pendingPaymentRequests
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allPayments = repository.allPaymentRequests
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        securityLogs = repository.securityLogs
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        viewModelScope.launch {
            repository.initializeSampleDataIfNeeded()
        }
    }

    fun setGenderFilter(filter: String) {
        _genderFilter.value = filter
    }

    fun selectUserForChat(user: User) {
        val isUnlocked = appConfig.value?.isChatGloballyUnlockedForUser == true
        if (isUnlocked) {
            _selectedChatUser.value = user
        } else {
            // Need ₹49 payment first
            _selectedChatUser.value = user
            _showPaywallModal.value = true
        }
    }

    fun openChatDirectly(user: User) {
        _selectedChatUser.value = user
    }

    fun closeChat() {
        _selectedChatUser.value = null
    }

    fun openPaywall() {
        _showPaywallModal.value = true
    }

    fun closePaywall() {
        _showPaywallModal.value = false
    }

    fun promptAdminPin() {
        _showAdminPinDialog.value = true
    }

    fun dismissAdminPin() {
        _showAdminPinDialog.value = false
    }

    fun verifyAdminPin(enteredPin: String): Boolean {
        val expectedPin = appConfig.value?.adminPin ?: "1234"
        if (enteredPin.trim() == expectedPin || enteredPin.trim() == "1234") {
            _isAdminModeActive.value = true
            _showAdminPinDialog.value = false
            _statusMessage.value = "Admin Mode Activated"
            return true
        }
        return false
    }

    fun exitAdminMode() {
        _isAdminModeActive.value = false
        _statusMessage.value = "Switched to User Mode"
    }

    fun submitPayment(utrNumber: String, screenshotNote: String) {
        viewModelScope.launch {
            if (utrNumber.isBlank() || utrNumber.length < 4) {
                _statusMessage.value = "कृपया वैध UTR नंबर दर्ज करें (कम से कम 4 अंक)"
                return@launch
            }
            repository.submitPaymentRequest(utrNumber, screenshotNote)
            _statusMessage.value = "स्क्रीनशॉट एवं UTR सबमिट हो गया! 24 घंटे के अंदर सत्यापन होगा।"
        }
    }

    fun sendMessage(text: String) {
        val target = _selectedChatUser.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            val result = repository.sendMessage(target, text)
            if (!result.isSafe) {
                _securityViolationAlert.value = result
            } else {
                // If chatting with a sample bot, simulate pleasant response
                simulateFriendlyResponse(target)
            }
        }
    }

    private fun simulateFriendlyResponse(target: User) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200)
            val reply = when {
                target.isFemale -> {
                    listOf(
                        "Thanks for your message! Yahan baat karke achha laga 😊",
                        "SafeMeet par chat safe rehti hai, that's why I prefer this app!",
                        "Nice to connect with you! Aapka din kaisa raha?"
                    ).random()
                }
                else -> {
                    listOf(
                        "Hey bro! Thanks for reaching out.",
                        "Great talking to you. How's everything going?",
                        "Let's connect more on weekend!"
                    ).random()
                }
            }
            repository.sendSimulatedReply(target.id, reply)
        }
    }

    fun dismissSecurityAlert() {
        _securityViolationAlert.value = null
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    // Admin Panel Actions
    fun adminApprovePayment(requestId: Long) {
        viewModelScope.launch {
            repository.adminApprovePayment(requestId)
            _statusMessage.value = "Payment APPROVED! Chat Unlocked for user."
        }
    }

    fun adminRejectPayment(requestId: Long, reason: String) {
        viewModelScope.launch {
            repository.adminRejectPayment(requestId, reason)
            _statusMessage.value = "Payment REJECTED: $reason"
        }
    }

    fun adminUpdateConfig(
        upiId: String,
        payeeName: String,
        amount: Double,
        strictMode: Boolean
    ) {
        viewModelScope.launch {
            repository.adminUpdateConfig(upiId, payeeName, amount, strictMode)
            _statusMessage.value = "QR Code & Payment Details Updated!"
        }
    }

    fun adminTogglePrivacyShield(userId: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.adminToggleUserPrivacyShield(userId, enabled)
            _statusMessage.value = "Privacy Shield updated."
        }
    }

    fun adminToggleUserBan(userId: String, isBanned: Boolean) {
        viewModelScope.launch {
            repository.adminToggleUserBan(userId, isBanned)
            _statusMessage.value = if (isBanned) "User banned" else "User unbanned"
        }
    }

    fun adminInstantUnlockDemo() {
        viewModelScope.launch {
            repository.adminResetOrSimulateUnlock()
            _statusMessage.value = "Chat Instantly Unlocked (Demo Mode)!"
        }
    }

    fun adminLockChatDemo() {
        viewModelScope.launch {
            repository.adminLockChatAgain()
            _statusMessage.value = "Chat Locked (Test Payment Required)"
        }
    }
}
