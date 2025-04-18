import type React from "react"

import InventorySidebar from "@/components/inventory/sidebar"
import InventoryHeader from "@/components/inventory/header"

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-screen bg-gray-50">
      {/* Sidebar */}
      <InventorySidebar />

      {/* Main Content */}
      <div className="flex flex-1 flex-col">
        {/* Top Navigation */}
        <InventoryHeader />

        {/* Page Content */}
        {children}
      </div>
    </div>
  )
}
