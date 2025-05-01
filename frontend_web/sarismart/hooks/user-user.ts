"use client"

import { useEffect, useState } from "react"

interface User {
  id: string
  username: string
  email: string
  firstName: string
  lastName: string
  role: string
  profileImage?: string
}

export function useUser() {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchUserData = async () => {
      setIsLoading(true)
      setError(null)

      try {
        const token = localStorage.getItem("token")
        if (!token) {
          throw new Error("No authentication token found")
        }

        const response = await fetch("https://sarismart-backend.onrender.com/api/v1/auth/user", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })

        if (!response.ok) {
          throw new Error(`Failed to fetch user data: ${response.status}`)
        }

        const userData = await response.json()
        setUser(userData)
      } catch (err) {
        console.error("Error fetching user data:", err)
        setError(err instanceof Error ? err.message : "Failed to fetch user data")
      } finally {
        setIsLoading(false)
      }
    }

    fetchUserData()
  }, [])

  return { user, isLoading, error }
}
