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

// Update the fetchStores function to properly fetch and map store data
const fetchStores = async () => {
  // Fetch the owner ID from local storage
  const token = localStorage.getItem("token")
  if (!token) {
    console.error("No token found in local storage")
    return []
  }

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
    const ownerId = payload.sub

    if (!ownerId) {
      console.error("No ownerId found in token")
      return []
    }

    const response = await fetch(`https://sarismart-backend.onrender.com/api/v1/stores/owner/${ownerId}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })

    if (!response.ok) {
      throw new Error(`Failed to fetch stores: ${response.status}`)
    }

    // Map backend fields to frontend Store interface
    const stores = await response.json()
    return stores.map((store: any) => ({
      id: store.id,
      name: store.storeName, // map storeName to name
      location: store.location,
      latitude: store.latitude,
      longitude: store.longitude,
    }))
  } catch (error) {
    console.error("Error fetching stores:", error)
    return []
  }
}

// Update the useStores hook to include real data fetching for products, transactions, and insights
export function useStores() {
  // Create state to manage the stores data
  const [stores, setStores] = useState<Store[]>([])
  const [selectedStore, setSelectedStore] = useState<string>("all") // Ensure selectedStore is a string
  const [isLoading, setIsLoading] = useState(false)
  const [storeMetrics, setStoreMetrics] = useState<
    Record<string, { totalProducts: number; lowStock: number; inventoryValue: number }>
  >({})

  // Fetch stores on component mount
  React.useEffect(() => {
    const getStores = async () => {
      setIsLoading(true)
      try {
        const storesData = await fetchStores()
        setStores(storesData)

        // For each store, fetch metrics
        const metricsPromises = storesData.map(async (store: Store) => {
          const metrics = await fetchStoreMetrics(String(store.id))
          return { storeId: String(store.id), metrics }
        })

        const allMetrics = await Promise.all(metricsPromises)
        const metricsMap: Record<string, { totalProducts: number; lowStock: number; inventoryValue: number }> = {}

        allMetrics.forEach(({ storeId, metrics }) => {
          metricsMap[storeId] = metrics
        })

        setStoreMetrics(metricsMap)
      } catch (error) {
        console.error("Error fetching stores:", error)
      } finally {
        setIsLoading(false)
      }
    }

    getStores()
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

  // Function to fetch store metrics (products, low stock, inventory value)
  const fetchStoreMetrics = async (storeId: string) => {
    const token = localStorage.getItem("token")
    if (!token) {
      return { totalProducts: 0, lowStock: 0, inventoryValue: 0 }
    }

    try {
      // Fetch products for the store
      const response = await fetch(`https://sarismart-backend.onrender.com/api/v1/stores/${storeId}/products`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })

      if (!response.ok) {
        throw new Error(`Failed to fetch products: ${response.status}`)
      }

      const products = await response.json()

      // Calculate metrics
      const totalProducts = products.length
      const lowStock = products.filter(
        (product: any) => product.stock <= product.reorder_level && product.stock > 0,
      ).length
      const inventoryValue = products.reduce(
        (sum: number, product: any) => sum + Number(product.price) * Number(product.stock),
        0,
      )

      return { totalProducts, lowStock, inventoryValue }
    } catch (error) {
      console.error(`Error fetching metrics for store ${storeId}:`, error)
      return { totalProducts: 0, lowStock: 0, inventoryValue: 0 }
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
        throw new Error(`Failed to add store: ${response.status}`)
      }

      const createdStore = await response.json()
      // Map backend response to frontend Store interface
      const newStoreData = {
        id: createdStore.id,
        name: createdStore.storeName,
        location: createdStore.location,
        latitude: createdStore.latitude,
        longitude: createdStore.longitude,
      }

      setStores((prev) => [...prev, newStoreData])

      // Initialize metrics for the new store
      setStoreMetrics((prev) => ({
        ...prev,
        [String(createdStore.id)]: { totalProducts: 0, lowStock: 0, inventoryValue: 0 },
      }))

      return newStoreData
    } catch (error) {
      console.error("Error adding store:", error)
      throw error
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
        throw new Error(`Failed to delete store: ${response.status}`)
      }

      setStores((prev) => prev.filter((store) => store.id !== storeId))

      // Remove store metrics
      setStoreMetrics((prev) => {
        const newMetrics = { ...prev }
        delete newMetrics[String(storeId)]
        return newMetrics
      })

      // If the deleted store was selected, switch to "all"
      if (selectedStore === String(storeId)) {
        setSelectedStore("all")
      }
    } catch (error) {
      console.error("Error deleting store:", error)
      throw error
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
        throw new Error(`Failed to update store: ${response.status}`)
      }

      const updatedStore = await response.json()
      setStores((prev) =>
        prev.map((store) =>
          store.id === storeId ? { ...store, name: updatedStore.storeName, location: updatedStore.location } : store,
        ),
      )
    } catch (error) {
      console.error("Error updating store:", error)
      throw error
    }
  }

  // Function to filter products by store
  const filterProductsByStore = (storeId: string) => {
    if (storeId === "all") {
      // Aggregate data for all stores
      return {
        totalProducts: Object.values(storeMetrics).reduce((sum, metrics) => sum + metrics.totalProducts, 0),
        lowStock: Object.values(storeMetrics).reduce((sum, metrics) => sum + metrics.lowStock, 0),
        inventoryValue: Object.values(storeMetrics).reduce((sum, metrics) => sum + metrics.inventoryValue, 0),
      }
    }

    // Return metrics for the specific store
    return storeMetrics[storeId] || { totalProducts: 0, lowStock: 0, inventoryValue: 0 }
  }

  // Function to fetch transactions for a store
  const fetchTransactions = async (storeId: string) => {
    const token = localStorage.getItem("token")
    if (!token) {
      console.error("No token found")
      return []
    }

    try {
      const response = await fetch(
        `https://sarismart-backend.onrender.com/api/v1/stores/${storeId}/transactions/sales`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      )

      if (!response.ok) {
        throw new Error(`Failed to fetch transactions: ${response.status}`)
      }

      const data = await response.json()

      // If data is an array, return it; otherwise, return an empty array
      return Array.isArray(data) ? data : []
    } catch (error) {
      console.error(`Error fetching transactions for store ${storeId}:`, error)
      return []
    }
  }

  // Function to filter transactions by store
  const filterTransactionsByStore = async (storeId: string) => {
    // First, ensure we only work with the user's own stores
    if (stores.length === 0) {
      console.warn("No stores available to fetch transactions")
      return []
    }

    if (storeId === "all") {
      // Fetch transactions for all user's stores
      const allTransactions = []
      for (const store of stores) {
        try {
          const storeTransactions = await fetchTransactions(String(store.id))
          // Process each transaction to ensure it has the expected format
          const processedTransactions = storeTransactions.map((tx) => ({
            id: tx.id?.toString() || "N/A",
            product: typeof tx.product === "string" ? tx.product : "N/A",
            date: tx.date || tx.createdAt || new Date().toISOString(),
            store: store.name || "N/A",
            store_id: store.id,
            amount: typeof tx.amount === "number" ? tx.amount : 0,
            status: typeof tx.status === "string" ? tx.status : "Completed",
          }))
          allTransactions.push(...processedTransactions)
        } catch (error) {
          console.error(`Error fetching transactions for store ${store.id}:`, error)
        }
      }
      return allTransactions
    }

    // Verify the selected store belongs to the user
    const storeExists = stores.some((store) => String(store.id) === storeId)
    if (!storeExists) {
      console.warn(`Store ${storeId} does not belong to the current user`)
      return []
    }

    // Fetch transactions for the specific store
    try {
      const transactions = await fetchTransactions(storeId)
      const storeName = stores.find((s) => String(s.id) === storeId)?.name || "Unknown Store"

      // Process transactions to ensure they have the expected format
      return transactions.map((tx) => ({
        id: tx.id?.toString() || "N/A",
        product: typeof tx.product === "string" ? tx.product : "N/A",
        date: tx.date || tx.createdAt || new Date().toISOString(),
        store: storeName,
        store_id: storeId,
        amount: typeof tx.amount === "number" ? tx.amount : 0,
        status: typeof tx.status === "string" ? tx.status : "Completed",
      }))
    } catch (error) {
      console.error(`Error fetching transactions for store ${storeId}:`, error)
      return []
    }
  }

  // Function to fetch insights for a store
  const fetchStoreInsights = async (storeId: string) => {
    const token = localStorage.getItem("token")
    if (!token) {
      console.error("No token found")
      return { revenueGrowth: "0%", turnover: "0x", topCategory: "None" }
    }

    try {
      // Fetch monthly report for revenue growth
      const monthlyResponse = await fetch(
        `https://sarismart-backend.onrender.com/api/v1/stores/${storeId}/reports/monthly`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      )

      // Fetch inventory report for turnover and top category
      const inventoryResponse = await fetch(
        `https://sarismart-backend.onrender.com/api/v1/stores/${storeId}/reports/inventory`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      )

      if (!monthlyResponse.ok || !inventoryResponse.ok) {
        throw new Error("Failed to fetch insights")
      }

      const monthlyData = await monthlyResponse.json()
      const inventoryData = await inventoryResponse.json()

      // Process the data to extract insights
      // This is a simplified example - adjust based on actual API response structure
      const revenueGrowth = monthlyData.growth || "0%"
      const turnover = inventoryData.turnover || "0x"
      const topCategory = inventoryData.topCategory || "None"

      return { revenueGrowth, turnover, topCategory }
    } catch (error) {
      console.error(`Error fetching insights for store ${storeId}:`, error)
      return { revenueGrowth: "0%", turnover: "0x", topCategory: "None" }
    }
  }

  // Function to filter insights by store
  const filterInsightsByStore = async (storeId: string) => {
    // First, ensure we only work with the user's own stores
    if (stores.length === 0) {
      console.warn("No stores available to fetch insights")
      return { revenueGrowth: "0%", turnover: "0x", topCategory: "None" }
    }

    if (storeId === "all") {
      // For "all" stores, we could aggregate insights from all user's stores
      // This is a simplified implementation - in a real app, you might want to calculate
      // weighted averages or more sophisticated aggregations
      let totalRevenue = 0
      let totalTurnover = 0
      const categoryCount: Record<string, number> = {} // Explicitly type categoryCount

      for (const store of stores) {
        try {
          const storeInsights = await fetchStoreInsights(String(store.id))
          // Parse numeric values from string representations
          const revenueValue = Number.parseFloat(storeInsights.revenueGrowth) || 0
          const turnoverValue = Number.parseFloat(storeInsights.turnover) || 0

          totalRevenue += revenueValue
          totalTurnover += turnoverValue

          // Count category occurrences
          if (storeInsights.topCategory && storeInsights.topCategory !== "None") {
            categoryCount[storeInsights.topCategory] = (categoryCount[storeInsights.topCategory] || 0) + 1
          }
        } catch (error) {
          console.error(`Error fetching insights for store ${store.id}:`, error)
        }
      }

      // Calculate averages
      const avgRevenue = stores.length > 0 ? totalRevenue / stores.length : 0
      const avgTurnover = stores.length > 0 ? totalTurnover / stores.length : 0

      // Find the most common top category
      let topCategory = "None"
      let maxCount = 0
      for (const [category, count] of Object.entries(categoryCount)) {
        if (count > maxCount) {
          maxCount = count
          topCategory = category
        }
      }

      return {
        revenueGrowth: `${avgRevenue.toFixed(1)}%`,
        turnover: `${avgTurnover.toFixed(1)}x`,
        topCategory,
      }
    }

    // Verify the selected store belongs to the user
    const storeExists = stores.some((store) => String(store.id) === storeId)
    if (!storeExists) {
      console.warn(`Store ${storeId} does not belong to the current user`)
      return { revenueGrowth: "0%", turnover: "0x", topCategory: "None" }
    }

    // Fetch insights for the specific store
    return await fetchStoreInsights(storeId)
  }

  // Return all the functions and data needed by components
  return {
    stores,
    selectedStore,
    setSelectedStore,
    isLoading,
    addStore,
    deleteStore,
    updateStore,
    filterProductsByStore,
    filterTransactionsByStore,
    filterTransactions: fetchTransactions,
    filterInsightsByStore,
    fetchStoreInsights,
  }
}
