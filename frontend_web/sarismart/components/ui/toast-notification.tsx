"use client"

import { useEffect } from "react"

type ToastProps = {
  message: string
  type: "success" | "error" | "info"
  duration?: number
  onClose: () => void
}

export function Toast({ message, type, duration = 3000, onClose }: ToastProps) {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose()
    }, duration)

    return () => clearTimeout(timer)
  }, [duration, onClose])

  const bgColor = type === "success" ? "bg-green-600" : type === "error" ? "bg-red-600" : "bg-blue-600"

  return (
    <div
      className={`fixed top-4 right-4 px-4 py-2 rounded shadow-lg z-50 text-white ${bgColor} animate-fade-in-down`}
      role="alert"
    >
      {message}
    </div>
  )
}

export function showToast(message: string, type: "success" | "error" | "info" = "info") {
  const toast = document.createElement("div")
  toast.className = `fixed top-4 right-4 px-4 py-2 rounded shadow-lg z-50 transition-opacity duration-300 ${
    type === "success"
      ? "bg-green-600 text-white"
      : type === "error"
        ? "bg-red-600 text-white"
        : "bg-blue-600 text-white"
  }`
  toast.textContent = message
  document.body.appendChild(toast)

  // Fade out and remove
  setTimeout(() => {
    toast.style.opacity = "0"
    setTimeout(() => document.body.removeChild(toast), 500)
  }, 3000)
}
