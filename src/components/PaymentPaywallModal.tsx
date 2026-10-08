import React, { useState } from 'react';
import { X, Lock, Copy, Check, ExternalLink, Image as ImageIcon, UploadCloud, Shield, AlertTriangle } from 'lucide-react';
import { AppConfig, PaymentRequest } from '../types.ts';
import { QrCodeView } from './QrCodeView.tsx';
import { CountdownTimerCard } from './CountdownTimerCard.tsx';

interface PaymentPaywallModalProps {
  appConfig: AppConfig;
  latestPayment: PaymentRequest | null;
  onSubmitPayment: (utr: string, screenshotNote: string) => void;
  onAdminFastTrackApprove: () => void;
  onClose: () => void;
}

export const PaymentPaywallModal: React.FC<PaymentPaywallModalProps> = ({
  appConfig,
  latestPayment,
  onSubmitPayment,
  onAdminFastTrackApprove,
  onClose,
}) => {
  const [utrInput, setUtrInput] = useState('');
  const [selectedScreenshotName, setSelectedScreenshotName] = useState('upi_payment_receipt_49.png');
  const [isScreenshotSelected, setIsScreenshotSelected] = useState(false);
  const [copied, setCopied] = useState(false);

  const unlockAmount = appConfig.unlockAmount || 49;
  const upiId = appConfig.upiId || 'safemeet99@okhdfcbank';
  const payeeName = appConfig.payeeName || 'SafeMeet Payments';

  const upiPayload = `upi://pay?pa=${upiId}&pn=${encodeURIComponent(payeeName)}&am=${unlockAmount}&cu=INR&tn=SafeMeet%20Chat%20Access`;

  const handleCopyUpi = () => {
    navigator.clipboard.writeText(upiId);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const finalUtr = utrInput.trim() || `UTR${Date.now().toString().slice(-8)}`;
    const finalNote = isScreenshotSelected ? selectedScreenshotName : 'upi_screenshot_proof.jpg';
    onSubmitPayment(finalUtr, finalNote);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 bg-slate-900/70 backdrop-blur-xs overflow-y-auto">
      <div className="bg-white w-full max-w-md rounded-3xl shadow-2xl border border-slate-200 overflow-hidden my-auto max-h-[92vh] flex flex-col">
        {/* Header */}
        <div className="p-4 border-b border-slate-100 flex items-center justify-between bg-slate-50">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-full bg-rose-100 text-rose-600 flex items-center justify-center">
              <Lock className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-bold text-base text-slate-800 leading-tight">
                चैट अनलॉक पास
              </h3>
              <p className="text-xs font-semibold text-rose-600">
                ₹{unlockAmount} One-time Lifetime Pass
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-slate-200/80 hover:bg-slate-300 text-slate-600 flex items-center justify-center transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Modal Scrollable Body */}
        <div className="p-5 overflow-y-auto space-y-4 text-slate-800">
          {/* Active 24-hr review state if already pending */}
          {latestPayment && latestPayment.status === 'PENDING' && (
            <CountdownTimerCard
              paymentRequest={latestPayment}
              onAdminFastTrack={onAdminFastTrackApprove}
            />
          )}

          {/* Approved state */}
          {latestPayment && latestPayment.status === 'APPROVED' && (
            <div className="p-3.5 bg-emerald-50 border border-emerald-300 rounded-xl flex items-center gap-3">
              <Check className="w-6 h-6 text-emerald-600" />
              <div>
                <p className="font-bold text-sm text-emerald-900">भुगतान सत्यापित (Approved)</p>
                <p className="text-xs text-emerald-700">आपकी चैट सेवा सक्रिय हो चुकी है!</p>
              </div>
            </div>
          )}

          {/* Rejected note */}
          {latestPayment && latestPayment.status === 'REJECTED' && (
            <div className="p-3 bg-red-50 border border-red-300 rounded-xl text-xs text-red-800 flex items-start gap-2">
              <AlertTriangle className="w-4 h-4 text-red-600 shrink-0 mt-0.5" />
              <div>
                <span className="font-bold">अस्वीकृत: </span>
                {latestPayment.rejectionReason || 'अमान्य UTR या स्क्रीनशॉट'}। कृपया दोबारा सही विवरण के साथ सबमिट करें।
              </div>
            </div>
          )}

          {/* Security Notice */}
          <div className="p-3 bg-rose-50/70 border border-rose-200 rounded-xl flex items-center gap-2.5 text-xs text-rose-900">
            <Shield className="w-4 h-4 text-rose-600 shrink-0" />
            <p>
              लड़कों एवं लड़कियों के बीच सुरक्षित और वास्तविक बातचीत के लिए ₹{unlockAmount} का सत्यापन अनिवार्य है।
            </p>
          </div>

          {/* STEP 1: PAYMENT */}
          <div className="space-y-2.5">
            <h4 className="font-bold text-sm text-slate-900">
              स्टेप 1: स्कैन करें या UPI से ₹{unlockAmount} भुगतान करें
            </h4>

            {/* QR Code */}
            <div className="flex justify-center py-1">
              <QrCodeView data={upiPayload} size={180} />
            </div>

            {/* UPI ID Copy Card */}
            <div className="bg-slate-50 border border-slate-200 rounded-xl p-3 flex items-center justify-between gap-2">
              <div className="min-w-0">
                <span className="block text-[10px] text-slate-400 font-medium">
                  UPI ID (PhonePe / GPay / Paytm):
                </span>
                <span className="block font-mono font-bold text-xs text-slate-800 truncate">
                  {upiId}
                </span>
              </div>
              <button
                onClick={handleCopyUpi}
                className="px-3 py-1.5 bg-rose-600 hover:bg-rose-700 text-white rounded-lg text-xs font-semibold flex items-center gap-1 shrink-0 transition-colors"
              >
                {copied ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
                <span>{copied ? 'कॉपी हुआ' : 'कॉपी करें'}</span>
              </button>
            </div>

            {/* Direct Pay Link */}
            <a
              href={upiPayload}
              className="w-full py-2.5 px-3 border border-slate-300 hover:border-slate-400 bg-white hover:bg-slate-50 text-slate-700 font-medium text-xs rounded-xl flex items-center justify-center gap-2 transition-colors"
            >
              <ExternalLink className="w-4 h-4" />
              <span>UPI ऐप खोलें (Pay ₹{unlockAmount})</span>
            </a>
          </div>

          <hr className="border-slate-200 my-2" />

          {/* STEP 2: PROOF SUBMISSION */}
          <form onSubmit={handleSubmit} className="space-y-3">
            <div>
              <h4 className="font-bold text-sm text-slate-900">
                स्टेप 2: भुगतान का स्क्रीनशॉट एवं UTR सबमिट करें
              </h4>
              <p className="text-xs text-slate-500">
                स्क्रीनशॉट सबमिट करने के 24 घंटे के अंदर सत्यापन पूरा होगा।
              </p>
            </div>

            {/* Screenshot attachment picker */}
            <div
              onClick={() => {
                setIsScreenshotSelected(true);
                setSelectedScreenshotName(`upi_receipt_₹${unlockAmount}_${Date.now().toString().slice(-4)}.jpg`);
              }}
              className={`p-3.5 border-2 border-dashed rounded-xl cursor-pointer transition-all flex items-center gap-3 ${
                isScreenshotSelected
                  ? 'border-emerald-500 bg-emerald-50/70 text-emerald-900'
                  : 'border-slate-300 hover:border-rose-400 bg-slate-50 text-slate-600'
              }`}
            >
              <div
                className={`w-10 h-10 rounded-full flex items-center justify-center shrink-0 ${
                  isScreenshotSelected ? 'bg-emerald-600 text-white' : 'bg-rose-500 text-white'
                }`}
              >
                {isScreenshotSelected ? <Check className="w-5 h-5" /> : <ImageIcon className="w-5 h-5" />}
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-semibold text-xs truncate">
                  {isScreenshotSelected ? 'स्क्रीनशॉट अटैच हो गया ✅' : 'पेमेंट का स्क्रीनशॉट चुनें (Tap to Attach)'}
                </p>
                <p className="text-[11px] text-slate-400 truncate">
                  {isScreenshotSelected ? selectedScreenshotName : 'Google Pay / PhonePe / Paytm रसीद का फोटो'}
                </p>
              </div>
            </div>

            {/* UTR Input */}
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                12 अंकों का UTR / UPI Ref ID
              </label>
              <input
                type="text"
                value={utrInput}
                onChange={(e) => setUtrInput(e.target.value.slice(0, 16))}
                placeholder="उदा. 423985172634"
                className="w-full px-3 py-2.5 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-rose-500"
              />
            </div>

            {/* Submit Button */}
            <button
              type="submit"
              className="w-full py-3 px-4 bg-rose-600 hover:bg-rose-700 text-white font-bold text-sm rounded-xl flex items-center justify-center gap-2 shadow-md hover:shadow-lg transition-all"
            >
              <UploadCloud className="w-4 h-4" />
              <span>स्क्रीनशॉट सबमिट करें (24 घंटे में रिव्यू)</span>
            </button>
          </form>

          <p className="text-[10px] text-center text-slate-400 leading-tight">
            🔒 SafeMeet सुरक्षा वादा: आपका भुगतान पूर्णतः सुरक्षित है। Admin द्वारा 24 घंटे में जांच के बाद चैट सक्रिय होगी।
          </p>
        </div>
      </div>
    </div>
  );
};
