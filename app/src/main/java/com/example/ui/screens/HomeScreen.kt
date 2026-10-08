package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppConfig
import com.example.data.model.PaymentRequest
import com.example.data.model.User
import com.example.ui.components.PrivacyInfoBanner
import com.example.ui.components.PrivacyShieldBadge
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnRoseContainer
import com.example.ui.theme.OnShieldGreenContainer
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.ShieldGreenContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    users: List<User>,
    appConfig: AppConfig?,
    latestPayment: PaymentRequest?,
    currentFilter: String,
    onFilterChange: (String) -> Unit,
    onSelectUser: (User) -> Unit,
    onOpenPaywall: () -> Unit,
    onPromptAdmin: () -> Unit,
    showAdminPinDialog: Boolean,
    onDismissAdminPin: () -> Unit,
    onVerifyAdminPin: (String) -> Unit
) {
    var adminPinInput by remember { mutableStateOf("") }
    val isChatUnlocked = appConfig?.isChatGloballyUnlockedForUser == true
    val isPaymentPending = latestPayment?.isPending == true

    val filteredUsers = when (currentFilter) {
        "FEMALE" -> users.filter { it.isFemale }
        "MALE" -> users.filter { !it.isFemale }
        else -> users
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(RosePrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "SafeMeet",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = ShieldGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = "लड़के-लड़कियां सुरक्षित चैट",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                },
                actions = {
                    // Chat Access Status Pill
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onOpenPaywall() }
                            .testTag("chat_status_pill"),
                        color = when {
                            isChatUnlocked -> ShieldGreenContainer
                            isPaymentPending -> AmberContainer
                            else -> RoseContainer
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when {
                                    isChatUnlocked -> Icons.Default.CheckCircle
                                    isPaymentPending -> Icons.Default.HourglassTop
                                    else -> Icons.Default.Lock
                                },
                                contentDescription = null,
                                tint = when {
                                    isChatUnlocked -> OnShieldGreenContainer
                                    isPaymentPending -> OnAmberContainer
                                    else -> RosePrimary
                                },
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when {
                                    isChatUnlocked -> "चैट अनलॉक"
                                    isPaymentPending -> "24h रिव्यू"
                                    else -> "₹49 पास"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isChatUnlocked -> OnShieldGreenContainer
                                    isPaymentPending -> OnAmberContainer
                                    else -> OnRoseContainer
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Admin Panel Button
                    IconButton(
                        onClick = onPromptAdmin,
                        modifier = Modifier.testTag("admin_panel_icon_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Control",
                            tint = Color(0xFF0F172A)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Safety Info Banner (Emphasizing girl privacy & no personal info sharing)
                PrivacyInfoBanner()
            }

            // Quick Status Reminder if pending
            if (isPaymentPending && latestPayment != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenPaywall() },
                        colors = CardDefaults.cardColors(containerColor = AmberContainer.copy(alpha = 0.8f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = AmberWarning,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "₹49 सत्यापन प्रगति पर है (24 घंटे में चालू)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = OnAmberContainer
                                )
                                Text(
                                    text = "UTR: ${latestPayment.utrNumber} • समय सीमा देखने के लिए टैप करें",
                                    fontSize = 10.sp,
                                    color = OnAmberContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // Gender Filter Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "ALL" to "सभी प्रोफ़ाइल",
                        "FEMALE" to "लड़कियां 🛡️",
                        "MALE" to "लड़के"
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = currentFilter == key,
                            onClick = { onFilterChange(key) },
                            label = { Text(label, fontSize = 12.sp, fontWeight = if (currentFilter == key) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RosePrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_$key")
                        )
                    }
                }
            }

            // Profile Cards
            items(filteredUsers, key = { it.id }) { user ->
                UserProfileCard(
                    user = user,
                    isChatUnlocked = isChatUnlocked,
                    isPaymentPending = isPaymentPending,
                    onChatClick = { onSelectUser(user) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Admin PIN Authentication Dialog
    if (showAdminPinDialog) {
        AlertDialog(
            onDismissRequest = onDismissAdminPin,
            icon = {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text("एडमिन प्रमाणीकरण (Admin Access)")
            },
            text = {
                Column {
                    Text(
                        text = "QR कोड बदलने, ₹49 पेमेंट सत्यापन करने और सुरक्षा लॉग देखने के लिए एडमिन पिन दर्ज करें (Default PIN: 1234):",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = adminPinInput,
                        onValueChange = { adminPinInput = it },
                        label = { Text("Admin PIN") },
                        placeholder = { Text("1234") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pin = if (adminPinInput.isNotBlank()) adminPinInput else "1234"
                        onVerifyAdminPin(pin)
                        adminPinInput = ""
                    },
                    modifier = Modifier.testTag("admin_login_confirm_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Text("लॉगिन करें")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissAdminPin) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
fun UserProfileCard(
    user: User,
    isChatUnlocked: Boolean,
    isPaymentPending: Boolean,
    onChatClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_card_${user.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Profile Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Avatar with Status
                Box {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(user.avatarBgColor), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        )
                    }
                    if (user.isOnline) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(Color(0xFF10B981), CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${user.name}, ${user.age}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (user.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = user.city,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Female Privacy Shield Indicator
                if (user.isFemale && user.privacyShieldEnabled) {
                    PrivacyShieldBadge(isFemale = true)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bio
            Text(
                text = user.bio,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Interests Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                user.interests.split(",").take(3).forEach { interest ->
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = interest.trim(),
                            fontSize = 10.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }

            // Female Profile Security Assurance
            if (user.isFemale) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ShieldGreenContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🔒 फ़ोन नंबर व Instagram छुपा हुआ है (गोपनीयता सुरक्षित)",
                        fontSize = 10.sp,
                        color = OnShieldGreenContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chat Action Button
            Button(
                onClick = onChatClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("chat_button_${user.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isChatUnlocked) ShieldGreen else RosePrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = when {
                        isChatUnlocked -> Icons.Default.ChatBubble
                        isPaymentPending -> Icons.Default.HourglassTop
                        else -> Icons.Default.Lock
                    },
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        isChatUnlocked -> "सुरक्षित चैट करें (Chat Now)"
                        isPaymentPending -> "सत्यापन जारी (24h Review Status)"
                        else -> "चैट अनलॉक करें (₹49 UPI पास)"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
