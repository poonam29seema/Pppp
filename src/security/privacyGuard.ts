export interface SecurityCheckResult {
  isSafe: boolean;
  violationType?: string;
  sanitizedText: string;
  reasonHindi?: string;
}

// 10-digit Indian phone numbers with optional +91, 0, spaces, dashes
const PHONE_PATTERN = /(?:(?:\+|0{0,2})91[\s\-\.]*)?[6-9](?:[\s\-\.]*\d){9}/i;

// Spaced / dotted digits (e.g. 9 8 7 6 5 4 3 2 1 0)
const OBSCURED_DIGITS_PATTERN = /\b\d(?:[\s\-\._]*\d){7,11}\b/;

// Instagram / Snapchat / Social handles
const SOCIAL_PATTERN = /(?:(?:insta|ig|instagram|snap|snapchat|telegram|tg|fb|facebook)\s*[:=\-\s@]?\s*[a-zA-Z0-9_\.]{3,30})|@[a-zA-Z0-9_\.]{3,30}/i;

// Email addresses
const EMAIL_PATTERN = /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/i;

// External contact invitations (WhatsApp / Call)
const OFFLINE_CONTACT_PATTERN = /(?:wa\.me|whatsapp|call me|number lo|mera no|contact me at|call karna|ph no|mobile no)/i;

// Links
const URL_PATTERN = /https?:\/\/\S+|www\.\S+/i;

export function analyzeMessage(
  rawText: string,
  isTargetFemale: boolean,
  strictMode: boolean = true
): SecurityCheckResult {
  if (!isTargetFemale && !strictMode) {
    return { isSafe: true, sanitizedText: rawText };
  }

  // 1. Phone number check
  if (PHONE_PATTERN.test(rawText) || OBSCURED_DIGITS_PATTERN.test(rawText)) {
    return {
      isSafe: false,
      violationType: 'PHONE_NUMBER',
      sanitizedText: rawText.replace(PHONE_PATTERN, '[फोन नंबर सुरक्षित / Phone Hidden]'),
      reasonHindi: 'महिला सुरक्षा नीति: लड़कियों की ID पर फ़ोन नंबर या मोबाइल नंबर शेयर करना सख़्त मना है।'
    };
  }

  // 2. Instagram / Social check
  if (SOCIAL_PATTERN.test(rawText)) {
    return {
      isSafe: false,
      violationType: 'SOCIAL_HANDLE',
      sanitizedText: rawText.replace(SOCIAL_PATTERN, '[सोशल ID सुरक्षित / ID Hidden]'),
      reasonHindi: 'सुरक्षा अलर्ट: Instagram, Snapchat या सोशल मीडिया ID शेयर करने की अनुमति नहीं है।'
    };
  }

  // 3. Email check
  if (EMAIL_PATTERN.test(rawText)) {
    return {
      isSafe: false,
      violationType: 'EMAIL_ADDRESS',
      sanitizedText: rawText.replace(EMAIL_PATTERN, '[ईमेल सुरक्षित / Email Hidden]'),
      reasonHindi: 'सुरक्षा अलर्ट: ईमेल पता (Email ID) शेयर करना प्रतिबंधित है।'
    };
  }

  // 4. WhatsApp / Offline Contact check
  if (OFFLINE_CONTACT_PATTERN.test(rawText)) {
    return {
      isSafe: false,
      violationType: 'CONTACT_INVITATION',
      sanitizedText: rawText.replace(OFFLINE_CONTACT_PATTERN, '[सुरक्षित संदेश / Filtered]'),
      reasonHindi: 'सुरक्षा अलर्ट: WhatsApp या डायरेक्ट कॉल की मांग SafeMeet सुरक्षा नियमों के ख़िलाफ़ है।'
    };
  }

  // 5. External URLs
  if (URL_PATTERN.test(rawText)) {
    return {
      isSafe: false,
      violationType: 'EXTERNAL_LINK',
      sanitizedText: '[लिंक अवरुद्ध / Link Blocked]',
      reasonHindi: 'सुरक्षा अलर्ट: बाहरी वेबसाइट लिंक शेयर करना सुरक्षित नहीं है।'
    };
  }

  return { isSafe: true, sanitizedText: rawText };
}
