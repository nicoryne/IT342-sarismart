"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { Button } from "@/components/ui/button"

export default function Navbar() {
  const pathname = usePathname()

  // 👇 hide Navbar if we're inside the dashboard section
  const isDashboard = pathname.startsWith("/dashboard")
  if (isDashboard) return null

  const isActive = (path: string) => pathname === path

  return (
    <header className="sticky top-0 z-50 border-b bg-white/80 backdrop-blur-md">
      <div className="container flex h-16 items-center justify-between px-4 md:px-6">
        <div className="flex items-center gap-8">
          <Link href="/" className="flex items-center gap-2">
            <span className="text-xl font-bold sm:text-2xl">
              <span className="text-[#008080]">Sari</span>
              <span>Smart</span>
            </span>
          </Link>

          {/* Navigation Links */}
          <nav className="flex gap-6">
            <Link
              href="/"
              className={`text-sm font-medium transition-colors hover:text-[#008080] ${
                isActive("/") ? "text-[#008080]" : "text-gray-600"
              }`}
            >
              Home
            </Link>
            <Link
              href="/about"
              className={`text-sm font-medium transition-colors hover:text-[#008080] ${
                isActive("/about") ? "text-[#008080]" : "text-gray-600"
              }`}
            >
              About Us
            </Link>
            <Link
              href="/contact"
              className={`text-sm font-medium transition-colors hover:text-[#008080] ${
                isActive("/contact") ? "text-[#008080]" : "text-gray-600"
              }`}
            >
              Contact Us
            </Link>
          </nav>
        </div>

        {/* Auth Buttons */}
        <div className="flex items-center gap-4">
          <Link href="/register" className="text-sm font-medium text-gray-600 transition-colors hover:text-[#008080]">
            Register
          </Link>
          <Button asChild size="sm" className="bg-[#008080] text-white hover:bg-[#005F6B]">
            <Link href="/login">Log In</Link>
          </Button>
        </div>
      </div>
    </header>
  )
}
