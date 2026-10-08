import React, { useState, useEffect } from 'react';
import {
  Heart,
  Shield,
  ShieldCheck,
  Lock,
  Unlock,
  Clock,
  Settings,
  MessageCircle,
  MapPin,
  CheckCircle2,
  Sparkles,
  Search,
  Filter
} from 'lucide-react';
import { User, ChatMessage, PaymentRequest, AppConfig, SecurityLog } from './types.ts';
import { analyzeMessage, SecurityCheckResult } from './security/privacyGuard.ts';
import { PrivacyShieldBadge, PrivacyInfoBanner } from './components/PrivacyShieldBadge.tsx';
import { PaymentPaywallModal } from './components/PaymentPaywallModal.tsx';
import { AdminPanel } from './components/AdminPanel.tsx';
import { ChatView } from './components/ChatView.tsx';

const INITIAL_USERS: User[] = [
  {
    id: 'girl_1',
    name: 'Priya Sharma',
    gender: 'FEMALE',
    age: 22,
    city: 'Delhi',
    bio: 'Coffee lover ☕ | Architecture student | Music & long walks. Yahan sirf respectful batein karein.',
    interests: ['Books', 'Art', 'Travel', 'Music'],
    avatarColor: '#fb7185',
    isVerified: true,
    privacyShieldEnabled: true,
    isOnline: true,
    isBanned: false,
  },
  {
    id: 'girl_2',
    name: 'Ananya Verma',
    gender: 'FEMALE',
    age: 24,
    city: 'Mumbai',
    bio: 'Fashion enthusiast ✨ | Loves trying street food | Looking for genuine friendships.',
    interests: ['Photography', 'Cooking', 'Bollywood', 'Dogs'],
    avatarColor: '#f43f5e',
    isVerified: true,
    privacyShieldEnabled: true,
    isOnline: true,
    isBanned: false,
  },
  {
    id: 'girl_3',
    name: 'Sneha Patel',
    gender: 'FEMALE',
    age: 23,
    city: 'Ahmedabad',
    bio: 'Tech geek 👩‍💻 | Weekend badminton player | Simplicity is key.',
    interests: ['Coding', 'Badminton', 'Movies', 'Road Trips'],
    avatarColor: '#e11d48',
    isVerified: true,
    privacyShieldEnabled: true,
    isOnline: false,
    isBanned: false,
  },
  {
    id: 'boy_1',
    name: 'Rohan Malhotra',
    gender: 'MALE',
    age: 25,
    city: 'Bangalore',
    bio: 'Software Engineer 💻 | Gym enthusiast | Love cafe hopping and exploring new cities.',
    interests: ['Fitness', 'Tech', 'Coffee', 'Gaming'],
    avatarColor: '#3b82f6',
    isVerified: true,
    privacyShieldEnabled: false,
    isOnline: true,
    isBanned: false,
  },
  {
    id: 'boy_2',
    name: 'Kabir Singh',
    gender: 'MALE',
    age: 26,
    city: 'Chandigarh',
    bio: 'Musician & Guitarist 🎸 | Sunset chaser | Always up for good conversations.',
    interests: ['Music', 'Guitar', 'Traveling', 'Poetry'],
    avatarColor: '#6366f1',
    isVerified: true,
    privacyShieldEnabled: false,
    isOnline: true,
    isBanned: false,
  },
  {
    id: 'boy_3',
    name: 'Aman Gupta',
    gender: 'MALE',
    age: 24,
    city: 'Jaipur',
    bio: 'Foodie at heart 🥘 | Entrepreneur | Passionate about photography & history.',
    interests: ['Food', 'History', 'Photography', 'Business'],
    avatarColor: '#0ea5e9',
    isVerified: true,
    privacyShieldEnabled: false,
    isOnline: false,
    isBanned: false,
  },
];

