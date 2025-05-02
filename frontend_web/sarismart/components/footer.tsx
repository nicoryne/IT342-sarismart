"use client"

import { useState } from "react"
import Link from "next/link"
import { Facebook, Instagram, Linkedin, Twitter } from "lucide-react"
import { TermsModal } from "@/components/terms-modal"
import { PrivacyModal } from "@/components/privacy-modal"

export default function Footer() {
  const [showTermsModal, setShowTermsModal] = useState(false)
  const [showPrivacyModal, setShowPrivacyModal] = useState(false)

  return (
    <footer className="border-t bg-white py-8 md:py-12">
      <div className="container px-4 md:px-6">
        <div className="grid gap-8 sm:grid-cols-2 md:grid-cols-4">
          <div>
            <Link href="/" className="flex items-center gap-2">
              <span className="text-xl font-bold">
                <span className="text-[#008080]">Sari</span>
                <span>Smart</span>
              </span>
            </Link>
            <p className="mt-4 text-sm text-gray-600">
              Empowering businesses with intelligent solutions that drive growth, efficiency, and innovation.
            </p>
            <div className="mt-4 flex space-x-4">
              <Link href="#" className="text-gray-500 hover:text-[#008080]">
                <Twitter className="h-5 w-5" />
                <span className="sr-only">Twitter</span>
              </Link>
              <Link href="#" className="text-gray-500 hover:text-[#008080]">
                <Facebook className="h-5 w-5" />
                <span className="sr-only">Facebook</span>
              </Link>
              <Link href="#" className="text-gray-500 hover:text-[#008080]">
                <Instagram className="h-5 w-5" />
                <span className="sr-only">Instagram</span>
              </Link>
              <Link href="#" className="text-gray-500 hover:text-[#008080]">
                <Linkedin className="h-5 w-5" />
                <span className="sr-only">LinkedIn</span>
              </Link>
            </div>
          </div>

          <div>
            <h3 className="text-lg font-medium">Company</h3>
            <ul className="mt-4 space-y-2">
              <li>
                <Link href="/about" className="text-sm text-gray-600 hover:text-[#008080]">
                  About Us
                </Link>
              </li>
              <li>
                <Link href="/careers" className="text-sm text-gray-600 hover:text-[#008080]">
                  Careers
                </Link>
              </li>
              <li>
                <Link href="/blog" className="text-sm text-gray-600 hover:text-[#008080]">
                  Blog
                </Link>
              </li>
              <li>
                <Link href="/press" className="text-sm text-gray-600 hover:text-[#008080]">
                  Press
                </Link>
              </li>
            </ul>
          </div>

          <div>
            <h3 className="text-lg font-medium">Products</h3>
            <ul className="mt-4 space-y-2">
              <li>
                <Link href="/products/analytics" className="text-sm text-gray-600 hover:text-[#008080]">
                  Analytics
                </Link>
              </li>
              <li>
                <Link href="/products/automation" className="text-sm text-gray-600 hover:text-[#008080]">
                  Automation
                </Link>
              </li>
              <li>
                <Link href="/products/integration" className="text-sm text-gray-600 hover:text-[#008080]">
                  Integration
                </Link>
              </li>
              <li>
                <Link href="/products/security" className="text-sm text-gray-600 hover:text-[#008080]">
                  Security
                </Link>
              </li>
            </ul>
          </div>

          <div>
            <h3 className="text-lg font-medium">Support</h3>
            <ul className="mt-4 space-y-2">
              <li>
                <Link href="/contact" className="text-sm text-gray-600 hover:text-[#008080]">
                  Contact Us
                </Link>
              </li>
              <li>
                <Link href="/support" className="text-sm text-gray-600 hover:text-[#008080]">
                  Help Center
                </Link>
              </li>
              <li>
                <Link href="/documentation" className="text-sm text-gray-600 hover:text-[#008080]">
                  Documentation
                </Link>
              </li>
              <li>
                <Link href="/status" className="text-sm text-gray-600 hover:text-[#008080]">
                  System Status
                </Link>
              </li>
            </ul>
          </div>
        </div>

        <div className="mt-8 border-t pt-8">
          <div className="flex flex-col items-center justify-between gap-4 sm:flex-row">
            <p className="text-sm text-gray-600">© {new Date().getFullYear()} SariSmart. All rights reserved.</p>
            <div className="flex space-x-4">
              <button
                onClick={() => setShowPrivacyModal(true)}
                className="text-sm text-gray-600 hover:text-[#008080] hover:underline focus:outline-none"
              >
                Privacy Policy
              </button>
              <button
                onClick={() => setShowTermsModal(true)}
                className="text-sm text-gray-600 hover:text-[#008080] hover:underline focus:outline-none"
              >
                Terms of Service
              </button>
              <Link href="/cookies" className="text-sm text-gray-600 hover:text-[#008080]">
                Cookie Policy
              </Link>
            </div>
          </div>
        </div>
      </div>

      {/* Terms of Service Modal */}
      <TermsModal defaultOpen={showTermsModal} onOpenChange={setShowTermsModal} />

      {/* Privacy Policy Modal */}
      <PrivacyModal defaultOpen={showPrivacyModal} onOpenChange={setShowPrivacyModal} />
    </footer>
  )
}
