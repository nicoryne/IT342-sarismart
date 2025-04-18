"use client"

import { useState } from "react"
import Image from "next/image"
import { Bell, ChevronDown, Menu, MessageSquare, Search, Store } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useStores } from "@/hooks/use-stores"

export default function InventoryHeader() {
  const [selectedStore, setSelectedStore] = useState("all")
  const { stores } = useStores()

  return (
    <header className="flex h-16 items-center justify-between border-b bg-white px-4 md:px-6">
      <div className="flex items-center md:hidden">
        <Button variant="ghost" size="icon" className="mr-2">
          <Menu className="h-6 w-6" />
          <span className="sr-only">Toggle menu</span>
        </Button>
      </div>

      <div className="flex items-center gap-4">
        <div className="relative w-full max-w-md">
          <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-gray-500" />
          <Input type="search" placeholder="Search..." className="w-full pl-8" />
        </div>

        {/* Store Selector */}
        <div className="flex items-center">
          <Store className="mr-2 h-5 w-5 text-[#008080]" />
          <Select
            value={selectedStore}
            onValueChange={(value) => {
              setSelectedStore(value)
              // Add a visual feedback when store changes
              const toast = document.createElement("div")
              toast.className = "fixed top-4 right-4 bg-[#008080] text-white px-4 py-2 rounded shadow-lg z-50"
              toast.textContent = `Switched to ${value === "all" ? "All Stores" : stores.find((s) => s.id === value)?.name}`
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

      <div className="flex items-center gap-4">
        <Button variant="ghost" size="icon" className="relative">
          <MessageSquare className="h-5 w-5" />
          <span className="sr-only">Messages</span>
          <span className="absolute right-1 top-1 h-2 w-2 rounded-full bg-[#008080]"></span>
        </Button>
        <Button variant="ghost" size="icon" className="relative">
          <Bell className="h-5 w-5" />
          <span className="sr-only">Notifications</span>
          <span className="absolute right-1 top-1 h-2 w-2 rounded-full bg-[#008080]"></span>
        </Button>
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
