"use client"

import type React from "react"

import { useState } from "react"
import Link from "next/link"
import { Eye, EyeOff, Mail, Loader2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Checkbox } from "@/components/ui/checkbox"
import { Separator } from "@/components/ui/separator"
import { useRouter } from "next/navigation"
import { TermsModal } from "@/components/terms-modal"
import { PrivacyModal } from "@/components/privacy-modal"

export default function RegisterPage() {
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const router = useRouter()
  const [formData, setFormData] = useState({
    email: "",
    fullName: "",
    phone: "",
    password: "",
    confirmPassword: "",
    agreeTerms: false,
  })
  const [passwordError, setPasswordError] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState("")
  const [showTermsModal, setShowTermsModal] = useState(false)
  const [showPrivacyModal, setShowPrivacyModal] = useState(false)

  // Add these new state variables after the existing state declarations
  const [passwordRequirements, setPasswordRequirements] = useState({
    length: false,
    lowercase: false,
    uppercase: false,
    special: false,
  })

  // Add this new state variable for phone error
  const [phoneError, setPhoneError] = useState("")

  // Function to show toast notifications
  const showToast = (message: string, type: "success" | "error") => {
    const toast = document.createElement("div")
    toast.className = `fixed top-4 right-4 px-4 py-2 rounded shadow-lg z-50 transition-opacity duration-300 ${
      type === "success" ? "bg-green-600 text-white" : "bg-red-600 text-white"
    }`
    toast.textContent = message
    document.body.appendChild(toast)

    // Fade out and remove
    setTimeout(() => {
      toast.style.opacity = "0"
      setTimeout(() => document.body.removeChild(toast), 500)
    }, 3000)
  }

  // Add this function after the existing handleChange function
  const validatePassword = (password: string) => {
    const requirements = {
      length: password.length >= 8,
      lowercase: /[a-z]/.test(password),
      uppercase: /[A-Z]/.test(password),
      special: /[!@#$%^&*(),.?":{}|<>]/.test(password),
    }

    setPasswordRequirements(requirements)
    return Object.values(requirements).every(Boolean)
  }

  // Modify the handleChange function to validate password when it changes
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))

    if (name === "password") {
      validatePassword(value)
    }

    if (name === "password" || name === "confirmPassword") {
      setPasswordError("")
    }

    if (name === "phone") {
      // Clear phone error when user types
      if (phoneError) setPhoneError("")
    }
  }

  // Modify the handleSubmit function to include validation before submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError("")

    // Validate passwords match
    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match")
      setIsLoading(false)
      return
    }

    // Validate password requirements
    if (!validatePassword(formData.password)) {
      setError("Password does not meet all requirements")
      setIsLoading(false)
      return
    }

    // Validate phone number (Philippine format)
    const phoneRegex = /^(09|\+639)\d{9}$/
    if (!phoneRegex.test(formData.phone)) {
      setPhoneError("Please enter a valid Philippine phone number (e.g., 09123456789 or +639123456789)")
      setIsLoading(false)
      return
    }

    try {
      const response = await fetch("https://sarismart-backend.onrender.com/api/v1/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: formData.email,
          password: formData.password,
          fullName: formData.fullName,
          phone: formData.phone,
        }),
      })

      if (response.ok) {
        const data = await response.json()
        console.log("Sign Up successful:", data)

        localStorage.setItem("token", data.access_token)

        // Show success toast
        showToast("Registration successful! Redirecting...", "success")

        // Redirect after a short delay to show the success message
        setTimeout(() => {
          router.push("/dashboard")
        }, 1000)
      } else {
        const errorData = await response.json()
        console.error("Sign Up error:", errorData)
        setError(errorData.message || "Registration failed")
        showToast("Registration failed: " + (errorData.message || "Please try again"), "error")
      }
    } catch (error) {
      console.error("Sign Up error:", error)
      setError("An unexpected error occurred. Please try again.")
      showToast("Connection error. Please check your internet and try again.", "error")
    } finally {
      setIsLoading(false)
    }
  }

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword)
  }

  const toggleConfirmPasswordVisibility = () => {
    setShowConfirmPassword(!showConfirmPassword)
  }

  const handleCheckboxChange = (checked: boolean) => {
    setFormData({ ...formData, agreeTerms: checked })
  }

  return (
    <div className="container flex flex-col items-center justify-center px-4 py-12 md:py-16 lg:py-20">
      <div className="mx-auto w-full max-w-md space-y-6">
        <div className="space-y-2 text-center">
          <h1 className="text-3xl font-bold">Create an Account</h1>
          <p className="text-gray-500">Enter your information to get started</p>
        </div>

        <div className="rounded-lg border bg-white p-6 shadow-sm">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                name="email"
                type="email"
                placeholder="name@example.com"
                required
                value={formData.email}
                onChange={handleChange}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="email">Full Name</Label>
              <Input
                id="fullName"
                name="fullName"
                type="text"
                placeholder="Juan De la Cruz"
                required
                value={formData.fullName}
                onChange={handleChange}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="email">Phone Number</Label>
              <Input
                id="phone"
                name="phone"
                type="tel"
                placeholder="09123456789"
                required
                value={formData.phone}
                onChange={handleChange}
              />
              {phoneError && <p className="text-sm text-red-500">{phoneError}</p>}
              <p className="text-xs text-gray-500">Format: 09123456789 or +639123456789</p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="password">Password</Label>
              <div className="relative">
                <Input
                  id="password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="••••••••"
                  required
                  value={formData.password}
                  onChange={handleChange}
                />
                <button
                  type="button"
                  onClick={togglePasswordVisibility}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                >
                  {showPassword ? (
                    <EyeOff className="h-4 w-4" aria-hidden="true" />
                  ) : (
                    <Eye className="h-4 w-4" aria-hidden="true" />
                  )}
                  <span className="sr-only">{showPassword ? "Hide password" : "Show password"}</span>
                </button>
              </div>

              {/* Password requirements checklist */}
              <div className="mt-2 space-y-1 text-xs">
                <p className="font-medium">Password must contain:</p>
                <div className="grid grid-cols-2 gap-1">
                  <div
                    className={`flex items-center ${passwordRequirements.length ? "text-green-600" : "text-gray-500"}`}
                  >
                    <div
                      className={`mr-1 h-3 w-3 rounded-full ${passwordRequirements.length ? "bg-green-600" : "bg-gray-300"}`}
                    ></div>
                    At least 8 characters
                  </div>
                  <div
                    className={`flex items-center ${passwordRequirements.lowercase ? "text-green-600" : "text-gray-500"}`}
                  >
                    <div
                      className={`mr-1 h-3 w-3 rounded-full ${passwordRequirements.lowercase ? "bg-green-600" : "bg-gray-300"}`}
                    ></div>
                    Lowercase letter
                  </div>
                  <div
                    className={`flex items-center ${passwordRequirements.uppercase ? "text-green-600" : "text-gray-500"}`}
                  >
                    <div
                      className={`mr-1 h-3 w-3 rounded-full ${passwordRequirements.uppercase ? "bg-green-600" : "bg-gray-300"}`}
                    ></div>
                    Uppercase letter
                  </div>
                  <div
                    className={`flex items-center ${passwordRequirements.special ? "text-green-600" : "text-gray-500"}`}
                  >
                    <div
                      className={`mr-1 h-3 w-3 rounded-full ${passwordRequirements.special ? "bg-green-600" : "bg-gray-300"}`}
                    ></div>
                    Special character
                  </div>
                </div>
              </div>
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirm Password</Label>
              <div className="relative">
                <Input
                  id="confirmPassword"
                  name="confirmPassword"
                  type={showConfirmPassword ? "text" : "password"}
                  placeholder="••••••••"
                  required
                  value={formData.confirmPassword}
                  onChange={handleChange}
                />
                <button
                  type="button"
                  onClick={toggleConfirmPasswordVisibility}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                >
                  {showConfirmPassword ? (
                    <EyeOff className="h-4 w-4" aria-hidden="true" />
                  ) : (
                    <Eye className="h-4 w-4" aria-hidden="true" />
                  )}
                  <span className="sr-only">{showConfirmPassword ? "Hide password" : "Show password"}</span>
                </button>
              </div>
              {passwordError && <p className="text-sm text-red-500">{passwordError}</p>}
            </div>
            {error && <div className="rounded-md bg-red-50 p-3 text-sm text-red-600">{error}</div>}

            <div className="flex items-center space-x-2">
              <Checkbox id="terms" required checked={formData.agreeTerms} onCheckedChange={handleCheckboxChange} />
              <Label htmlFor="terms" className="text-sm font-normal">
                I agree to the{" "}
                <button
                  type="button"
                  onClick={() => setShowTermsModal(true)}
                  className="text-[#008080] font-medium hover:underline focus:outline-none"
                >
                  Terms of Service
                </button>{" "}
                and{" "}
                <button
                  type="button"
                  onClick={() => setShowPrivacyModal(true)}
                  className="text-[#008080] font-medium hover:underline focus:outline-none"
                >
                  Privacy Policy
                </button>
              </Label>
            </div>

            <Button type="submit" className="w-full bg-[#008080] text-white hover:bg-[#005F6B]" disabled={isLoading}>
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Creating Account...
                </>
              ) : (
                "Create Account"
              )}
            </Button>
          </form>

          <div className="mt-6">
            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <Separator className="w-full" />
              </div>
              <div className="relative flex justify-center text-xs uppercase">
                <span className="bg-white px-2 text-gray-500">Or continue with</span>
              </div>
            </div>

            <div className="mt-6 grid grid-cols-2 gap-3">
              <Button variant="outline" className="col-span-2 w-full">
                <Mail className="mr-2 h-4 w-4" />
                Email
              </Button>
            </div>
          </div>
        </div>

        <div className="text-center text-sm">
          Already have an account?{" "}
          <Link href="/login" className="font-medium text-[#008080] hover:underline">
            Sign in
          </Link>
        </div>
      </div>

      {/* Terms of Service Modal */}
      <TermsModal defaultOpen={showTermsModal} onOpenChange={setShowTermsModal} />

      {/* Privacy Policy Modal */}
      <PrivacyModal defaultOpen={showPrivacyModal} onOpenChange={setShowPrivacyModal} />
    </div>
  )
}
