"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { TermsModal } from "@/components/terms-modal"
import { PrivacyModal } from "@/components/privacy-modal"

export function LegalLinks() {
  const [showTerms, setShowTerms] = useState(false)
  const [showPrivacy, setShowPrivacy] = useState(false)

  return (
    <div className="flex flex-col items-center justify-center gap-4 p-4">
      <h2 className="text-xl font-bold">Legal Documents</h2>
      <div className="flex gap-4">
        <Button
          variant="outline"
          onClick={() => setShowTerms(true)}
          className="bg-[#008080] text-white hover:bg-[#005F6B]"
        >
          View Terms of Service
        </Button>
        <Button
          variant="outline"
          onClick={() => setShowPrivacy(true)}
          className="bg-[#008080] text-white hover:bg-[#005F6B]"
        >
          View Privacy Policy
        </Button>
      </div>

      {/* Modals */}
      <TermsModal defaultOpen={showTerms} onOpenChange={setShowTerms} />
      <PrivacyModal defaultOpen={showPrivacy} onOpenChange={setShowPrivacy} />
    </div>
  )
}
