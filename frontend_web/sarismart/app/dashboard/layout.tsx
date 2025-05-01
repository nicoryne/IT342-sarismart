import type React from "react"
import Verification from "@/components/inventory/verification"
import DashboardClientWrapper from "./dashboard-client-wrapper"

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  return (
    <Verification>
      <DashboardClientWrapper>{children}</DashboardClientWrapper>
    </Verification>
  )
}
