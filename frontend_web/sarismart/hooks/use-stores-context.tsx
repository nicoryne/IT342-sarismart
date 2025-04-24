"use client"
import React, { createContext, useContext } from "react"
import { useStores } from "./use-stores"

const StoresContext = createContext<ReturnType<typeof useStores> | undefined>(undefined)

export function StoresProvider({ children }: { children: React.ReactNode }) {
  const stores = useStores()
  return (
    <StoresContext.Provider value={stores}>
      {children}
    </StoresContext.Provider>
  )
}

export function useStoresContext() {
  const ctx = useContext(StoresContext)
  if (!ctx) throw new Error("useStoresContext must be used within a StoresProvider")
  return ctx
}
