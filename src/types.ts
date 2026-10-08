export interface User {
  id: string;
  name: string;
  gender: 'FEMALE' | 'MALE';
  age: number;
  city: string;
  bio: string;
  interests: string[];
  avatarColor: string;
  isVerified: boolean;
  privacyShieldEnabled: boolean;
  isOnline: boolean;
  isBanned: boolean;
}

export interface ChatMessage {
  id: string;
  conversationUserId: string;
  text: string;
  timestamp: number;
  isFromMe: boolean;
  isBlockedOrMasked?: boolean;
  securityViolationReason?: string;
}

export interface PaymentRequest {
  id: string;
  userId: string;
  userName: string;
  amount: number;
  utrNumber: string;
  screenshotName: string;
  submittedAt: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  rejectionReason?: string;
  reviewedAt?: number;
}

export interface AppConfig {
  upiId: string;
  payeeName: string;
  unlockAmount: number;
  adminPin: string;
  isChatGloballyUnlockedForUser: boolean;
  securityStrictMode: boolean;
}

export interface SecurityLog {
  id: string;
  targetUserName: string;
  attemptedText: string;
  violationType: string;
  timestamp: number;
}
