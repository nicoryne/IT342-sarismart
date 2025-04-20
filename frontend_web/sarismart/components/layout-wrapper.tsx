"use client"

import type React from "react"

import { usePathname } from "next/navigation"
import { useEffect, useState } from "react"
import Navbar from "@/components/navbar"
import Footer from "@/components/footer"

export default function LayoutWrapper({ children }: { children: React.ReactNode }) {
  const pathname = usePathname()
  // Use state to avoid hydration mismatch
  const [mounted, setMounted] = useState(false)

  useEffect(() => {
    setMounted(true)
  }, [])

  // Only render conditionally after mounting on client
  if (!mounted) {
    return (
      <div className="flex min-h-screen flex-col">
        <main className="flex-1">{children}</main>
      </div>
    )
  }

  // Don't show navbar on dashboard or inventory pages
  const showNavbar = !pathname.includes("/dashboard") && !pathname.includes("/inventory")
  // Don't show footer on dashboard or inventory pages either
  const showFooter = showNavbar

  return (
    <div className="flex min-h-screen flex-col">
      {showNavbar && <Navbar />}
      <main className="flex-1">{children}</main>
      {showFooter && <Footer />}
    </div>
  )
}
