package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PaymentPaywallModal
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val activeUsers by viewModel.activeUsers.collectAsStateWithLifecycle()
    val allUsersForAdmin by viewModel.allUsersForAdmin.collectAsStateWithLifecycle()
    val appConfig by viewModel.appConfig.collectAsStateWithLifecycle()
    val latestPayment by viewModel.latestPayment.collectAsStateWithLifecycle()
    val pendingPayments by viewModel.pendingPayments.collectAsStateWithLifecycle()
    val allPayments by viewModel.allPayments.collectAsStateWithLifecycle()
    val securityLogs by viewModel.securityLogs.collectAsStateWithLifecycle()

    val selectedChatUser by viewModel.selectedChatUser.collectAsStateWithLifecycle()
    val currentChatMessages by viewModel.currentChatMessages.collectAsStateWithLifecycle()
    val genderFilter by viewModel.genderFilter.collectAsStateWithLifecycle()

    val showPaywallModal by viewModel.showPaywallModal.collectAsStateWithLifecycle()
    val isAdminModeActive by viewModel.isAdminModeActive.collectAsStateWithLifecycle()
    val showAdminPinDialog by viewModel.showAdminPinDialog.collectAsStateWithLifecycle()
    val securityViolationAlert by viewModel.securityViolationAlert.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isAdminModeActive -> {
                    BackHandler {
                        viewModel.exitAdminMode()
                    }
                    AdminPanelScreen(
                        appConfig = appConfig,
                        pendingPayments = pendingPayments,
                        allPayments = allPayments,
                        securityLogs = securityLogs,
                        users = allUsersForAdmin,
                        onApprovePayment = { reqId -> viewModel.adminApprovePayment(reqId) },
                        onRejectPayment = { reqId, reason -> viewModel.adminRejectPayment(reqId, reason) },
                        onUpdateConfig = { upi, payee, amt, strict ->
                            viewModel.adminUpdateConfig(upi, payee, amt, strict)
                        },
                        onToggleUserBan = { userId, isBanned ->
                            viewModel.adminToggleUserBan(userId, isBanned)
                        },
                        onTogglePrivacyShield = { userId, enabled ->
                            viewModel.adminTogglePrivacyShield(userId, enabled)
                        },
                        onInstantUnlockDemo = { viewModel.adminInstantUnlockDemo() },
                        onLockChatDemo = { viewModel.adminLockChatDemo() },
                        onExitAdminMode = { viewModel.exitAdminMode() }
                    )
                }

                selectedChatUser != null && (appConfig?.isChatGloballyUnlockedForUser == true) -> {
                    val user = selectedChatUser!!
                    BackHandler {
                        viewModel.closeChat()
                    }
                    ChatScreen(
                        user = user,
                        messages = currentChatMessages,
                        securityAlert = securityViolationAlert,
                        onSendMessage = { text -> viewModel.sendMessage(text) },
                        onDismissSecurityAlert = { viewModel.dismissSecurityAlert() },
                        onBack = { viewModel.closeChat() }
                    )
                }

                else -> {
                    HomeScreen(
                        users = activeUsers,
                        appConfig = appConfig,
                        latestPayment = latestPayment,
                        currentFilter = genderFilter,
                        onFilterChange = { filter -> viewModel.setGenderFilter(filter) },
                        onSelectUser = { user -> viewModel.selectUserForChat(user) },
                        onOpenPaywall = { viewModel.openPaywall() },
                        onPromptAdmin = { viewModel.promptAdminPin() },
                        showAdminPinDialog = showAdminPinDialog,
                        onDismissAdminPin = { viewModel.dismissAdminPin() },
                        onVerifyAdminPin = { pin -> viewModel.verifyAdminPin(pin) }
                    )
                }
            }

            // ₹49 Payment Paywall Modal Dialog
            if (showPaywallModal) {
                PaymentPaywallModal(
                    appConfig = appConfig,
                    latestPayment = latestPayment,
                    onSubmitPayment = { utr, note ->
                        viewModel.submitPayment(utr, note)
                    },
                    onAdminFastTrackApprove = {
                        viewModel.adminInstantUnlockDemo()
                    },
                    onDismiss = {
                        viewModel.closePaywall()
                    }
                )
            }
        }
    }
}
