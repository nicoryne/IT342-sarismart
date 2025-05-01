"use client"

import { useRouter } from "next/navigation"
import { createClientComponentClient } from "@supabase/auth-helpers-nextjs"

import Link from "next/link"
import { Home, Settings } from "lucide-react"
import { usePathname } from "next/navigation"

export default function InventorySidebar() {
  const pathname = usePathname()

  const isActive = (path: string) => pathname === path

  const router = useRouter()
  const supabase = createClientComponentClient({
    supabaseUrl: process.env.NEXT_PUBLIC_SUPABASE_URL,
    supabaseKey: process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY,
  })

  const handleSignOut = async () => {
    const { error } = await supabase.auth.signOut()

    if (error) {
      console.error("Sign out error:", error.message)
    } else {
      localStorage.removeItem("token") // Remove the token from local storage
      router.push("/login")
    }
  }

  return (
    <div className="hidden w-64 flex-col bg-white shadow-sm md:flex">
      <div className="flex h-16 items-center border-b px-6">
        <span className="text-xl font-bold">
          <span className="text-[#008080]">Sari</span>
          <span>Smart</span>
        </span>
      </div>
      <div className="flex flex-1 flex-col py-4">
        <nav className="space-y-1 px-2">
          <Link
            href="/dashboard"
            className={`flex items-center rounded-md px-3 py-2 text-sm font-medium ${
              isActive("/dashboard") ? "bg-[#008080]/10 text-[#008080]" : "text-gray-600 hover:bg-gray-100"
            }`}
          >
            <Home className={`mr-3 h-5 w-5 ${isActive("/dashboard") ? "text-[#008080]" : "text-gray-500"}`} />
            Dashboard
          </Link>
          <Link
            href="/inventory/insights"
            className={`flex items-center rounded-md px-3 py-2 text-sm font-medium ${
              isActive("/inventory/insights") ? "bg-[#008080]/10 text-[#008080]" : "text-gray-600 hover:bg-gray-100"
            }`}
          >
            <svg
              className={`mr-3 h-5 w-5 ${isActive("/inventory/insights") ? "text-[#008080]" : "text-gray-500"}`}
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M2 22h20" />
              <path d="M10 3v19" />
              <path d="M18 10l-4-7-4 7" />
              <path d="M18 16v6" />
              <path d="M18 13v3" />
              <path d="M6 16v6" />
              <path d="M6 13v3" />
            </svg>
            Insights
          </Link>
          <Link
            href="/inventory/products"
            className={`flex items-center rounded-md px-3 py-2 text-sm font-medium ${
              isActive("/inventory/products") ? "bg-[#008080]/10 text-[#008080]" : "text-gray-600 hover:bg-gray-100"
            }`}
          >
            <svg
              className={`mr-3 h-5 w-5 ${isActive("/inventory/products") ? "text-[#008080]" : "text-gray-500"}`}
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
              <polyline points="3.29 7 12 12 20.71 7" />
              <line x1="12" y1="22" x2="12" y2="12" />
            </svg>
            Products
          </Link>
          <Link
            href="/inventory/history"
            className={`flex items-center rounded-md px-3 py-2 text-sm font-medium ${
              isActive("/inventory/history") ? "bg-[#008080]/10 text-[#008080]" : "text-gray-600 hover:bg-gray-100"
            }`}
          >
            <svg
              className={`mr-3 h-5 w-5 ${isActive("/inventory/history") ? "text-[#008080]" : "text-gray-500"}`}
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M12 8v4l3 3" />
              <circle cx="12" cy="12" r="10" />
            </svg>
            History
          </Link>
          <button
            onClick={handleSignOut}
            className="flex w-full items-center rounded-md px-3 py-2 text-sm font-medium text-gray-600 hover:bg-gray-100"
          >
            <svg
              className="mr-3 h-5 w-5 text-gray-500"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <polyline points="16 17 21 12 16 7" />
              <line x1="21" y1="12" x2="9" y2="12" />
            </svg>
            Sign Out
          </button>
        </nav>
      </div>
    </div>
  )
}
