import React, { useState, useEffect, useRef } from 'react';
import {
  ArrowLeft,
  Send,
  Shield,
  Lock,
  AlertTriangle,
  Flag,
  CheckCheck
} from 'lucide-react';
import { ChatMessage, User } from '../types.ts';
import { SecurityCheckResult } from '../security/privacyGuard.ts';

interface ChatViewProps {
  user: User;
  messages: ChatMessage[];
  onSendMessage: (text: string) => void;
  onBack: () => void;
  securityAlert: SecurityCheckResult | null;
  onDismissSecurityAlert: () => void;
}

export const ChatView: React.FC<ChatViewProps> = ({
  user,
  messages,
  onSendMessage,
  onBack,
  securityAlert,
  onDismissSecurityAlert,
}) => {
  const [inputText, setInputText] = useState('');
  const [showReportDialog, setShowReportDialog] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement | null>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputText.trim()) return;
    onSendMessage(inputText.trim());
    setInputText('');
  };

  return (
    <div className="flex flex-col h-screen max-w-md mx-auto bg-slate-50 border-x border-slate-200">
      {/* Top Header */}
      <header className="p-3 bg-white border-b border-slate-200 flex items-center justify-between sticky top-0 z-20">
        <div className="flex items-center gap-2.5 min-w-0">
          <button
            onClick={onBack}
            className="w-8 h-8 rounded-full hover:bg-slate-100 flex items-center justify-center text-slate-700"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>

          <div
            className="w-10 h-10 rounded-full flex items-center justify-center text-white font-bold text-sm shrink-0 shadow-xs"
            style={{ backgroundColor: user.avatarColor }}
          >
            {user.name.charAt(0)}
          </div>

          <div className="min-w-0">
            <div className="flex items-center gap-1.5">
              <h3 className="font-bold text-sm text-slate-900 truncate">{user.name}</h3>
              {user.gender === 'FEMALE' && user.privacyShieldEnabled && (
                <Shield className="w-3.5 h-3.5 text-emerald-600 fill-emerald-100 shrink-0" />
              )}
            </div>
            <p className="text-[11px] text-emerald-600 font-medium truncate">
              {user.gender === 'FEMALE' ? 'सुरक्षित चैट • डेटा लीक प्रतिबंधित' : `${user.city} • ऑनलाइन`}
            </p>
          </div>
        </div>

        <button
          onClick={() => setShowReportDialog(true)}
          className="text-slate-400 hover:text-slate-600 p-1.5 rounded-lg"
          title="Report"
        >
          <Flag className="w-4 h-4" />
        </button>
      </header>

      {/* Female Security Shield Sub-Header */}
      {user.gender === 'FEMALE' && (
        <div className="bg-emerald-100/80 border-b border-emerald-200 px-3 py-1.5 flex items-center gap-2 text-[11px] text-emerald-900">
          <Lock className="w-3.5 h-3.5 text-emerald-700 shrink-0" />
          <p className="truncate">
            🛡️ महिला सुरक्षा सक्रिय: फ़ोन नंबर, WhatsApp या सोशल ID शेयर करना ब्लॉक है।
          </p>
        </div>
      )}

      {/* Message List */}
      <div className="flex-1 overflow-y-auto p-4 space-y-3">
        {/* Intro Safety Tip */}
        <div className="bg-white border border-slate-200 rounded-2xl p-3.5 text-center shadow-2xs space-y-1">
          <div className="w-8 h-8 rounded-full bg-rose-50 text-rose-500 mx-auto flex items-center justify-center">
            <Shield className="w-4 h-4" />
          </div>
          <h4 className="font-bold text-xs text-slate-800">
            SafeMeet एंड-टू-एंड सुरक्षित बातचीत
          </h4>
          <p className="text-[11px] text-slate-500 leading-tight">
            लड़कियों की निजता सर्वोपरि है। व्यक्तिगत जानकारी छुपाकर केवल स्वस्थ बातचीत करें।
          </p>
        </div>

        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`flex flex-col ${msg.isFromMe ? 'items-end' : 'items-start'}`}
          >
            <div
              className={`max-w-[80%] rounded-2xl px-3.5 py-2.5 text-xs shadow-2xs ${
                msg.isBlockedOrMasked
                  ? 'bg-red-50 border border-red-300 text-red-900'
                  : msg.isFromMe
                  ? 'bg-rose-600 text-white rounded-br-xs'
                  : 'bg-white border border-slate-200 text-slate-800 rounded-bl-xs'
              }`}
            >
              {msg.isBlockedOrMasked && (
                <div className="flex items-center gap-1 text-[10px] font-bold text-red-600 mb-1">
                  <AlertTriangle className="w-3.5 h-3.5" />
                  <span>अवरुद्ध संदेश (Blocked by Security)</span>
                </div>
              )}

              <p className="whitespace-pre-wrap leading-relaxed">{msg.text}</p>

              {msg.securityViolationReason && (
                <p className="text-[10px] text-red-600 font-semibold mt-1">
                  {msg.securityViolationReason}
                </p>
              )}

              <div
                className={`text-[9px] mt-1 text-right flex items-center justify-end gap-1 ${
                  msg.isFromMe && !msg.isBlockedOrMasked ? 'text-rose-100' : 'text-slate-400'
                }`}
              >
                <span>{new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                {msg.isFromMe && !msg.isBlockedOrMasked && <CheckCheck className="w-3 h-3" />}
              </div>
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      {/* Input Field */}
      <form onSubmit={handleSend} className="p-3 bg-white border-t border-slate-200 flex items-center gap-2">
        <input
          type="text"
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          placeholder={
            user.gender === 'FEMALE'
              ? 'सुरक्षित संदेश लिखें (नंबर/ID शेयर न करें)...'
              : 'संदेश लिखें...'
          }
          className="flex-1 px-4 py-2.5 bg-slate-100 border border-slate-200 rounded-full text-xs text-slate-800 focus:outline-none focus:ring-2 focus:ring-rose-500"
        />

        <button
          type="submit"
          className="w-10 h-10 rounded-full bg-rose-600 hover:bg-rose-700 text-white flex items-center justify-center shrink-0 shadow-sm transition-colors"
        >
          <Send className="w-4 h-4" />
        </button>
      </form>

      {/* Security Alert Popup */}
      {securityAlert && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs">
          <div className="bg-white border-2 border-red-500 w-full max-w-sm rounded-3xl p-5 space-y-3.5 shadow-2xl text-slate-800">
            <div className="w-12 h-12 rounded-full bg-red-100 text-red-600 flex items-center justify-center mx-auto">
              <AlertTriangle className="w-6 h-6" />
            </div>

            <div className="text-center">
              <h3 className="font-extrabold text-base text-red-600">
                सुरक्षा नीति उल्लंघन (Blocked)
              </h3>
              <p className="text-xs text-slate-600 mt-1 leading-relaxed">
                {securityAlert.reasonHindi || 'लड़कियों की प्रोफाइल पर फ़ोन नंबर या सोशल ID भेजना प्रतिबंधित है।'}
              </p>
            </div>

            <div className="p-3 bg-red-50 border border-red-200 rounded-xl text-[11px] text-red-800">
              🛡️ आपका संदेश सिस्टम द्वारा ब्लॉक कर दिया गया है एवं सुरक्षा लॉग में दर्ज हुआ है।
            </div>

            <button
              onClick={onDismissSecurityAlert}
              className="w-full py-2.5 bg-red-600 hover:bg-red-700 text-white font-bold text-xs rounded-xl transition-colors"
            >
              समझ गया (Understood)
            </button>
          </div>
        </div>
      )}

      {/* Report Modal */}
      {showReportDialog && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50">
          <div className="bg-white w-full max-w-xs rounded-2xl p-5 space-y-3 text-slate-800">
            <h4 className="font-bold text-sm">रिपोर्ट करें / सुरक्षा सहायता</h4>
            <p className="text-xs text-slate-600">
              यदि कोई यूज़र अनुचित व्यवहार करता है, तो Admin Panel से तुरंत रिपोर्ट कर ब्लॉक किया जा सकता है।
            </p>
            <button
              onClick={() => setShowReportDialog(false)}
              className="w-full py-2 bg-slate-900 text-white rounded-xl text-xs font-semibold"
            >
              ठीक है
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
