"use client"
import Image from "next/image"
import { ChevronDown, Menu } from "lucide-react"

import { Button } from "@/components/ui/button"

export default function InventoryHeader() {
  // Remove all the store-related state and functions

  return (
    <header className="relative flex h-16 items-center justify-between border-b bg-white px-4 md:px-6">
      {/* Left Section */}
      <div className="flex items-center md:hidden">
        <Button variant="ghost" size="icon" className="mr-2">
          <Menu className="h-6 w-6" />
          <span className="sr-only">Toggle menu</span>
        </Button>
      </div>

      {/* Center Section - Remove the store selector */}
      <div className="absolute left-1/2 transform -translate-x-1/2">
        <h1 className="text-lg font-semibold text-[#008080]">SariSmart</h1>
      </div>

      {/* Right Section - Profile */}
      <div className="flex items-center gap-4 ml-auto">
        <div className="flex items-center gap-2">
          <div className="relative h-8 w-8 overflow-hidden rounded-full">
            <Image
              src="/placeholder.svg?height=32&width=32"
              alt="User"
              width={32}
              height={32}
              className="object-cover"
            />
          </div>
          <div className="hidden md:block">
            <div className="text-sm font-medium">Rahimah</div>
            <div className="text-xs text-gray-500">Admin</div>
          </div>
          <ChevronDown className="h-4 w-4 text-gray-500" />
        </div>
      </div>

      {/* Remove all the modals related to store management */}
    </header>
  )
}
