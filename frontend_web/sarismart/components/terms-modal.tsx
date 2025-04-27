"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { X } from "lucide-react"

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { ScrollArea } from "@/components/ui/scroll-area"

interface TermsModalProps {
  trigger?: React.ReactNode
  defaultOpen?: boolean
  onOpenChange?: (open: boolean) => void
}

export function TermsModal({ trigger, defaultOpen, onOpenChange }: TermsModalProps) {
  const [open, setOpen] = useState(defaultOpen || false)

  // Add this useEffect to respond to defaultOpen prop changes
  useEffect(() => {
    setOpen(defaultOpen || false)
  }, [defaultOpen])

  const handleOpenChange = (open: boolean) => {
    setOpen(open)
    if (onOpenChange) onOpenChange(open)
  }

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      {trigger && <DialogTrigger asChild>{trigger}</DialogTrigger>}
      <DialogContent className="sm:max-w-[700px] max-h-[90vh]">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold">Terms of Service</DialogTitle>
          <DialogDescription>Last updated: April 23, 2025</DialogDescription>
        </DialogHeader>
        <Button variant="ghost" size="icon" className="absolute right-4 top-4" onClick={() => handleOpenChange(false)}>
          <X className="h-4 w-4" />
          <span className="sr-only">Close</span>
        </Button>
        <ScrollArea className="h-[60vh] pr-4">
          <div className="prose prose-gray max-w-none">
            <h2>1. Acceptance of Terms</h2>
            <p>
              By accessing or using SariSmart's services, you agree to be bound by these Terms of Service. If you do not
              agree to these terms, please do not use our services.
            </p>

            <h2>2. Description of Service</h2>
            <p>
              SariSmart provides inventory management and business intelligence tools for retail businesses. We reserve
              the right to modify, suspend, or discontinue any part of the service at any time.
            </p>

            <h2>3. User Accounts</h2>
            <p>
              You are responsible for maintaining the confidentiality of your account information and for all activities
              that occur under your account. You agree to notify SariSmart immediately of any unauthorized use of your
              account.
            </p>

            <h2>4. User Conduct</h2>
            <p>You agree not to:</p>
            <ul>
              <li>Use the service for any illegal purpose</li>
              <li>Violate any laws in your jurisdiction</li>
              <li>Infringe upon the rights of others</li>
              <li>Distribute malware or other harmful code</li>
              <li>Interfere with the proper functioning of the service</li>
            </ul>

            <h2>5. Data and Privacy</h2>
            <p>Our collection and use of personal information is governed by our Privacy Policy.</p>

            <h2>6. Intellectual Property</h2>
            <p>
              All content, features, and functionality of the SariSmart service are owned by SariSmart and are protected
              by copyright, trademark, and other intellectual property laws.
            </p>

            <h2>7. Termination</h2>
            <p>
              We may terminate or suspend your account and access to the service immediately, without prior notice, for
              conduct that we believe violates these Terms or is harmful to other users, us, or third parties.
            </p>

            <h2>8. Disclaimer of Warranties</h2>
            <p>
              THE SERVICE IS PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING, BUT
              NOT LIMITED TO, IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
              NON-INFRINGEMENT.
            </p>

            <h2>9. Limitation of Liability</h2>
            <p>
              SARISMART SHALL NOT BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, CONSEQUENTIAL, OR PUNITIVE DAMAGES
              RESULTING FROM YOUR ACCESS TO OR USE OF, OR INABILITY TO ACCESS OR USE, THE SERVICE.
            </p>

            <h2>10. Changes to Terms</h2>
            <p>
              We reserve the right to modify these Terms at any time. We will provide notice of significant changes by
              posting the new Terms on the service and updating the "Last Updated" date.
            </p>

            <h2>11. Governing Law</h2>
            <p>
              These Terms shall be governed by the laws of the Philippines, without regard to its conflict of law
              provisions.
            </p>

            <h2>12. Contact Information</h2>
            <p>
              For questions about these Terms, please contact us at{" "}
              <a href="mailto:legal@sarismart.com" className="text-[#008080] hover:underline">
                legal@sarismart.com
              </a>
              .
            </p>
          </div>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  )
}
