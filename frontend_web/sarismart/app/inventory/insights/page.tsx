"use client"

import { useState, useEffect } from "react"
import { History, AlertCircle, Loader2, RefreshCw } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { useStoresContext } from "@/hooks/use-stores-context"
import { StoreSelector } from "@/components/store-selector"
import { showToast } from "@/components/ui/toast-notification"
import { Separator } from "@/components/ui/separator"
import { Badge } from "@/components/ui/badge"
import { ScrollArea } from "@/components/ui/scroll-area"

// Define the type for stock history entries
type StockHistoryEntry = {
  description: string
  timestamp?: string
  productName?: string
  oldStock?: string | number
  newStock?: string | number
  user?: string
}

// Define the type for restock alert entries
type RestockAlertEntry = {
  message: string
  productName?: string
  currentStock?: number
  threshold?: number
  severity?: "low" | "critical"
}

export default function InsightsPage() {
  const { selectedStore, stores } = useStoresContext()
  const [isLoading, setIsLoading] = useState(false)
  const [isRefreshing, setIsRefreshing] = useState(false)
  const [stockHistory, setStockHistory] = useState<StockHistoryEntry[]>([])
  const [restockAlerts, setRestockAlerts] = useState<RestockAlertEntry[]>([])
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null)

  // Fetch data when selectedStore changes
  useEffect(() => {
    fetchData()
  }, [selectedStore])

  const fetchData = async () => {
    setIsLoading(true)
    try {
      await Promise.all([fetchStockHistory(), fetchRestockAlerts()])
      setLastUpdated(new Date())
    } catch (error) {
      console.error("Error fetching data:", error)
    } finally {
      setIsLoading(false)
    }
  }

  const refreshData = async () => {
    setIsRefreshing(true)
    try {
      await Promise.all([fetchStockHistory(), fetchRestockAlerts()])
      setLastUpdated(new Date())
      showToast("Data refreshed successfully", "success")
    } catch (error) {
      console.error("Error refreshing data:", error)
      showToast("Failed to refresh data", "error")
    } finally {
      setIsRefreshing(false)
    }
  }

  // Fetch stock adjustment history
  const fetchStockHistory = async () => {
    try {
      const token = localStorage.getItem("token")
      if (!token) {
        showToast("Authentication token not found", "error")
        return
      }

      if (selectedStore === "all") {
        // Aggregate stock history for all stores
        const allStockHistory = await Promise.all(
          stores.map(async (store) => {
            const response = await fetch(
              `https://sarismart-backend.onrender.com/api/v1/stores/${store.id}/stock/history`,
              {
                method: "GET",
                headers: {
                  Authorization: `Bearer ${token}`,
                },
              },
            )
            if (!response.ok) {
              console.warn(`Failed to fetch stock history for store ${store.id}: ${response.status}`)
              return []
            }
            return response.json()
          }),
        )
        // Flatten the aggregated data and map it to the desired format
        const aggregatedData = allStockHistory.flat().map((entry) => ({
          description: `${entry.product.name || "Unknown Product"} stock has been updated from ${entry.oldStock || "N/A"} to ${entry.newStock || "N/A"} on ${new Date(entry.timestamp).toLocaleString()} by ${entry.user.fullName || entry.user.email || "N/A"}`,
          timestamp: entry.timestamp,
          productName: entry.product.name || "Unknown Product",
          oldStock: entry.oldStock || "N/A",
          newStock: entry.newStock || "N/A",
          user: entry.user.fullName || entry.user.email || "N/A",
        }))
        setStockHistory(aggregatedData)
        return
      }

      const storeExists = stores.some((store) => String(store.id) === selectedStore)
      if (!storeExists) {
        console.warn(`Store ${selectedStore} does not belong to the current user`)
        setStockHistory([])
        return
      }

      const response = await fetch(
        `https://sarismart-backend.onrender.com/api/v1/stores/${selectedStore}/stock/history`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      )

      if (!response.ok) {
        throw new Error(`Failed to fetch stock history: ${response.status}`)
      }

      const data = await response.json()

      // Map data to the desired format
      if (Array.isArray(data) && data.length > 0) {
        setStockHistory(
          data.map((entry) => ({
            description: `${entry.product.name || "Unknown Product"} stock has been updated from ${entry.oldStock || "N/A"} to ${entry.newStock || "N/A"} on ${new Date(entry.timestamp).toLocaleString()} by ${entry.user.fullName || entry.user.email || "N/A"}`,
            timestamp: entry.timestamp,
            productName: entry.product.name || "Unknown Product",
            oldStock: entry.oldStock || "N/A",
            newStock: entry.newStock || "N/A",
            user: entry.user.fullName || entry.user.email || "N/A",
          })),
        )
      } else {
        setStockHistory([])
      }
    } catch (error) {
      console.error("Error fetching stock history:", error)
      setStockHistory([])
    }
  }

  // Fetch restock alerts
  const fetchRestockAlerts = async () => {
    try {
      const token = localStorage.getItem("token")
      if (!token) {
        showToast("Authentication token not found", "error")
        return
      }

      if (selectedStore === "all") {
        // For demo purposes, create some sample alerts
        const sampleAlerts = [
          {
            message: "Coca-Cola is running low on stock (5 remaining, threshold: 10)",
            productName: "Coca-Cola",
            currentStock: 5,
            threshold: 10,
            severity: "low",
          },
          {
            message: "Rice is critically low on stock (2 remaining, threshold: 15)",
            productName: "Rice",
            currentStock: 2,
            threshold: 15,
            severity: "critical",
          },
        ] as RestockAlertEntry[]
        setRestockAlerts(sampleAlerts)
        return
      }

      const storeExists = stores.some((store) => String(store.id) === selectedStore)
      if (!storeExists) {
        console.warn(`Store ${selectedStore} does not belong to the current user`)
        setRestockAlerts([])
        return
      }

      const response = await fetch(
        `https://sarismart-backend.onrender.com/api/v1/stores/${selectedStore}/inventory/alerts`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      )

      if (!response.ok) {
        throw new Error(`Failed to fetch restock alerts: ${response.status}`)
      }

      const data = await response.json()

      // For demo purposes, if no alerts are returned, create some sample ones
      if (Array.isArray(data) && data.length === 0) {
        const sampleAlerts = [
          {
            message: "Coca-Cola is running low on stock (5 remaining, threshold: 10)",
            productName: "Coca-Cola",
            currentStock: 5,
            threshold: 10,
            severity: "low",
          },
          {
            message: "Rice is critically low on stock (2 remaining, threshold: 15)",
            productName: "Rice",
            currentStock: 2,
            threshold: 15,
            severity: "critical",
          },
        ] as RestockAlertEntry[]
        setRestockAlerts(sampleAlerts)
      } else {
        setRestockAlerts(data)
      }
    } catch (error) {
      console.error("Error fetching restock alerts:", error)
      setRestockAlerts([])
    }
  }

  // Format date for display
  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    })
  }

  return (
    <main className="flex-1 p-4 md:p-6">
      {/* Page Header */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">Inventory Insights</h1>
          <p className="text-muted-foreground">
            Monitor stock adjustments and restock alerts
            {lastUpdated && <span className="ml-2 text-xs">Last updated: {lastUpdated.toLocaleTimeString()}</span>}
          </p>
        </div>
        <div className="flex flex-wrap items-center gap-2">
          <StoreSelector />
          <Button
            variant="outline"
            size="sm"
            onClick={refreshData}
            disabled={isRefreshing}
            className="flex items-center gap-1"
          >
            {isRefreshing ? <Loader2 className="h-4 w-4 animate-spin" /> : <RefreshCw className="h-4 w-4" />}
            Refresh
          </Button>
        </div>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      ) : (
        <div className="grid gap-6 md:grid-cols-2">
          {/* Stock Adjustment History */}
          <Card className="md:col-span-1">
            <CardHeader className="pb-3">
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle className="flex items-center gap-2">
                    <History className="h-5 w-5 text-primary" />
                    Stock Adjustment History
                  </CardTitle>
                  <CardDescription>Recent changes to your inventory</CardDescription>
                </div>
                <Badge variant="outline" className="ml-auto">
                  {stockHistory.length} entries
                </Badge>
              </div>
            </CardHeader>
            <Separator />
            <CardContent className="pt-4">
              <ScrollArea className="h-[400px] pr-4">
                {stockHistory.length > 0 ? (
                  <div className="space-y-4">
                    {stockHistory.map((entry, index) => (
                      <div key={index} className="rounded-lg border p-3 bg-card">
                        <div className="flex flex-col space-y-1.5">
                          <div className="flex items-center justify-between">
                            <h3 className="font-semibold text-sm">{entry.productName}</h3>
                            <time className="text-xs text-muted-foreground">
                              {entry.timestamp ? formatDate(entry.timestamp) : "N/A"}
                            </time>
                          </div>
                          <div className="flex items-center gap-2 text-sm">
                            <span className="text-muted-foreground">Stock changed:</span>
                            <span className="font-medium">{entry.oldStock}</span>
                            <span className="text-muted-foreground">→</span>
                            <span className="font-medium">{entry.newStock}</span>
                          </div>
                          <div className="text-xs text-muted-foreground">Updated by: {entry.user}</div>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="flex flex-col items-center justify-center py-8 text-center">
                    <History className="h-12 w-12 text-muted-foreground/60 mb-2" />
                    <p className="text-muted-foreground">No stock adjustment history available</p>
                  </div>
                )}
              </ScrollArea>
            </CardContent>
          </Card>

          {/* Restock Alerts */}
          <Card className="md:col-span-1">
            <CardHeader className="pb-3">
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle className="flex items-center gap-2">
                    <AlertCircle className="h-5 w-5 text-destructive" />
                    Restock Alerts
                  </CardTitle>
                  <CardDescription>Products that need to be restocked</CardDescription>
                </div>
                <Badge variant="outline" className="ml-auto">
                  {restockAlerts.length} alerts
                </Badge>
              </div>
            </CardHeader>
            <Separator />
            <CardContent className="pt-4">
              <ScrollArea className="h-[400px] pr-4">
                {restockAlerts.length > 0 ? (
                  <div className="space-y-4">
                    {restockAlerts.map((alert, index) => (
                      <div
                        key={index}
                        className={`rounded-lg border p-3 ${
                          alert.severity === "critical"
                            ? "border-destructive/50 bg-destructive/5"
                            : "border-amber-500/50 bg-amber-500/5"
                        }`}
                      >
                        <div className="flex flex-col space-y-1.5">
                          <div className="flex items-center justify-between">
                            <h3 className="font-semibold text-sm flex items-center gap-1">
                              {alert.productName || "Unknown Product"}
                              <Badge
                                variant={alert.severity === "critical" ? "destructive" : "outline"}
                                className="ml-2 text-xs"
                              >
                                {alert.severity === "critical" ? "Critical" : "Low Stock"}
                              </Badge>
                            </h3>
                          </div>
                          <div className="flex items-center gap-2 text-sm">
                            <span className="text-muted-foreground">Current stock:</span>
                            <span className="font-medium">{alert.currentStock || "N/A"}</span>
                            <span className="text-muted-foreground">Threshold:</span>
                            <span className="font-medium">{alert.threshold || "N/A"}</span>
                          </div>
                          <div className="text-xs text-muted-foreground mt-1">{alert.message}</div>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="flex flex-col items-center justify-center py-8 text-center">
                    <AlertCircle className="h-12 w-12 text-muted-foreground/60 mb-2" />
                    <p className="text-muted-foreground">No restock alerts available</p>
                  </div>
                )}
              </ScrollArea>
            </CardContent>
          </Card>
        </div>
      )}
    </main>
  )
}
