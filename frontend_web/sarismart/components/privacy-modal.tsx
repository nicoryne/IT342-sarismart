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

interface PrivacyModalProps {
  trigger?: React.ReactNode
  defaultOpen?: boolean
  onOpenChange?: (open: boolean) => void
}

export function PrivacyModal({ trigger, defaultOpen, onOpenChange }: PrivacyModalProps) {
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
          <DialogTitle className="text-2xl font-bold">Privacy Policy</DialogTitle>
          <DialogDescription>Last updated: April 23, 2025</DialogDescription>
        </DialogHeader>
        <Button variant="ghost" size="icon" className="absolute right-4 top-4" onClick={() => handleOpenChange(false)}>
          <X className="h-4 w-4" />
          <span className="sr-only">Close</span>
        </Button>
        <ScrollArea className="h-[60vh] pr-4">
          <div className="prose prose-gray max-w-none">
            <h2>1. Introduction</h2>
            <p>
              At SariSmart, we respect your privacy and are committed to protecting your personal data. This Privacy
              Policy explains how we collect, use, disclose, and safeguard your information when you use our service.
            </p>

            <h2>2. Information We Collect</h2>
            <p>We collect several types of information from and about users of our service, including:</p>
            <ul>
              <li>
                <strong>Personal Information:</strong> Name, email address, phone number, and other identifiers you
                provide when registering or using our service.
              </li>
              <li>
                <strong>Business Information:</strong> Store details, inventory data, sales records, and other
                information related to your business operations.
              </li>
              <li>
                <strong>Usage Data:</strong> Information about how you access and use our service, including your
                browser type, IP address, and device information.
              </li>
              <li>
                <strong>Location Data:</strong> Geographic location information when you use location-based features of
                our service.
              </li>
            </ul>

            <h2>3. How We Use Your Information</h2>
            <p>We use the information we collect to:</p>
            <ul>
              <li>Provide, maintain, and improve our service</li>
              <li>Process transactions and manage your account</li>
              <li>Send you technical notices, updates, and support messages</li>
              <li>Respond to your comments and questions</li>
              <li>Understand how users interact with our service</li>
              <li>Detect, prevent, and address technical issues</li>
              <li>Protect against harmful or illegal activity</li>
            </ul>

            <h2>4. Data Sharing and Disclosure</h2>
            <p>We may share your information with:</p>
            <ul>
              <li>Service providers who perform services on our behalf</li>
              <li>Business partners with your consent</li>
              <li>Legal authorities when required by law</li>
              <li>In connection with a business transfer or acquisition</li>
            </ul>

            <h2>5. Data Security</h2>
            <p>
              We implement appropriate security measures to protect your personal information. However, no method of
              transmission over the Internet or electronic storage is 100% secure, and we cannot guarantee absolute
              security.
            </p>

            <h2>6. Your Data Rights</h2>
            <p>Depending on your location, you may have rights to:</p>
            <ul>
              <li>Access the personal information we hold about you</li>
              <li>Correct inaccurate or incomplete information</li>
              <li>Delete your personal information</li>
              <li>Restrict or object to certain processing of your data</li>
              <li>Request transfer of your personal information</li>
              <li>Withdraw consent where processing is based on consent</li>
            </ul>

            <h2>7. Cookies and Tracking Technologies</h2>
            <p>
              We use cookies and similar tracking technologies to track activity on our service and hold certain
              information. You can instruct your browser to refuse all cookies or to indicate when a cookie is being
              sent.
            </p>

            <h2>8. Childrens Privacy</h2>
            <p>
              Our service is not intended for children under 16 years of age. We do not knowingly collect personal
              information from children under 16.
            </p>

            <h2>9. Changes to This Privacy Policy</h2>
            <p>
              We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new
              Privacy Policy on this page and updating the Last Updated date.
            </p>

            <h2>10. Contact Us</h2>
            <p>
              If you have questions about this Privacy Policy, please contact us at{" "}
              <a href="mailto:privacy@sarismart.com" className="text-[#008080] hover:underline">
                privacy@sarismart.com
              </a>
              .
            </p>
          </div>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  )
}
