package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OnShieldGreenContainer
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.ShieldGreenContainer

@Composable
fun PrivacyShieldBadge(
    modifier: Modifier = Modifier,
    isFemale: Boolean = true
) {
    Box(
        modifier = modifier
            .testTag("privacy_shield_badge")
            .background(
                color = ShieldGreenContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = ShieldGreen.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Privacy Shield",
                tint = OnShieldGreenContainer,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isFemale) "लड़कियों के लिए सुरक्षा शील्ड (100% Secure)" else "सुरक्षित प्रोफ़ाइल (Safe)",
                color = OnShieldGreenContainer,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun PrivacyInfoBanner(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ShieldGreenContainer.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .border(1.dp, ShieldGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(ShieldGreen, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "महिला सुरक्षा एवं गोपनीयता गारंटी",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = OnShieldGreenContainer
                )
                Text(
                    text = "लड़कियों की ID पर फ़ोन नंबर, Instagram, WhatsApp या व्यक्तिगत जानकारी शेयर करना सिस्टम द्वारा स्वतः ब्लॉक है। 100% सुरक्षित बातचीत।",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = OnShieldGreenContainer.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
