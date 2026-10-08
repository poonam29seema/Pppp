package com.example

import com.example.security.PrivacyGuard
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivacyGuardTest {

    @Test
    fun testSafeMessagesAllowed() {
        val safeMsg = "Namaste! Aapka din kaisa raha? Mujhe books padhna pasand hai."
        val result = PrivacyGuard.analyzeMessage(safeMsg, isTargetFemale = true, strictMode = true)
        assertTrue(result.isSafe)
    }

    @Test
    fun testPhoneNumberBlocked() {
        val phoneMsg = "Mera number hai 9876543210 call karo"
        val result = PrivacyGuard.analyzeMessage(phoneMsg, isTargetFemale = true, strictMode = true)
        assertFalse(result.isSafe)
    }

    @Test
    fun testSpacedPhoneNumberBlocked() {
        val spacedPhoneMsg = "call on 9 8 7 6 5 4 3 2 1 0 please"
        val result = PrivacyGuard.analyzeMessage(spacedPhoneMsg, isTargetFemale = true, strictMode = true)
        assertFalse(result.isSafe)
    }

    @Test
    fun testInstagramHandleBlocked() {
        val instaMsg = "Mera insta @rohan_rocks par message bhejo"
        val result = PrivacyGuard.analyzeMessage(instaMsg, isTargetFemale = true, strictMode = true)
        assertFalse(result.isSafe)
    }

    @Test
    fun testEmailBlocked() {
        val emailMsg = "Mujhe email karo testuser@gmail.com par"
        val result = PrivacyGuard.analyzeMessage(emailMsg, isTargetFemale = true, strictMode = true)
        assertFalse(result.isSafe)
    }

    @Test
    fun testWhatsAppPromptBlocked() {
        val waMsg = "Aap whatsapp me aa jao"
        val result = PrivacyGuard.analyzeMessage(waMsg, isTargetFemale = true, strictMode = true)
        assertFalse(result.isSafe)
    }
}
