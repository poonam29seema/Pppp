import React, { useState } from 'react';
import {
  ShieldAlert,
  CreditCard,
  QrCode,
  Users,
  Check,
  X,
  Save,
  Lock,
  Unlock,
  ArrowLeft,
  AlertTriangle,
  ShieldCheck,
  ShieldOff,
  UserX,
  UserCheck
} from 'lucide-react';
import { AppConfig, PaymentRequest, SecurityLog, User } from '../types.ts';
import { QrCodeView } from './QrCodeView.tsx';

interface AdminPanelProps {
  appConfig: AppConfig;
  pendingPayments: PaymentRequest[];
  allPayments: PaymentRequest[];
  securityLogs: SecurityLog[];
  users: User[];
  onApprovePayment: (requestId: string) => void;
  onRejectPayment: (requestId: string, reason: string) => void;
  onUpdateConfig: (newConfig: Partial<AppConfig>) => void;
  onToggleUserBan: (userId: string, isBanned: boolean) => void;
  onTogglePrivacyShield: (userId: string, enabled: boolean) => void;
  onInstantUnlockDemo: () => void;
  onLockChatDemo: () => void;
  onExitAdmin: () => void;
}

export const AdminPanel: React.FC<AdminPanelProps> = ({
  appConfig,
  pendingPayments,
  allPayments,
  securityLogs,
  users,
  onApprovePayment,
  onRejectPayment,
  onUpdateConfig,
  onToggleUserBan,
  onTogglePrivacyShield,
  onInstantUnlockDemo,
  onLockChatDemo,
  onExitAdmin,
}) => {
  const [activeTab, setActiveTab] = useState<'payments' | 'qr' | 'logs' | 'users'>('payments');

  // Form states for QR & UPI Settings
  const [upiId, setUpiId] = useState(appConfig.upiId);
  const [payeeName, setPayeeName] = useState(appConfig.payeeName);
  const [amount, setAmount] = useState(appConfig.unlockAmount.toString());
  const [strictMode, setStrictMode] = useState(appConfig.securityStrictMode);
  const [savedSuccess, setSavedSuccess] = useState(false);

  // Rejection dialog
  const [rejectingReqId, setRejectingReqId] = useState<string | null>(null);
  const [rejectReason, setRejectReason] = useState('अमान्य UTR नंबर / स्क्रीनशॉट स्पष्ट नहीं है');

  const handleSaveQrConfig = (e: React.FormEvent) => {
    e.preventDefault();
    onUpdateConfig({
      upiId: upiId.trim(),
      payeeName: payeeName.trim(),
      unlockAmount: parseFloat(amount) || 49,
      securityStrictMode: strictMode,
    });
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 2500);
  };

  const previewUpiPayload = `upi://pay?pa=${upiId}&pn=${encodeURIComponent(payeeName)}&am=${amount}&cu=INR&tn=SafeMeet%20Chat%20Access`;

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 flex flex-col">
      {/* Top Bar */}
      <header className="bg-slate-950 border-b border-slate-800 p-4 sticky top-0 z-30 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-cyan-500/20 text-cyan-400 border border-cyan-500/40 flex items-center justify-center font-bold">
            🛡️
          </div>
          <div>
            <h1 className="font-extrabold text-base text-white tracking-wide">
              SafeMeet एडमिन पैनल
            </h1>
            <p className="text-[11px] text-slate-400">
              Master Control & Verification System
            </p>
          </div>
        </div>

        <button
          onClick={onExitAdmin}
          className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold rounded-lg flex items-center gap-1.5 transition-colors border border-slate-700"
        >
          <ArrowLeft className="w-3.5 h-3.5" />
          <span>यूज़र मोड में लौटें</span>
        </button>
      </header>

      {/* Demo Quick-Control Bar */}
      <div className="bg-slate-800/80 border-b border-slate-700 px-4 py-2 flex items-center justify-between text-xs flex-wrap gap-2">
        <div className="flex items-center gap-2">
          <span className="text-slate-400">यूज़र चैट स्थिति:</span>
          {appConfig.isChatGloballyUnlockedForUser ? (
            <span className="bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 font-bold px-2 py-0.5 rounded text-[10px]">
              UNLOCKED (सक्रिय)
            </span>
          ) : (
            <span className="bg-rose-500/20 text-rose-400 border border-rose-500/40 font-bold px-2 py-0.5 rounded text-[10px]">
              LOCKED (₹49 देय)
            </span>
          )}
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={onInstantUnlockDemo}
            className="px-2.5 py-1 bg-emerald-600 hover:bg-emerald-500 text-white rounded font-medium text-[11px] flex items-center gap-1"
          >
            <Unlock className="w-3 h-3" />
            <span>तुरंत अनलॉक (Demo)</span>
          </button>
          <button
            onClick={onLockChatDemo}
            className="px-2.5 py-1 bg-rose-600 hover:bg-rose-500 text-white rounded font-medium text-[11px] flex items-center gap-1"
          >
            <Lock className="w-3 h-3" />
            <span>लॉक करें</span>
          </button>
        </div>
      </div>

      {/* Admin Tabs */}
      <nav className="flex border-b border-slate-800 bg-slate-950/60 overflow-x-auto">
        <button
          onClick={() => setActiveTab('payments')}
          className={`flex items-center gap-2 px-4 py-3 text-xs font-bold whitespace-nowrap border-b-2 transition-colors ${
            activeTab === 'payments'
              ? 'border-cyan-400 text-cyan-400 bg-slate-900'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <CreditCard className="w-4 h-4" />
          <span>भुगतान सत्यापन</span>
          {pendingPayments.length > 0 && (
            <span className="bg-amber-500 text-slate-950 text-[10px] font-black px-1.5 py-0.2 rounded-full">
              {pendingPayments.length}
            </span>
          )}
        </button>

        <button
          onClick={() => setActiveTab('qr')}
          className={`flex items-center gap-2 px-4 py-3 text-xs font-bold whitespace-nowrap border-b-2 transition-colors ${
            activeTab === 'qr'
              ? 'border-cyan-400 text-cyan-400 bg-slate-900'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <QrCode className="w-4 h-4" />
          <span>QR कोड व UPI सेटिंग्स</span>
        </button>

        <button
          onClick={() => setActiveTab('logs')}
          className={`flex items-center gap-2 px-4 py-3 text-xs font-bold whitespace-nowrap border-b-2 transition-colors ${
            activeTab === 'logs'
              ? 'border-cyan-400 text-cyan-400 bg-slate-900'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <ShieldAlert className="w-4 h-4" />
          <span>सुरक्षा ऑडिट लॉग्स</span>
          {securityLogs.length > 0 && (
            <span className="bg-red-500/30 text-red-300 text-[10px] font-semibold px-1.5 py-0.2 rounded-full">
              {securityLogs.length}
            </span>
          )}
        </button>

        <button
          onClick={() => setActiveTab('users')}
          className={`flex items-center gap-2 px-4 py-3 text-xs font-bold whitespace-nowrap border-b-2 transition-colors ${
            activeTab === 'users'
              ? 'border-cyan-400 text-cyan-400 bg-slate-900'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Users className="w-4 h-4" />
          <span>यूज़र नियंत्रण</span>
        </button>
      </nav>

      {/* Main Tab Content */}
      <main className="flex-1 p-4 md:p-6 max-w-4xl w-full mx-auto space-y-6">
        {/* TAB 1: PAYMENTS */}
        {activeTab === 'payments' && (
          <div className="space-y-4">
            <div>
              <h2 className="text-base font-bold text-white">
                प्रलंबित सत्यापन अनुरोध ({pendingPayments.length})
              </h2>
              <p className="text-xs text-slate-400">
                उपयोगकर्ताओं द्वारा सबमिट किए गए ₹49 स्क्रीनशॉट एवं UTR नंबर की जांच कर अनुमोदित करें।
              </p>
            </div>

            {pendingPayments.length === 0 ? (
              <div className="p-8 text-center bg-slate-800/40 rounded-2xl border border-slate-800">
                <Check className="w-8 h-8 text-emerald-400 mx-auto mb-2 opacity-60" />
                <p className="text-sm font-semibold text-slate-300">
                  कोई नया प्रलंबित भुगतान नहीं है
                </p>
                <p className="text-xs text-slate-500 mt-0.5">
                  सभी प्राप्त अनुरोध पहले ही सत्यापित किए जा चुके हैं।
                </p>
              </div>
            ) : (
              <div className="grid gap-3">
                {pendingPayments.map((req) => (
                  <div
                    key={req.id}
                    className="p-4 bg-slate-800/70 border border-amber-500/40 rounded-2xl space-y-3"
                  >
                    <div className="flex items-center justify-between">
                      <div>
                        <h4 className="font-bold text-sm text-white">{req.userName}</h4>
                        <span className="text-[11px] text-slate-400">
                          समय: {new Date(req.submittedAt).toLocaleTimeString()} • {new Date(req.submittedAt).toLocaleDateString()}
                        </span>
                      </div>
                      <span className="text-lg font-black text-rose-400">
                        ₹{req.amount}
                      </span>
                    </div>

                    <div className="bg-slate-900/80 p-3 rounded-xl border border-slate-700/60 text-xs space-y-1">
                      <div className="flex justify-between">
                        <span className="text-slate-400">UTR / Ref No:</span>
                        <span className="font-mono font-bold text-cyan-300">{req.utrNumber}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-slate-400">स्क्रीनशॉट फ़ाइल:</span>
                        <span className="text-slate-300 truncate max-w-[200px]">{req.screenshotName}</span>
                      </div>
                    </div>

                    {/* Approve / Reject Actions */}
                    <div className="flex gap-2 pt-1">
                      <button
                        onClick={() => onApprovePayment(req.id)}
                        className="flex-1 py-2 px-3 bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs rounded-xl flex items-center justify-center gap-1.5 transition-colors shadow-sm"
                      >
                        <Check className="w-4 h-4" />
                        <span>स्वीकृत करें (Approve)</span>
                      </button>
                      <button
                        onClick={() => setRejectingReqId(req.id)}
                        className="flex-1 py-2 px-3 bg-red-600/20 hover:bg-red-600/30 text-red-300 border border-red-500/40 font-bold text-xs rounded-xl flex items-center justify-center gap-1.5 transition-colors"
                      >
                        <X className="w-4 h-4" />
                        <span>अस्वीकार करें</span>
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* History */}
            {allPayments.filter((p) => p.status !== 'PENDING').length > 0 && (
              <div className="pt-4 border-t border-slate-800 space-y-2">
                <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                  सत्यापन इतिहास (History)
                </h3>
                <div className="space-y-2">
                  {allPayments
                    .filter((p) => p.status !== 'PENDING')
                    .map((item) => (
                      <div
                        key={item.id}
                        className="p-3 bg-slate-800/40 border border-slate-800 rounded-xl flex items-center justify-between text-xs"
                      >
                        <div>
                          <p className="font-mono font-semibold text-slate-300">
                            UTR: {item.utrNumber}
                          </p>
                          <p className="text-[10px] text-slate-500">
                            ₹{item.amount} • {new Date(item.submittedAt).toLocaleDateString()}
                          </p>
                        </div>
                        <span
                          className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                            item.status === 'APPROVED'
                              ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                              : 'bg-red-500/20 text-red-400 border border-red-500/30'
                          }`}
                        >
                          {item.status}
                        </span>
                      </div>
                    ))}
                </div>
              </div>
            )}
          </div>
        )}

        {/* TAB 2: QR & UPI SETTINGS */}
        {activeTab === 'qr' && (
          <div className="space-y-6">
            <div>
              <h2 className="text-base font-bold text-white">
                UPI QR कोड एवं भुगतान दर सेटिंग्स
              </h2>
              <p className="text-xs text-slate-400">
                यहाँ अपना UPI ID अपडेट करें। यह तुरंत यूज़र ऐप के QR कोड और पेमेंट बटन में अपडेट हो जाएगा।
              </p>
            </div>

            <form onSubmit={handleSaveQrConfig} className="bg-slate-800/70 border border-slate-700 rounded-2xl p-5 space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  UPI ID (GPay / PhonePe / Paytm):
                </label>
                <input
                  type="text"
                  value={upiId}
                  onChange={(e) => setUpiId(e.target.value)}
                  placeholder="उदा. safemeet@okhdfcbank"
                  className="w-full px-3 py-2 bg-slate-900 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-cyan-400"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  व्यापारी / प्राप्तकर्ता नाम (Merchant Name):
                </label>
                <input
                  type="text"
                  value={payeeName}
                  onChange={(e) => setPayeeName(e.target.value)}
                  placeholder="उदा. SafeMeet Payments"
                  className="w-full px-3 py-2 bg-slate-900 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-cyan-400"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  चैट अनलॉक शुल्क (₹):
                </label>
                <input
                  type="number"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  placeholder="49"
                  className="w-full px-3 py-2 bg-slate-900 border border-slate-700 rounded-xl text-sm text-white focus:outline-none focus:border-cyan-400"
                  required
                />
              </div>

              <div className="flex items-center justify-between p-3 bg-slate-900/70 border border-slate-700 rounded-xl">
                <div>
                  <p className="text-xs font-bold text-white">महिला सुरक्षा एवं DLP फ़िल्टर</p>
                  <p className="text-[11px] text-slate-400">लड़कियों की ID पर फ़ोन नंबर/Instagram शेयरिंग रोकें</p>
                </div>
                <input
                  type="checkbox"
                  checked={strictMode}
                  onChange={(e) => setStrictMode(e.target.checked)}
                  className="w-5 h-5 accent-emerald-500 rounded cursor-pointer"
                />
              </div>

              <button
                type="submit"
                className="w-full py-2.5 px-4 bg-cyan-600 hover:bg-cyan-500 text-white font-bold text-xs rounded-xl flex items-center justify-center gap-1.5 transition-colors shadow-md"
              >
                <Save className="w-4 h-4" />
                <span>परिवर्तन सुरक्षित करें (Save QR Settings)</span>
              </button>

              {savedSuccess && (
                <div className="p-2.5 bg-emerald-500/20 border border-emerald-500/40 text-emerald-300 text-xs rounded-lg text-center font-medium">
                  सेटिंग्स सफलतापूर्वक सेव हो गई! ✅
                </div>
              )}
            </form>

            {/* Live QR Preview */}
            <div className="bg-slate-800/40 border border-slate-800 rounded-2xl p-5 text-center space-y-3">
              <h3 className="font-bold text-sm text-white">वर्तमान लाइव QR कोड प्रीव्यू</h3>
              <div className="flex justify-center">
                <QrCodeView data={previewUpiPayload} size={180} />
              </div>
              <p className="text-[11px] font-mono text-slate-400 break-all max-w-md mx-auto">
                {previewUpiPayload}
              </p>
            </div>
          </div>
        )}

        {/* TAB 3: SECURITY LOGS */}
        {activeTab === 'logs' && (
          <div className="space-y-4">
            <div>
              <h2 className="text-base font-bold text-white">
                महिला सुरक्षा उल्लंघन लॉग्स ({securityLogs.length})
              </h2>
              <p className="text-xs text-slate-400">
                लड़कियों की प्रोफाइल पर फ़ोन नंबर, Instagram या व्यक्तिगत डेटा भेजने के अवरुद्ध प्रयास यहाँ दर्ज होते हैं।
              </p>
            </div>

            {securityLogs.length === 0 ? (
              <div className="p-8 text-center bg-slate-800/40 rounded-2xl border border-slate-800">
                <ShieldCheck className="w-8 h-8 text-emerald-400 mx-auto mb-2 opacity-60" />
                <p className="text-sm font-semibold text-slate-300">
                  सब सुरक्षित है! कोई भी सुरक्षा उल्लंघन दर्ज नहीं हुआ।
                </p>
              </div>
            ) : (
              <div className="space-y-2.5">
                {securityLogs.map((log) => (
                  <div
                    key={log.id}
                    className="p-3.5 bg-red-950/40 border border-red-500/30 rounded-xl text-xs space-y-1"
                  >
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-red-300">लक्षित: {log.targetUserName}</span>
                      <span className="bg-red-500/30 text-red-200 border border-red-500/50 text-[10px] font-bold px-1.5 py-0.5 rounded">
                        {log.violationType}
                      </span>
                    </div>
                    <p className="text-slate-300 bg-black/40 p-2 rounded border border-red-900/50 font-mono text-[11px]">
                      "{log.attemptedText}"
                    </p>
                    <span className="text-[10px] text-slate-500 block">
                      {new Date(log.timestamp).toLocaleString()}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* TAB 4: USERS */}
        {activeTab === 'users' && (
          <div className="space-y-4">
            <div>
              <h2 className="text-base font-bold text-white">
                उपयोगकर्ता नियंत्रण ({users.length})
              </h2>
              <p className="text-xs text-slate-400">
                लड़के और लड़कियों के प्रोफाइल का प्रबंधन करें। सुरक्षा शील्ड सक्रिय/निष्क्रिय करें या बैन करें।
              </p>
            </div>

            <div className="space-y-2.5">
              {users.map((user) => (
                <div
                  key={user.id}
                  className="p-3.5 bg-slate-800/60 border border-slate-700 rounded-xl flex items-center justify-between gap-3 text-xs"
                >
                  <div className="flex items-center gap-3 min-w-0">
                    <div
                      className="w-9 h-9 rounded-full flex items-center justify-center font-bold text-white shrink-0"
                      style={{ backgroundColor: user.avatarColor }}
                    >
                      {user.name.charAt(0)}
                    </div>
                    <div className="min-w-0">
                      <div className="flex items-center gap-1.5">
                        <span className="font-bold text-white truncate">{user.name}</span>
                        <span
                          className={`text-[10px] font-semibold ${
                            user.gender === 'FEMALE' ? 'text-rose-400' : 'text-blue-400'
                          }`}
                        >
                          ({user.gender === 'FEMALE' ? 'लड़की' : 'लड़का'})
                        </span>
                      </div>
                      <span className="text-[11px] text-slate-400 truncate block">
                        {user.city} • {user.isBanned ? '🚫 BANNED' : 'सक्रिय'}
                      </span>
                    </div>
                  </div>

                  <div className="flex items-center gap-2 shrink-0">
                    {user.gender === 'FEMALE' && (
                      <button
                        onClick={() => onTogglePrivacyShield(user.id, !user.privacyShieldEnabled)}
                        title="Toggle Privacy Shield"
                        className={`p-1.5 rounded-lg border transition-colors ${
                          user.privacyShieldEnabled
                            ? 'bg-emerald-500/20 text-emerald-400 border-emerald-500/40'
                            : 'bg-slate-700 text-slate-400 border-slate-600'
                        }`}
                      >
                        {user.privacyShieldEnabled ? <ShieldCheck className="w-4 h-4" /> : <ShieldOff className="w-4 h-4" />}
                      </button>
                    )}

                    <button
                      onClick={() => onToggleUserBan(user.id, !user.isBanned)}
                      title="Ban / Unban User"
                      className={`p-1.5 rounded-lg border transition-colors ${
                        user.isBanned
                          ? 'bg-red-500/20 text-red-400 border-red-500/40'
                          : 'bg-slate-700 text-slate-300 border-slate-600 hover:text-red-400'
                      }`}
                    >
                      {user.isBanned ? <UserCheck className="w-4 h-4" /> : <UserX className="w-4 h-4" />}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>

      {/* Reject Modal */}
      {rejectingReqId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-black/70 backdrop-blur-xs">
          <div className="bg-slate-900 border border-slate-700 w-full max-w-sm rounded-2xl p-5 space-y-3">
            <h4 className="font-bold text-sm text-white">भुगतान अस्वीकार करें</h4>
            <p className="text-xs text-slate-400">अस्वीकृति का कारण दर्ज करें:</p>
            <input
              type="text"
              value={rejectReason}
              onChange={(e) => setRejectReason(e.target.value)}
              className="w-full px-3 py-2 bg-slate-950 border border-slate-700 rounded-lg text-xs text-white focus:outline-none"
            />
            <div className="flex gap-2 pt-2">
              <button
                onClick={() => {
                  onRejectPayment(rejectingReqId, rejectReason);
                  setRejectingReqId(null);
                }}
                className="flex-1 py-2 bg-red-600 hover:bg-red-500 text-white font-bold text-xs rounded-lg"
              >
                अस्वीकार करें
              </button>
              <button
                onClick={() => setRejectingReqId(null)}
                className="flex-1 py-2 bg-slate-800 text-slate-300 font-bold text-xs rounded-lg"
              >
                रद्द करें
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
