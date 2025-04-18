"use client"

import { useState } from "react"

// Sample store data
const storeData = [
  { id: "store1", name: "Downtown Branch", location: "City Center" },
  { id: "store2", name: "Westside Mall", location: "West District" },
  { id: "store3", name: "Eastside Plaza", location: "East District" },
  { id: "store4", name: "North Point", location: "North District" },
  { id: "store5", name: "South Center", location: "South District" },
]

export function useStores() {
  const [stores] = useState(storeData)

  const filterProductsByStore = (storeId: string) => {
    // For demo purposes, we'll just return different counts based on the store
    return {
      totalProducts: storeId === "all" ? 1248 : Math.floor(Math.random() * 300) + 100,
      categories: storeId === "all" ? 32 : Math.floor(Math.random() * 15) + 5,
      lowStock: storeId === "all" ? 42 : Math.floor(Math.random() * 10) + 2,
      inventoryValue: storeId === "all" ? "$1.2M" : `$${(Math.random() * 400000 + 100000).toFixed(0)}`,
    }
  }

  const filterTransactionsByStore = (storeId: string, allTransactions: any[]) => {
    if (storeId === "all") {
      return allTransactions
    }

    // For demo purposes, we'll just return a subset of transactions
    return allTransactions.filter((_, index) => {
      // Use the store ID to create a deterministic but different subset for each store
      const storeNum = Number.parseInt(storeId.replace("store", ""))
      return index % 5 === (storeNum - 1) % 5 || index % 3 === (storeNum - 1) % 3
    })
  }

  const filterInsightsByStore = (storeId: string) => {
    return {
      revenueGrowth: storeId === "all" ? "+24.5%" : `+${(Math.random() * 30).toFixed(1)}%`,
      turnover: storeId === "all" ? "4.3x" : `${(Math.random() * 5 + 2).toFixed(1)}x`,
      topCategory:
        storeId === "all"
          ? "Electronics"
          : ["Electronics", "Clothing", "Food & Beverage", "Home Goods"][Math.floor(Math.random() * 4)],
    }
  }

  return {
    stores,
    filterProductsByStore,
    filterTransactionsByStore,
    filterInsightsByStore,
  }
}
