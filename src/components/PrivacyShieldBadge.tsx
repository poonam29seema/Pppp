import React from 'react';
import { Shield, ShieldCheck } from 'lucide-react';

export const PrivacyShieldBadge: React.FC<{ isFemale?: boolean }> = ({ isFemale = true }) => {
  return (
    <div className="inline-flex items-center gap-1.5 px-2.5 py-1 bg-emerald-50 border border-emerald-300 text-emerald-800 rounded-full text-[11px] font-semibold shadow-xs">
      <Shield className="w-3.5 h-3.5 text-emerald-600 fill-emerald-100" />
      <span>{isFemale ? 'महिला सुरक्षा शील्ड (100% Secure)' : 'सत्यापित प्रोफ़ाइल'}</span>
    </div>
  );
};

export const PrivacyInfoBanner: React.FC = () => {
  return (
    <div className="w-full bg-emerald-50/90 border border-emerald-200 rounded-2xl p-3.5 flex items-start gap-3 shadow-xs">
      <div className="w-9 h-9 rounded-xl bg-emerald-600 text-white flex items-center justify-center shrink-0 shadow-sm">
        <ShieldCheck className="w-5 h-5" />
      </div>
      <div className="text-xs">
        <h4 className="font-bold text-emerald-900 text-sm">
          महिला सुरक्षा एवं गोपनीयता गारंटी
        </h4>
        <p className="text-emerald-800/90 mt-0.5 leading-relaxed">
          लड़कियों की ID पर फ़ोन नंबर, Instagram, WhatsApp या व्यक्तिगत जानकारी शेयर करना सिस्टम द्वारा स्वतः ब्लॉक है। 100% सुरक्षित और मर्यादित बातचीत।
        </p>
      </div>
    </div>
  );
};
