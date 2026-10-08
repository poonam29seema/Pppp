package com.example.security

import java.util.regex.Pattern

data class SecurityCheckResult(
    val isSafe: Boolean,
    val violationType: String? = null,
    val sanitizedText: String = "",
    val reasonHindi: String? = null
)

object PrivacyGuard {

    // Regex for 10-digit mobile numbers with optional country code (+91, 91, 0), spaces, dashes, dots
    private val PHONE_PATTERN = Pattern.compile(
        "(?:(?:\\+|0{0,2})91[\\s\\-\\.]*)?[6-9](?:[\\s\\-\\.]*\\d){9}",
        Pattern.CASE_INSENSITIVE
    )

    // Regex for spaced / obscured digits sequence of 7+ numbers (e.g., "9 8 7 6 5 4 3 2 1 0")
    private val OBSCURED_DIGITS_PATTERN = Pattern.compile(
        "\\b\\d(?:[\\s\\-\\._]*\\d){7,11}\\b"
    )

    // Regex for Email addresses
    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
        Pattern.CASE_INSENSITIVE
    )

    // Regex for Instagram handles or keywords
    private val INSTAGRAM_PATTERN = Pattern.compile(
        "(?:(?:insta|ig|instagram|snap|snapchat|telegram|tg|fb|facebook)\\s*[:=\\-\\s@]?\\s*[a-zA-Z0-9_\\.]{3,30})|@[a-zA-Z0-9_\\.]{3,30}",
        Pattern.CASE_INSENSITIVE
    )

    // Regex for WhatsApp / Calling prompts to share offline contact
    private val OFFLINE_CONTACT_PHRASES = Pattern.compile(
        "(?:wa\\.me|whatsapp|call me|number lo|mera no|contact me at|call karna|ph no|mobile no)",
        Pattern.CASE_INSENSITIVE
    )

    // Regex for web links
    private val URL_PATTERN = Pattern.compile(
        "https?://\\S+|www\\.\\S+",
        Pattern.CASE_INSENSITIVE
    )

    /**
     * Checks if the message contains sensitive personal information.
     * When isTargetFemale is true or strictMode is on, personal contact info is strictly blocked.
     */
    fun analyzeMessage(
        rawText: String,
        isTargetFemale: Boolean,
        strictMode: Boolean = true
    ): SecurityCheckResult {
        if (!isTargetFemale && !strictMode) {
            return SecurityCheckResult(isSafe = true, sanitizedText = rawText)
        }

        // 1. Check Phone Numbers
        if (PHONE_PATTERN.matcher(rawText).find() || OBSCURED_DIGITS_PATTERN.matcher(rawText).find()) {
            return SecurityCheckResult(
                isSafe = false,
                violationType = "PHONE_NUMBER",
                sanitizedText = maskPhone(rawText),
                reasonHindi = "महिला सुरक्षा नीति: फ़ोन नंबर या मोबाइल नंबर शेयर करना सख़्त मना है।"
            )
        }

        // 2. Check Instagram / Snapchat / Social Handles
        if (INSTAGRAM_PATTERN.matcher(rawText).find()) {
            return SecurityCheckResult(
                isSafe = false,
                violationType = "SOCIAL_HANDLE",
                sanitizedText = maskSocial(rawText),
                reasonHindi = "सुरक्षा अलर्ट: Instagram, Snapchat या सोशल मीडिया ID शेयर करने की अनुमति नहीं है।"
            )
        }

        // 3. Check Email Address
        if (EMAIL_PATTERN.matcher(rawText).find()) {
            return SecurityCheckResult(
                isSafe = false,
                violationType = "EMAIL_ADDRESS",
                sanitizedText = maskEmail(rawText),
                reasonHindi = "सुरक्षा अलर्ट: ईमेल पता (Email ID) शेयर करना प्रतिबंधित है।"
            )
        }

        // 4. Check External Contact Prompts (WhatsApp / Call)
        if (OFFLINE_CONTACT_PHRASES.matcher(rawText).find()) {
            return SecurityCheckResult(
                isSafe = false,
                violationType = "CONTACT_INVITATION",
                sanitizedText = maskGeneric(rawText),
                reasonHindi = "सुरक्षा अलर्ट: WhatsApp या डायरेक्ट कॉल की मांग SafeMeet सुरक्षा नियमों के ख़िलाफ़ है।"
            )
        }

        // 5. Check External URLs
        if (URL_PATTERN.matcher(rawText).find()) {
            return SecurityCheckResult(
                isSafe = false,
                violationType = "EXTERNAL_LINK",
                sanitizedText = "[लिंक अवरुद्ध / Link Blocked]",
                reasonHindi = "सुरक्षा अलर्ट: बाहरी वेबसाइट लिंक शेयर करना सुरक्षित नहीं है।"
            )
        }

        return SecurityCheckResult(isSafe = true, sanitizedText = rawText)
    }

    private fun maskPhone(text: String): String {
        return PHONE_PATTERN.matcher(text).replaceAll("[फोन नंबर सुरक्षित / Phone Hidden]")
    }

    private fun maskSocial(text: String): String {
        return INSTAGRAM_PATTERN.matcher(text).replaceAll("[सोशल ID सुरक्षित / ID Hidden]")
    }

    private fun maskEmail(text: String): String {
        return EMAIL_PATTERN.matcher(text).replaceAll("[ईमेल सुरक्षित / Email Hidden]")
    }

    private fun maskGeneric(text: String): String {
        return OFFLINE_CONTACT_PHRASES.matcher(text).replaceAll("[सुरक्षित संदेश / Filtered]")
    }
}
