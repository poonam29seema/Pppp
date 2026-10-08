import React, { useState, useEffect } from 'react';
import { Clock, Hourglass, CheckCircle2 } from 'lucide-react';
import { PaymentRequest } from '../types.ts';

interface CountdownTimerCardProps {
  paymentRequest: PaymentRequest;
  onAdminFastTrack?: () => void;
}

export const CountdownTimerCard: React.FC<CountdownTimerCardProps> = ({
  paymentRequest,
  onAdminFastTrack,
}) => {
  const [currentTime, setCurrentTime] = useState(Date.now());

  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(Date.now()), 1000);
    return () => clearInterval(timer);
  }, []);

  const totalDuration = 24 * 60 * 60 * 1000; // 24 hours
  const deadline = paymentRequest.submittedAt + totalDuration;
  const remainingMillis = Math.max(0, deadline - currentTime);

  const hours = Math.floor(remainingMillis / (1000 * 60 * 60));
  const minutes = Math.floor((remainingMillis % (1000 * 60 * 60)) / (1000 * 60));
  const seconds = Math.floor((remainingMillis % (1000 * 60)) / 1000);

  const formattedRemaining = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
  const elapsed = Math.min(totalDuration, Math.max(0, currentTime - paymentRequest.submittedAt));
  const progressPct = Math.min(100, Math.max(0, (elapsed / totalDuration) * 100));

  return (
    <div className="bg-amber-50/90 border-2 border-amber-400 rounded-2xl p-4 text-slate-800 shadow-sm">
      <div className="flex items-center gap-3">
        <div className="w-10 h-10 rounded-full bg-amber-500 text-white flex items-center justify-center animate-pulse shrink-0">
          <Hourglass className="w-5 h-5" />
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2">
            <h4 className="font-bold text-sm text-amber-900 truncate">
              24 घंटे का सत्यापन जारी
            </h4>
            <span className="bg-amber-500 text-white text-[10px] font-extrabold px-1.5 py-0.5 rounded">
              PENDING
            </span>
          </div>
          <p className="text-xs text-amber-800">
            स्क्रीनशॉट एवं UTR नंबर की समीक्षा हो रही है
          </p>
        </div>
      </div>

      {/* Countdown Box */}
      <div className="mt-3.5 bg-white/90 border border-amber-300 rounded-xl p-3 text-center shadow-inner">
        <p className="text-[11px] font-medium text-slate-500">
          सत्यापन समय सीमा (Time Remaining)
        </p>
        <p className="text-3xl font-mono font-extrabold text-amber-600 tracking-wider my-0.5">
          {formattedRemaining}
        </p>
        <p className="text-[10px] text-slate-400">
          अधिकतम 24 घंटे के भीतर एडमिन द्वारा एक्टिवेट होगा
        </p>
      </div>

      {/* Progress Bar */}
      <div className="mt-3 w-full bg-slate-200 h-1.5 rounded-full overflow-hidden">
        <div
          className="bg-amber-500 h-full transition-all duration-1000"
          style={{ width: `${progressPct}%` }}
        />
      </div>

      {/* Receipt Info */}
      <div className="mt-3 bg-white/70 rounded-lg p-2.5 text-xs text-slate-700 space-y-1">
        <div className="flex justify-between">
          <span className="text-slate-500">जमा किया गया शुल्क:</span>
          <span className="font-bold text-slate-900">₹{paymentRequest.amount} (UPI)</span>
        </div>
        <div className="flex justify-between">
          <span className="text-slate-500">UTR / Ref No:</span>
          <span className="font-mono font-semibold text-slate-900">{paymentRequest.utrNumber}</span>
        </div>
        <div className="flex justify-between">
          <span className="text-slate-500">स्क्रीनशॉट फ़ाइल:</span>
          <span className="text-slate-700 truncate max-w-[180px]">{paymentRequest.screenshotName}</span>
        </div>
      </div>

      {onAdminFastTrack && (
        <button
          onClick={onAdminFastTrack}
          className="mt-3 w-full py-2 px-3 border border-emerald-600 bg-emerald-50 hover:bg-emerald-100 text-emerald-800 text-xs font-semibold rounded-lg flex items-center justify-center gap-1.5 transition-colors"
        >
          <CheckCircle2 className="w-4 h-4 text-emerald-600" />
          <span>Admin Fast-Track: तुरंत अनलॉक करें (Instant Demo)</span>
        </button>
      )}
    </div>
  );
};