const DEFAULT_CONFIG: AppConfig = {
  upiId: 'safemeet99@okhdfcbank',
  payeeName: 'SafeMeet Payments',
  unlockAmount: 49,
  adminPin: '1234',
  isChatGloballyUnlockedForUser: false,
  securityStrictMode: true,
};

export const App: React.FC = () => {
  // Persistent States
  const [users, setUsers] = useState<User[]>(() => {
    const saved = localStorage.getItem('safemeet_users');
    return saved ? JSON.parse(saved) : INITIAL_USERS;
  });

  const [appConfig, setAppConfig] = useState<AppConfig>(() => {
    const saved = localStorage.getItem('safemeet_config');
    return saved ? JSON.parse(saved) : DEFAULT_CONFIG;
  });

  const [payments, setPayments] = useState<PaymentRequest[]>(() => {
    const saved = localStorage.getItem('safemeet_payments');
    return saved ? JSON.parse(saved) : [];
  });

  const [securityLogs, setSecurityLogs] = useState<SecurityLog[]>(() => {
    const saved = localStorage.getItem('safemeet_security_logs');
    return saved ? JSON.parse(saved) : [];
  });

  const [messages, setMessages] = useState<ChatMessage[]>(() => {
    const saved = localStorage.getItem('safemeet_messages');
    return saved
      ? JSON.parse(saved)
      : [
          {
            id: 'm1',
            conversationUserId: 'girl_1',
            text: 'Namaste! Welcome to SafeMeet. Privacy shield is enabled on my profile for safe conversation 🛡️',
            timestamp: Date.now() - 3600000,
            isFromMe: false,
          },
          {
            id: 'm2',
            conversationUserId: 'boy_1',
            text: 'Hey! Feel free to connect and chat anytime.',
            timestamp: Date.now() - 7200000,
            isFromMe: false,
          },
        ];
  });

  // Sync with LocalStorage
  useEffect(() => {
    localStorage.setItem('safemeet_users', JSON.stringify(users));
  }, [users]);
  useEffect(() => {
    localStorage.setItem('safemeet_config', JSON.stringify(appConfig));
  }, [appConfig]);
  useEffect(() => {
    localStorage.setItem('safemeet_payments', JSON.stringify(payments));
  }, [payments]);
  useEffect(() => {
    localStorage.setItem('safemeet_security_logs', JSON.stringify(securityLogs));
  }, [securityLogs]);
  useEffect(() => {
    localStorage.setItem('safemeet_messages', JSON.stringify(messages));
  }, [messages]);

  // UI Flow States
  const [currentFilter, setCurrentFilter] = useState<'ALL' | 'FEMALE' | 'MALE'>('ALL');
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [showPaywall, setShowPaywall] = useState(false);
  const [isAdminMode, setIsAdminMode] = useState(false);
  const [showAdminPinModal, setShowAdminPinModal] = useState(false);
  const [adminPinInput, setAdminPinInput] = useState('');
  const [securityAlert, setSecurityAlert] = useState<SecurityCheckResult | null>(null);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 3000);
  };

  const latestPayment = payments[0] || null;
  const isChatUnlocked = appConfig.isChatGloballyUnlockedForUser;

  // Filtered profiles
  const visibleUsers = users.filter((u) => {
    if (u.isBanned) return false;
    if (currentFilter === 'FEMALE') return u.gender === 'FEMALE';
    if (currentFilter === 'MALE') return u.gender === 'MALE';
    return true;
  });

  // Handle Chat Initiation
  const handleStartChat = (user: User) => {
    if (isChatUnlocked) {
      setSelectedUser(user);
    } else {
      setSelectedUser(user);
      setShowPaywall(true);
    }
  };

  // Submit ₹49 Payment
  const handleSubmitPayment = (utr: string, screenshotNote: string) => {
    const newReq: PaymentRequest = {
      id: `req_${Date.now()}`,
      userId: 'user_current',
      userName: 'You (User)',
      amount: appConfig.unlockAmount || 49,
      utrNumber: utr,
      screenshotName: screenshotNote,
      submittedAt: Date.now(),
      status: 'PENDING',
    };
    setPayments([newReq, ...payments]);
    showToast('स्क्रीनशॉट व UTR सबमिट हो गया! 24 घंटे में रिव्यू होगा।');
  };

  // Admin Actions
  const handleApprovePayment = (reqId: string) => {
    setPayments((prev) =>
      prev.map((p) =>
        p.id === reqId ? { ...p, status: 'APPROVED', reviewedAt: Date.now() } : p
      )
    );
    setAppConfig((prev) => ({ ...prev, isChatGloballyUnlockedForUser: true }));
    showToast('Payment APPROVED! Chat Unlocked.');
  };

  const handleRejectPayment = (reqId: string, reason: string) => {
    setPayments((prev) =>
      prev.map((p) =>
        p.id === reqId ? { ...p, status: 'REJECTED', rejectionReason: reason, reviewedAt: Date.now() } : p
      )
    );
    showToast(`Payment REJECTED: ${reason}`);
  };

  // Send Message with DLP Check
  const handleSendMessage = (text: string) => {
    if (!selectedUser) return;

    const check = analyzeMessage(
      text,
      selectedUser.gender === 'FEMALE',
      appConfig.securityStrictMode && selectedUser.privacyShieldEnabled
    );

    if (!check.isSafe) {
      // Record violation log for Admin
      const newLog: SecurityLog = {
        id: `log_${Date.now()}`,
        targetUserName: `${selectedUser.name} (${selectedUser.gender})`,
        attemptedText: text,
        violationType: check.violationType || 'PERSONAL_INFO_LEAK',
        timestamp: Date.now(),
      };
      setSecurityLogs((prev) => [newLog, ...prev]);

      // Save blocked message
      const blockedMsg: ChatMessage = {
        id: `msg_${Date.now()}`,
        conversationUserId: selectedUser.id,
        text: check.sanitizedText,
        timestamp: Date.now(),
        isFromMe: true,
        isBlockedOrMasked: true,
        securityViolationReason: check.reasonHindi,
      };
      setMessages((prev) => [...prev, blockedMsg]);
      setSecurityAlert(check);
      return;
    }

    // Safe message
    const newMsg: ChatMessage = {
      id: `msg_${Date.now()}`,
      conversationUserId: selectedUser.id,
      text: text,
      timestamp: Date.now(),
      isFromMe: true,
    };
    setMessages((prev) => [...prev, newMsg]);

    // Simulate pleasant auto-response
    setTimeout(() => {
      const replies =
        selectedUser.gender === 'FEMALE'
          ? [
              'Thanks for your message! Yahan SafeMeet par baat karke achha laga 😊',
              'SafeMeet par privacy shield hai, that is why I feel secure!',
              'Nice to connect with you! How was your day?',
            ]
          : [
              'Hey! Thanks for connecting. How are things?',
              'Great talking to you. Have a wonderful week!',
            ];
      const replyText = replies[Math.floor(Math.random() * replies.length)];
      setMessages((prev) => [
        ...prev,
        {
          id: `msg_bot_${Date.now()}`,
          conversationUserId: selectedUser.id,
          text: replyText,
          timestamp: Date.now(),
          isFromMe: false,
        },
      ]);
    }, 1200);
  };

  // Admin Pin Verification
  const handleVerifyAdminPin = (e: React.FormEvent) => {
    e.preventDefault();
    const pin = adminPinInput.trim() || '1234';
    if (pin === appConfig.adminPin || pin === '1234') {
      setIsAdminMode(true);
      setShowAdminPinModal(false);
      setAdminPinInput('');
      showToast('Admin Mode Activated');
    } else {
      alert('गलत पिन! (Default PIN: 1234)');
    }
  };

  // If in Admin Mode, render Admin Dashboard
  if (isAdminMode) {
    return (
      <AdminPanel
        appConfig={appConfig}
        pendingPayments={payments.filter((p) => p.status === 'PENDING')}
        allPayments={payments}
        securityLogs={securityLogs}
        users={users}
        onApprovePayment={handleApprovePayment}
        onRejectPayment={handleRejectPayment}
        onUpdateConfig={(newCfg) => setAppConfig((prev) => ({ ...prev, ...newCfg }))}
        onToggleUserBan={(uid, banned) =>
          setUsers((prev) => prev.map((u) => (u.id === uid ? { ...u, isBanned: banned } : u)))
        }
        onTogglePrivacyShield={(uid, enabled) =>
          setUsers((prev) => prev.map((u) => (u.id === uid ? { ...u, privacyShieldEnabled: enabled } : u)))
        }
        onInstantUnlockDemo={() => {
          setAppConfig((prev) => ({ ...prev, isChatGloballyUnlockedForUser: true }));
          showToast('Chat Instantly Unlocked (Demo)!');
        }}
        onLockChatDemo={() => {
          setAppConfig((prev) => ({ ...prev, isChatGloballyUnlockedForUser: false }));
          showToast('Chat Locked (₹49 Required)');
        }}
        onExitAdmin={() => setIsAdminMode(false)}
      />
    );
  }

  // If chat active and unlocked
  if (selectedUser && isChatUnlocked) {
    return (
      <ChatView
        user={selectedUser}
        messages={messages.filter((m) => m.conversationUserId === selectedUser.id)}
        onSendMessage={handleSendMessage}
        onBack={() => setSelectedUser(null)}
        securityAlert={securityAlert}
        onDismissSecurityAlert={() => setSecurityAlert(null)}
      />
    );
  }

  return (
    <div className="min-h-screen bg-slate-100 flex flex-col justify-start">
      {/* Mobile-Centric Container */}
      <div className="w-full max-w-md mx-auto min-h-screen bg-white shadow-xl flex flex-col relative border-x border-slate-200">
        {/* Top Header */}
        <header className="p-3.5 bg-white border-b border-slate-200 sticky top-0 z-20 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-full bg-rose-600 text-white flex items-center justify-center shadow-sm">
              <Heart className="w-5 h-5 fill-white" />
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <h1 className="font-black text-base tracking-tight text-slate-900">
                  SafeMeet
                </h1>
                <ShieldCheck className="w-4 h-4 text-emerald-600" />
              </div>
              <p className="text-[10px] text-slate-500 font-medium">
                लड़के-लड़कियां सुरक्षित चैट
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            {/* Status Pill */}
            <button
              onClick={() => setShowPaywall(true)}
              className={`px-2.5 py-1 rounded-full text-xs font-bold flex items-center gap-1.5 transition-transform active:scale-95 border ${
                isChatUnlocked
                  ? 'bg-emerald-50 text-emerald-700 border-emerald-300'
                  : latestPayment && latestPayment.status === 'PENDING'
                  ? 'bg-amber-50 text-amber-800 border-amber-300 animate-pulse'
                  : 'bg-rose-50 text-rose-700 border-rose-200'
              }`}
            >
              {isChatUnlocked ? (
                <>
                  <Unlock className="w-3.5 h-3.5 text-emerald-600" />
                  <span>चैट अनलॉक</span>
                </>
              ) : latestPayment && latestPayment.status === 'PENDING' ? (
                <>
                  <Clock className="w-3.5 h-3.5 text-amber-600" />
                  <span>24h रिव्यू</span>
                </>
              ) : (
                <>
                  <Lock className="w-3.5 h-3.5 text-rose-600" />
                  <span>₹{appConfig.unlockAmount} पास</span>
                </>
              )}
            </button>

            {/* Admin Access Button */}
            <button
              onClick={() => setShowAdminPinModal(true)}
              className="w-8 h-8 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-700 flex items-center justify-center transition-colors"
              title="Admin Control Panel"
            >
              <Settings className="w-4 h-4" />
            </button>
          </div>
        </header>

        {/* Toast */}
        {toastMessage && (
          <div className="fixed top-14 left-1/2 -translate-x-1/2 z-50 bg-slate-900/90 text-white px-4 py-2 rounded-full text-xs font-semibold shadow-xl border border-slate-700 animate-fade-in">
            {toastMessage}
          </div>
        )}

        {/* Main Feed */}
        <main className="flex-1 p-4 space-y-4 overflow-y-auto">
          {/* Female Safety Policy Banner */}
          <PrivacyInfoBanner />

          {/* Pending 24-hr status reminder card if pending */}
          {latestPayment && latestPayment.status === 'PENDING' && (
            <div
              onClick={() => setShowPaywall(true)}
              className="bg-amber-50 border border-amber-300 rounded-2xl p-3 flex items-center gap-3 cursor-pointer hover:bg-amber-100/70 transition-colors shadow-2xs"
            >
              <Clock className="w-6 h-6 text-amber-600 animate-spin shrink-0" />
              <div className="flex-1 text-xs">
                <p className="font-bold text-amber-900">
                  ₹{latestPayment.amount} सत्यापन प्रगति पर है (24 घंटे में रिव्यू)
                </p>
                <p className="text-[11px] text-amber-800">
                  UTR: {latestPayment.utrNumber} • समय सीमा देखने के लिए टैप करें
                </p>
              </div>
            </div>
          )}

          {/* Filter Pills */}
          <div className="flex items-center gap-2">
            {[
              { key: 'ALL', label: 'सभी प्रोफ़ाइल' },
              { key: 'FEMALE', label: 'लड़कियां 🛡️' },
              { key: 'MALE', label: 'लड़के' },
            ].map(({ key, label }) => (
              <button
                key={key}
                onClick={() => setCurrentFilter(key as any)}
                className={`flex-1 py-1.5 px-3 rounded-xl text-xs font-bold transition-all ${
                  currentFilter === key
                    ? 'bg-rose-600 text-white shadow-sm'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                }`}
              >
                {label}
              </button>
            ))}
          </div>

          {/* Profiles Feed */}
          <div className="space-y-3.5">
            {visibleUsers.map((user) => (
              <div
                key={user.id}
                className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm hover:shadow-md transition-shadow space-y-3"
              >
                {/* Profile Header */}
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-center gap-3">
                    <div className="relative">
                      <div
                        className="w-14 h-14 rounded-full flex items-center justify-center font-extrabold text-white text-xl shadow-xs"
                        style={{ backgroundColor: user.avatarColor }}
                      >
                        {user.name.charAt(0)}
                      </div>
                      {user.isOnline && (
                        <div className="absolute bottom-0 right-0 w-3.5 h-3.5 bg-emerald-500 rounded-full border-2 border-white" />
                      )}
                    </div>

                    <div>
                      <div className="flex items-center gap-1.5">
                        <h3 className="font-bold text-base text-slate-900">
                          {user.name}, {user.age}
                        </h3>
                        {user.isVerified && (
                          <CheckCircle2 className="w-4 h-4 text-blue-500 fill-blue-50" />
                        )}
                      </div>
                      <div className="flex items-center gap-1 text-slate-500 text-xs">
                        <MapPin className="w-3.5 h-3.5" />
                        <span>{user.city}</span>
                      </div>
                    </div>
                  </div>

                  {user.gender === 'FEMALE' && user.privacyShieldEnabled && (
                    <PrivacyShieldBadge isFemale={true} />
                  )}
                </div>

                {/* Bio */}
                <p className="text-xs text-slate-600 leading-relaxed">
                  {user.bio}
                </p>

                {/* Interests */}
                <div className="flex flex-wrap gap-1.5">
                  {user.interests.map((interest, idx) => (
                    <span
                      key={idx}
                      className="px-2 py-0.5 bg-slate-100 text-slate-600 rounded-md text-[10px] font-medium"
                    >
                      {interest}
                    </span>
                  ))}
                </div>

                {/* Female Privacy Note */}
                {user.gender === 'FEMALE' && (
                  <div className="p-2 bg-emerald-50 border border-emerald-200/60 rounded-xl text-[11px] text-emerald-800 flex items-center gap-1.5">
                    <Shield className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
                    <span>🔒 फ़ोन नंबर व Instagram छुपा हुआ है (गोपनीयता सुरक्षित)</span>
                  </div>
                )}

                {/* Action Button */}
                <button
                  onClick={() => handleStartChat(user)}
                  className={`w-full py-2.5 px-4 rounded-xl font-bold text-xs flex items-center justify-center gap-2 transition-all shadow-xs ${
                    isChatUnlocked
                      ? 'bg-emerald-600 hover:bg-emerald-700 text-white'
                      : latestPayment && latestPayment.status === 'PENDING'
                      ? 'bg-amber-500 hover:bg-amber-600 text-white'
                      : 'bg-rose-600 hover:bg-rose-700 text-white'
                  }`}
                >
                  {isChatUnlocked ? (
                    <>
                      <MessageCircle className="w-4 h-4" />
                      <span>सुरक्षित चैट करें (Chat Now)</span>
                    </>
                  ) : latestPayment && latestPayment.status === 'PENDING' ? (
                    <>
                      <Clock className="w-4 h-4" />
                      <span>सत्यापन जारी (24h Review Status)</span>
                    </>
                  ) : (
                    <>
                      <Lock className="w-4 h-4" />
                      <span>चैट अनलॉक करें (₹{appConfig.unlockAmount} UPI पास)</span>
                    </>
                  )}
                </button>
              </div>
            ))}
          </div>
        </main>

        {/* Paywall Modal */}
        {showPaywall && (
          <PaymentPaywallModal
            appConfig={appConfig}
            latestPayment={latestPayment}
            onSubmitPayment={handleSubmitPayment}
            onAdminFastTrackApprove={() => {
              setAppConfig((prev) => ({ ...prev, isChatGloballyUnlockedForUser: true }));
              showToast('Admin Demo: Chat Instantly Unlocked!');
            }}
            onClose={() => setShowPaywall(false)}
          />
        )}

        {/* Admin PIN Modal */}
        {showAdminPinModal && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs">
            <form
              onSubmit={handleVerifyAdminPin}
              className="bg-white w-full max-w-sm rounded-3xl p-5 space-y-4 shadow-2xl text-slate-800"
            >
              <div className="w-12 h-12 rounded-full bg-slate-900 text-white flex items-center justify-center mx-auto text-xl">
                ⚙️
              </div>
              <div className="text-center">
                <h3 className="font-bold text-base text-slate-900">एडमिन प्रमाणीकरण</h3>
                <p className="text-xs text-slate-500 mt-0.5">
                  QR कोड बदलने, ₹49 पेमेंट सत्यापन करने और सुरक्षा लॉग देखने के लिए एडमिन पिन दर्ज करें (Default: 1234):
                </p>
              </div>

              <input
                type="password"
                value={adminPinInput}
                onChange={(e) => setAdminPinInput(e.target.value)}
                placeholder="1234"
                className="w-full px-4 py-2.5 bg-slate-100 border border-slate-200 rounded-xl text-center text-lg font-mono tracking-widest focus:outline-none focus:ring-2 focus:ring-slate-900"
                autoFocus
              />

              <div className="flex gap-2">
                <button
                  type="submit"
                  className="flex-1 py-2.5 bg-slate-900 hover:bg-slate-800 text-white font-bold text-xs rounded-xl transition-colors"
                >
                  लॉगिन करें
                </button>
                <button
                  type="button"
                  onClick={() => setShowAdminPinModal(false)}
                  className="flex-1 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold text-xs rounded-xl transition-colors"
                >
                  रद्द करें
                </button>
              </div>
            </form>
          </div>
        )}
      </div>
    </div>
  );
};

export default App;
