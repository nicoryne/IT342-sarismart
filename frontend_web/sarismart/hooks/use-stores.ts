// Add detailed comments to explain the store management implementation

"use client"

import React, { useState } from "react"

// Define the Store interface to match backend and map to frontend
interface Store {
  id: string | number
  name: string // mapped from storeName
  location: string
  latitude?: number
  longitude?: number
  totalProducts?: number
  lowStock?: number
  inventoryValue?: number
  // ...other fields if needed
}

// Set up initial store data
const fetchStores = async () => {
  // Fetch the owner ID from local storage
  const token = localStorage.getItem("token")
  let ownerId = null

  if (token) {
    try {
      const base64Url = token.split(".")[1]
      const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/")
      const jsonPayload = decodeURIComponent(
        window
          .atob(base64)
          .split("")
          .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
          .join(""),
      )

      const payload = JSON.parse(jsonPayload)
      ownerId = payload.sub
    } catch (error) {
      console.error("Error decoding or parsing the token:", error)
    }
  } else {
    console.log("No token found in local storage.")
  }

  if (!ownerId) throw new Error("No ownerId found in token")

  const response = await fetch(`https://sarismart-backend.onrender.com/api/v1/stores/owner/${ownerId}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })

  if (!response.ok) {
    throw new Error("Failed to fetch stores")
  }
  // Map backend fields to frontend Store interface
  const stores = await response.json()
  return stores.map((store: any) => ({
    id: store.id,
    name: store.storeName, // map storeName to name
    location: store.location,
    latitude: store.latitude,
    longitude: store.longitude,
    totalProducts: store.totalProducts,
    lowStock: store.lowStock,
    inventoryValue: store.inventoryValue,
    // ...add more mappings if needed
  }))
}

export function useStores() {
  // Create state to manage the stores data
  const [stores, setStores] = useState<Store[]>([])
  const [selectedStore, setSelectedStore] = useState<string>("all") // Ensure selectedStore is a string

  React.useEffect(() => {
    fetchStores().then(setStores).catch(console.error)
  }, [])

  // Helper to decode JWT and get user info
  function getUserFromToken(token: string | null) {
    if (!token) return null
    try {
      const base64Url = token.split(".")[1]
      const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/")
      const jsonPayload = decodeURIComponent(
        window
          .atob(base64)
          .split("")
          .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
          .join(""),
      )
      return JSON.parse(jsonPayload)
    } catch {
      return null
    }
  }

  // Function to add a new store using backend API
  const addStore = async (newStore: { name: string; location: string }) => {
    const token = localStorage.getItem("token")
    const user = getUserFromToken(token)

    if (!user) {
      console.error("No user info found in token")
      return
    }

    // Get device location
    const getPosition = (): Promise<GeolocationPosition> =>
      new Promise((resolve, reject) => navigator.geolocation.getCurrentPosition(resolve, reject))

    let latitude = 0
    let longitude = 0
    try {
      const position = await getPosition()
      latitude = position.coords.latitude
      longitude = position.coords.longitude
    } catch (error) {
      console.warn("Could not get device location, using 0,0")
    }

    // Construct the request body according to your backend's Store entity
    const body = {
      id: 0,
      storeName: newStore.name, // backend expects storeName
      location: newStore.location,
      latitude,
      longitude,
      ownerId: user.sub,
    }

    try {
      const response = await fetch("https://sarismart-backend.onrender.com/api/v1/stores", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(body),
      })

      if (!response.ok) {
        throw new Error("Failed to add store")
      }

      const createdStore = await response.json()
      // Map backend response to frontend Store interface
      setStores((prev) => [
        ...prev,
        {
          id: createdStore.id,
          name: createdStore.storeName,
          location: createdStore.location,
          latitude: createdStore.latitude,
          longitude: createdStore.longitude,
        },
      ])
    } catch (error) {
      console.error(error)
    }
  }

  // Function to delete a store
  const deleteStore = async (storeId: string | number) => {
    const token = localStorage.getItem("token")
    if (!token) {
      console.error("No token found")
      return
    }

    try {
      const response = await fetch(`https://sarismart-backend.onrender.com/api/v1/stores/${storeId}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })

      if (!response.ok) {
        throw new Error("Failed to delete store")
      }

      setStores((prev) => prev.filter((store) => store.id !== storeId))
    } catch (error) {
      console.error(error)
    }
  }

  // Function to update a store
  const updateStore = async (storeId: string | number, updatedData: { name: string; location: string }) => {
    const token = localStorage.getItem("token")
    if (!token) {
      console.error("No token found")
      return
    }

    try {
      const response = await fetch(`https://sarismart-backend.onrender.com/api/v1/stores/${storeId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          storeName: updatedData.name,
          location: updatedData.location,
        }),
      })

      if (!response.ok) {
        throw new Error("Failed to update store")
      }

      const updatedStore = await response.json()
      setStores((prev) =>
        prev.map((store) =>
          store.id === storeId ? { ...store, name: updatedStore.storeName, location: updatedStore.location } : store,
        ),
      )
    } catch (error) {
      console.error(error)
    }
  }

  // Function to filter products by store
  const filterProductsByStore = (storeId: string) => {
    if (storeId === "all") {
      // Aggregate data for all stores
      return {
        totalProducts: stores.reduce((sum, store) => sum + (store.totalProducts || 0), 0),
        lowStock: stores.reduce((sum, store) => sum + (store.lowStock || 0), 0),
        inventoryValue: stores.reduce((sum, store) => sum + (store.inventoryValue || 0), 0),
      }
    }
    // Find data for the specific store
    const store = stores.find((s) => String(s.id) === storeId) // Ensure comparison is consistent
    return store
      ? {
          totalProducts: store.totalProducts || 0,
          lowStock: store.lowStock || 0,
          inventoryValue: store.inventoryValue || 0,
        }
      : { totalProducts: 0, lowStock: 0, inventoryValue: 0 }
  }

  // Function to filter transactions by store
  const filterTransactionsByStore = (storeId: string, allTransactions: any[]) => {
    // In a real implementation, this would fetch actual transaction data filtered by store
    // Example: const response = await fetch(`/api/stores/${storeId}/transactions`);

    // Return empty array as placeholder - would be replaced with actual data in production
    return []
  }

  // Function to filter insights by store
  const filterInsightsByStore = (storeId: string) => {
    // In a real implementation, this would fetch actual insights data for the store
    // Example: const response = await fetch(`/api/stores/${storeId}/insights`);

    return {
      // These values would come from your backend in production
      revenueGrowth: "0%",
      turnover: "0x",
      topCategory: "None",
    }
  }

  // Return all the functions and data needed by components
  return {
    stores,
    selectedStore,
    setSelectedStore, // Expose setter for selectedStore
    addStore,
    deleteStore, // Expose deleteStore
    updateStore, // Expose updateStore
    filterProductsByStore,
    filterTransactionsByStore,
    filterInsightsByStore,
  }
}
