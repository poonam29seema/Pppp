package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppConfig
import com.example.data.model.PaymentRequest
import com.example.data.model.SecurityLog
import com.example.data.model.User
import com.example.ui.components.QrCodeView
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.OnRoseContainer
import com.example.ui.theme.OnShieldGreenContainer
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.ShieldGreenContainer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    appConfig: AppConfig?,
    pendingPayments: List<PaymentRequest>,
    allPayments: List<PaymentRequest>,
    securityLogs: List<SecurityLog>,
    users: List<User>,
    onApprovePayment: (requestId: Long) -> Unit,
    onRejectPayment: (requestId: Long, reason: String) -> Unit,
    onUpdateConfig: (upiId: String, payeeName: String, amount: Double, strictMode: Boolean) -> Unit,
    onToggleUserBan: (userId: String, isBanned: Boolean) -> Unit,
    onTogglePrivacyShield: (userId: String, enabled: Boolean) -> Unit,
    onInstantUnlockDemo: () -> Unit,
    onLockChatDemo: () -> Unit,
    onExitAdminMode: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("भुगतान सत्यापन", "QR कोड व UPI", "सुरक्षा लॉग्स", "यूज़र नियंत्रण")

    // QR & UPI form state
    var editUpiId by remember(appConfig?.upiId) { mutableStateOf(appConfig?.upiId ?: "safemeet99@okhdfcbank") }
    var editPayeeName by remember(appConfig?.payeeName) { mutableStateOf(appConfig?.payeeName ?: "SafeMeet Payments") }
    var editAmount by remember(appConfig?.unlockAmount) { mutableStateOf((appConfig?.unlockAmount ?: 49.0).toInt().toString()) }
    var editStrictMode by remember(appConfig?.securityStrictMode) { mutableStateOf(appConfig?.securityStrictMode ?: true) }

    // Rejection dialog state
    var rejectingRequestId by remember { mutableStateOf<Long?>(null) }
    var rejectReasonText by remember { mutableStateOf("अमान्य UTR नंबर / स्क्रीनशॉट स्पष्ट नहीं है") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFF0F172A), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SafeMeet एडमिन पैनल",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Master Control & Verification System",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                },
                actions = {
                    Button(
                        onClick = onExitAdminMode,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        modifier = Modifier.testTag("exit_admin_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("यूज़र व्यू", fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Fast-track demo status bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "चैट स्थिति:",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val isUnlocked = appConfig?.isChatGloballyUnlockedForUser == true
                        Box(
                            modifier = Modifier
                                .background(if (isUnlocked) ShieldGreen else Color(0xFFE11D48), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isUnlocked) "UNLOCKED (सक्रिय)" else "LOCKED (₹49 देय)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Row {
                        OutlinedButton(
                            onClick = onInstantUnlockDemo,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ShieldGreen),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ShieldGreen),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("admin_instant_unlock")
                        ) {
                            Text("तुरंत अनलॉक", fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(
                            onClick = onLockChatDemo,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF43F5E)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF43F5E)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("लॉक करें", fontSize = 10.sp)
                        }
                    }
                }
            }

            // Tab Navigation
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                                if (index == 0 && pendingPayments.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(AmberWarning, CircleShape)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${pendingPayments.size}",
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> PaymentsVerificationTab(
                    pendingPayments = pendingPayments,
                    allPayments = allPayments,
                    onApprove = onApprovePayment,
                    onRejectClick = { reqId -> rejectingRequestId = reqId }
                )
                1 -> QrSettingsTab(
                    upiId = editUpiId,
                    payeeName = editPayeeName,
                    amount = editAmount,
                    strictMode = editStrictMode,
                    onUpiChange = { editUpiId = it },
                    onPayeeChange = { editPayeeName = it },
                    onAmountChange = { editAmount = it },
                    onStrictModeChange = { editStrictMode = it },
                    onSave = {
                        val amt = editAmount.toDoubleOrNull() ?: 49.0
                        onUpdateConfig(editUpiId, editPayeeName, amt, editStrictMode)
                        Toast.makeText(context, "QR एवं UPI सेटिंग्स सेव हो गई!", Toast.LENGTH_SHORT).show()
                    }
                )
                2 -> SecurityLogsTab(logs = securityLogs)
                3 -> UserControlTab(
                    users = users,
                    onToggleBan = onToggleUserBan,
                    onToggleShield = onTogglePrivacyShield
                )
            }
        }
    }

    // Rejection Reason Dialog
    if (rejectingRequestId != null) {
        AlertDialog(
            onDismissRequest = { rejectingRequestId = null },
            title = { Text("भुगतान अस्वीकार करें") },
            text = {
                Column {
                    Text("अस्वीकृति का कारण दर्ज करें:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectReasonText,
                        onValueChange = { rejectReasonText = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        rejectingRequestId?.let { id ->
                            onRejectPayment(id, rejectReasonText)
                        }
                        rejectingRequestId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("अस्वीकार करें")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectingRequestId = null }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
fun PaymentsVerificationTab(
    pendingPayments: List<PaymentRequest>,
    allPayments: List<PaymentRequest>,
    onApprove: (Long) -> Unit,
    onRejectClick: (Long) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "प्रलंबित सत्यापन (Pending Reviews): ${pendingPayments.size}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "उपयोगकर्ता द्वारा सबमिट किए गए ₹49 स्क्रीनशॉट एवं UTR को जांचकर स्वीकृत करें।",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }

        if (pendingPayments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "कोई नया प्रलंबित भुगतान नहीं है ✅",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        } else {
            items(pendingPayments, key = { it.id }) { req ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_payment_item_${req.id}"),
                    colors = CardDefaults.cardColors(containerColor = AmberWarning.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = req.userName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "₹${req.amount.toInt()}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = RosePrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "UTR / Ref: ${req.utrNumber}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF0F172A)
                        )

                        Text(
                            text = "फ़ाइल: ${req.screenshotInfo}",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )

                        Text(
                            text = "समय: ${dateFormat.format(Date(req.submittedAt))}",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Approve & Reject
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onApprove(req.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_approve_btn_${req.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = ShieldGreen)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("स्वीकृत करें (Approve)", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { onRejectClick(req.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("admin_reject_btn_${req.id}"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626))
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("अस्वीकार करें", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "सभी भुगतानों का इतिहास (${allPayments.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        items(allPayments.filter { !it.isPending }) { past ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "UTR: ${past.utrNumber}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "₹${past.amount.toInt()} • ${dateFormat.format(Date(past.submittedAt))}", fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                    Box(
                        modifier = Modifier
                            .background(if (past.isApproved) ShieldGreenContainer else Color(0xFFFFE4E6), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = past.status,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (past.isApproved) OnShieldGreenContainer else Color(0xFFBE123C)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QrSettingsTab(
    upiId: String,
    payeeName: String,
    amount: String,
    strictMode: Boolean,
    onUpiChange: (String) -> Unit,
    onPayeeChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onStrictModeChange: (Boolean) -> Unit,
    onSave: () -> Unit
) {
    val upiPayload = "upi://pay?pa=$upiId&pn=${android.net.Uri.encode(payeeName)}&am=$amount&cu=INR&tn=SafeMeet%20Chat%20Access"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "UPI QR कोड एवं भुगतान दर सेटिंग्स",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "यहाँ अपना नया UPI ID या QR कोड विवरण अपडेट करें। यह तुरंत यूज़र ऐप में दिखाई देगा।",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = upiId,
                        onValueChange = onUpiChange,
                        label = { Text("UPI ID (उदा. yourname@upi / phonepe / paytm)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_upi_id_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = payeeName,
                        onValueChange = onPayeeChange,
                        label = { Text("व्यापारी / प्राप्तकर्ता नाम (Merchant Name)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_payee_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = onAmountChange,
                        label = { Text("चैट अनलॉक शुल्क (₹)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_amount_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("महिला सुरक्षा एवं DLP फ़िल्टर", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("लड़कियों की ID पर फ़ोन नंबर/Instagram शेयर करने से रोकें", fontSize = 10.sp, color = Color(0xFF64748B))
                        }
                        Switch(
                            checked = strictMode,
                            onCheckedChange = onStrictModeChange
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onSave,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_qr_settings_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("परिवर्तन सुरक्षित करें (Save QR Settings)")
                    }
                }
            }
        }

        item {
            Text(
                text = "वर्तमान लाइव QR कोड प्रीव्यू",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                QrCodeView(
                    data = upiPayload,
                    modifier = Modifier.fillMaxWidth(0.55f)
                )
            }
            Text(
                text = "UPI स्ट्रिंग: $upiPayload",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun SecurityLogsTab(logs: List<SecurityLog>) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "महिला सुरक्षा उल्लंघन लॉग्स (${logs.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "लड़कियों की प्रोफाइल पर फ़ोन नंबर, Instagram या व्यक्तिगत डेटा भेजने के प्रयास यहाँ दर्ज होते हैं।",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }

        if (logs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ShieldGreenContainer)
                ) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "सब सुरक्षित है! कोई भी सुरक्षा उल्लंघन दर्ज नहीं हुआ।",
                            fontSize = 13.sp,
                            color = OnShieldGreenContainer
                        )
                    }
                }
            }
        } else {
            items(logs, key = { it.id }) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "लक्षित: ${log.targetUserName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF991B1B)
                            )
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFDC2626), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = log.violationType,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "ब्लॉक किया गया संदेश: \"${log.attemptedText}\"",
                            fontSize = 12.sp,
                            color = Color(0xFF7F1D1D)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = dateFormat.format(Date(log.timestamp)),
                            fontSize = 10.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserControlTab(
    users: List<User>,
    onToggleBan: (String, Boolean) -> Unit,
    onToggleShield: (String, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "उपयोगकर्ता नियंत्रण (${users.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "लड़के और लड़कियों के प्रोफाइल का प्रबंधन करें। सुरक्षा शील्ड सक्रिय/निष्क्रिय करें या बैन करें।",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }

        items(users, key = { it.id }) { user ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(user.avatarBgColor), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.take(1),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(${if (user.isFemale) "लड़की" else "लड़का"})",
                                    fontSize = 11.sp,
                                    color = if (user.isFemale) RosePrimary else Color(0xFF3B82F6)
                                )
                            }
                            Text(
                                text = "${user.city} • ${if (user.isBanned) "BANNED" else "Active"}",
                                fontSize = 10.sp,
                                color = if (user.isBanned) Color(0xFFDC2626) else Color(0xFF64748B)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (user.isFemale) {
                            IconButton(
                                onClick = { onToggleShield(user.id, !user.privacyShieldEnabled) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Shield",
                                    tint = if (user.privacyShieldEnabled) ShieldGreen else Color(0xFFCBD5E1)
                                )
                            }
                        }

                        IconButton(
                            onClick = { onToggleBan(user.id, !user.isBanned) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = "Ban",
                                tint = if (user.isBanned) Color(0xFFDC2626) else Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }
        }
    }
}
