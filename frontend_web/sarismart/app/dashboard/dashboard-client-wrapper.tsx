"use client"
import type React from "react"
import { StoresProvider } from "@/hooks/use-stores-context"
import InventorySidebar from "@/components/inventory/sidebar"
import InventoryHeader from "@/components/inventory/header"

export default function DashboardClientWrapper({ children }: { children: React.ReactNode }) {
  return (
    <StoresProvider>
      <div className="flex min-h-screen bg-gray-50">
        <InventorySidebar />
        <div className="flex flex-1 flex-col">
          <InventoryHeader />
          <div className="flex-1 overflow-auto">{children}</div>
        </div>
      </div>
    </StoresProvider>
  )
}
