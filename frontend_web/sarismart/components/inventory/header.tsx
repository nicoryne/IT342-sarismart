"use client"

import { useState } from "react"
import Image from "next/image"
import { ChevronDown, Menu, Store } from "lucide-react"

import { Button } from "@/components/ui/button"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { useStores } from "@/hooks/use-stores"

export default function InventoryHeader() {
  const [selectedStore, setSelectedStore] = useState("all")
  const { stores } = useStores()

  return (
    <header className="relative flex h-16 items-center justify-between border-b bg-white px-4 md:px-6">
      {/* Left Section */}
      <div className="flex items-center md:hidden">
        <Button variant="ghost" size="icon" className="mr-2">
          <Menu className="h-6 w-6" />
          <span className="sr-only">Toggle menu</span>
        </Button>
      </div>

      {/* Center Section - Store Selector */}
      <div className="absolute left-1/2 transform -translate-x-1/2">
        <div className="flex items-center">
          <Store className="mr-2 h-5 w-5 text-[#008080]" />
          <Select
            value={selectedStore}
            onValueChange={(value) => {
              setSelectedStore(value)
              // Visual feedback (basic toast)
              const toast = document.createElement("div")
              toast.className =
                "fixed top-4 right-4 bg-[#008080] text-white px-4 py-2 rounded shadow-lg z-50"
              toast.textContent = `Switched to ${
                value === "all" ? "All Stores" : stores.find((s) => s.id === value)?.name
              }`
              document.body.appendChild(toast)
              setTimeout(() => {
                toast.style.opacity = "0"
                toast.style.transition = "opacity 0.5s ease"
                setTimeout(() => document.body.removeChild(toast), 500)
              }, 2000)
            }}
          >
            <SelectTrigger className="w-[180px] border-none">
              <SelectValue placeholder="Select store" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Stores</SelectItem>
              {stores.map((store) => (
                <SelectItem key={store.id} value={store.id}>
                  {store.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
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
    </header>
  )
}
