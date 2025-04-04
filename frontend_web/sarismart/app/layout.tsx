import type React from "react"
import "@/app/globals.css"
import { Inter } from "next/font/google"
import  ThemeProvider  from "@/components/theme-provider"

const inter = Inter({ subsets: ["latin"] })

export const metadata = {
  title: "SariSmart - Smart Technology Solutions",
  description: "Empowering businesses with intelligent solutions that drive growth, efficiency, and innovation.",
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <ThemeProvider attribute="class" defaultTheme="light">
          {children}
        </ThemeProvider>
      </body>
    </html>
  )
}

