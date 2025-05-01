"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { useRouter, usePathname } from "next/navigation"
import { createClientComponentClient } from "@supabase/auth-helpers-nextjs"

export default function Verification({ children }: { children: React.ReactNode }) {
  const [isChecking, setIsChecking] = useState(true)
  const router = useRouter()
  const pathname = usePathname()

  const supabase = createClientComponentClient({
    supabaseUrl: process.env.NEXT_PUBLIC_SUPABASE_URL,
    supabaseKey: process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY,
  })

  useEffect(() => {
    const checkSession = async () => {
      const token = localStorage.getItem("token")
      if (!token) {
        router.replace("/login")
        return
      }

      await supabase.auth.setSession({
        access_token: token,
        refresh_token: token,
      })

      const {
        data: { session },
        error,
      } = await supabase.auth.getSession()

      if (!session || error) {
        router.replace("/login")
      } else {
        if (pathname === "/verification") {
          router.replace("/dashboard")
        }
      }

      setIsChecking(false)
    }

    checkSession()
  }, [router, supabase, pathname])

  if (isChecking) {
    return (
      <div className="flex h-screen w-full items-center justify-center bg-gray-50">
        <div className="flex flex-col items-center space-y-4">
          <div className="h-12 w-12 animate-spin rounded-full border-4 border-[#008080] border-t-transparent"></div>
          <p className="text-lg font-medium text-gray-600">Verifying your session...</p>
        </div>
      </div>
    )
  }

  return <>{children}</>
}
