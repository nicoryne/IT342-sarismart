import type React from "react"

import InventorySidebar from "@/components/inventory/sidebar"
import InventoryHeader from "@/components/inventory/header"
import Verification from "@/components/inventory/verification"

export default function InventoryLayout({ children }: { children: React.ReactNode }) {
  return (
    <Verification>
      <div className="flex min-h-screen bg-gray-50">
        {/* Sidebar */}
        <InventorySidebar />

        {/* Main Content */}
        <div className="flex flex-1 flex-col">
          {/* Top Navigation */}
          <InventoryHeader />

          {/* Page Content */}
          <div className="flex-1 overflow-auto">{children}</div>
        </div>
      </div>
    </Verification>
  )
}
