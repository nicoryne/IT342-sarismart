import type React from "react"
import "@/app/globals.css"
import { Inter } from "next/font/google"
import LayoutWrapper from "@/components/layout-wrapper"

// Add Leaflet CSS to the layout
import "leaflet/dist/leaflet.css"

const inter = Inter({ subsets: ["latin"] })

export const metadata = {
  title: "SariSmart - Smart Technology Solutions",
  description: "Empowering businesses with intelligent solutions that drive growth, efficiency, and innovation.",
  generator: "v0.dev",
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <LayoutWrapper>{children}</LayoutWrapper>
      </body>
    </html>
  )
}
