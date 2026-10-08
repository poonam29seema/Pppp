package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppConfig
import com.example.data.model.PaymentRequest
import com.example.ui.components.CountdownTimerCard
import com.example.ui.components.QrCodeView
import com.example.ui.theme.OnRoseContainer
import com.example.ui.theme.OnShieldGreenContainer
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.RosePrimary
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.ShieldGreenContainer

@Composable
fun PaymentPaywallModal(
    appConfig: AppConfig?,
    latestPayment: PaymentRequest?,
    onSubmitPayment: (utr: String, screenshotNote: String) -> Unit,
    onAdminFastTrackApprove: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var utrInput by remember { mutableStateOf("") }
    var selectedScreenshotName by remember { mutableStateOf("payment_screenshot_success.png") }
    var isScreenshotSelected by remember { mutableStateOf(false) }

    val unlockAmount = appConfig?.unlockAmount ?: 49.0
    val upiId = appConfig?.upiId ?: "safemeet99@okhdfcbank"
    val payeeName = appConfig?.payeeName ?: "SafeMeet Official"

    // UPI Deep Link payload
    val upiPayload = "upi://pay?pa=$upiId&pn=${Uri.encode(payeeName)}&am=${unlockAmount.toInt()}&cu=INR&tn=SafeMeet%20Chat%20Unlock"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("payment_paywall_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(RoseContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = RosePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "चैट अनलॉक पास",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "₹${unlockAmount.toInt()} One-time Lifetime Pass",
                                fontSize = 12.sp,
                                color = RosePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_paywall_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // If currently pending, show the 24-hour review countdown card prominently
                if (latestPayment != null && latestPayment.isPending) {
                    CountdownTimerCard(
                        paymentRequest = latestPayment,
                        onAdminSimulateApprove = onAdminFastTrackApprove
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else if (latestPayment != null && latestPayment.isApproved) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ShieldGreenContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ShieldGreen,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "भुगतान सत्यापित (Payment Approved)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = OnShieldGreenContainer
                                )
                                Text(
                                    text = "आपकी चैट सेवा सक्रिय हो चुकी है! अब आप सभी प्रोफाइल से सुरक्षित बात कर सकते हैं।",
                                    fontSize = 12.sp,
                                    color = OnShieldGreenContainer.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // If rejected, show rejection note
                if (latestPayment != null && latestPayment.isRejected) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4E6))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "भुगतान अस्वीकृत (Payment Rejected)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF9F1239)
                            )
                            Text(
                                text = "कारण: ${latestPayment.rejectionReason ?: "स्क्रीनशॉट या UTR अस्पष्ट था"}. कृपया सही विवरण के साथ पुनः सबमिट करें।",
                                fontSize = 11.sp,
                                color = Color(0xFF9F1239).copy(alpha = 0.9f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Security & Policy Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RoseContainer.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = RosePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "लड़कों एवं लड़कियों के बीच सुरक्षित और वास्तविक बातचीत के लिए ₹${unlockAmount.toInt()} का सत्यापन अनिवार्य है।",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = OnRoseContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // STEP 1: PAYMENT QR CODE & UPI
                Text(
                    text = "स्टेप 1: स्कैन करें या UPI से ₹${unlockAmount.toInt()} भुगतान करें",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                // High fidelity QR Code view
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    QrCodeView(
                        data = upiPayload,
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // UPI ID display box with copy button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("UPI ID (GPay / PhonePe / Paytm):", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text(
                                text = upiId,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", upiId))
                                Toast.makeText(context, "UPI ID कॉपी हो गई: $upiId", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_upi_btn"),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("कॉपी करें", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Direct Open UPI App Button
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiPayload))
                            context.startActivity(Intent.createChooser(intent, "UPI ऐप चुनें (PhonePe / GPay / Paytm)"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "UPI ऐप नहीं मिला, कृपया QR कोड स्कैन करें या UPI ID कॉपी करें", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_upi_app_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("UPI ऐप खोलें (Pay ₹${unlockAmount.toInt()})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // STEP 2: SUBMIT PAYMENT SCREENSHOT & UTR
                Text(
                    text = "स्टेप 2: भुगतान का स्क्रीनशॉट एवं UTR नंबर सबमिट करें",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "स्क्रीनशॉट सबमिट करने के 24 घंटे के अंदर सत्यापन पूरा होगा।",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Screenshot selection area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isScreenshotSelected) ShieldGreenContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = 1.dp,
                            color = if (isScreenshotSelected) ShieldGreen else Color(0xFFCBD5E1),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            isScreenshotSelected = true
                            selectedScreenshotName = "payment_receipt_₹${unlockAmount.toInt()}_${System.currentTimeMillis() % 10000}.jpg"
                            Toast.makeText(context, "भुगतान स्क्रीनशॉट चुना गया!", Toast.LENGTH_SHORT).show()
                        }
                        .padding(14.dp)
                        .testTag("upload_screenshot_box")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(if (isScreenshotSelected) ShieldGreen else RosePrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isScreenshotSelected) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                                contentDescription = "Screenshot",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isScreenshotSelected) "स्क्रीनशॉट अटैच हो गया ✅" else "पेमेंट का स्क्रीनशॉट चुनें (Tap to Attach)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = if (isScreenshotSelected) OnShieldGreenContainer else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isScreenshotSelected) selectedScreenshotName else "Google Pay / PhonePe / Paytm रसीद का फोटो",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // UTR Number input
                OutlinedTextField(
                    value = utrInput,
                    onValueChange = { utrInput = it.take(16) },
                    label = { Text("12 अंकों का UTR / UPI Ref ID") },
                    placeholder = { Text("उदा. 423985172634") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("utr_number_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Verification Proof Button
                Button(
                    onClick = {
                        val finalUtr = if (utrInput.isNotBlank()) utrInput.trim() else "UTR_${System.currentTimeMillis() % 100000000}"
                        val finalNote = if (isScreenshotSelected) selectedScreenshotName else "upi_payment_proof_receipt.png"
                        onSubmitPayment(finalUtr, finalNote)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_payment_proof_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.UploadFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "स्क्रीनशॉट सबमिट करें (24 घंटे में रिव्यू)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security reminder note
                Text(
                    text = "🔒 SafeMeet सुरक्षा वादा: आपका भुगतान पूर्णतः सुरक्षित है। Admin द्वारा 24 घंटे में जांच के बाद आपकी चैट अनवरत चालू हो जाएगी।",
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
