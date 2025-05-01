"use client"

import { useState, useEffect } from "react"
import { LineChart, PieChart, BarChart3, Loader2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useStoresContext } from "@/hooks/use-stores-context"
import { StoreSelector } from "@/components/store-selector"
import { showToast } from "@/components/ui/toast-notification"

export default function InsightsPage() {
  const [timeRange, setTimeRange] = useState("30d")
  const { selectedStore, filterInsightsByStore, stores } = useStoresContext()
  const [storeInsights, setStoreInsights] = useState({
    revenueGrowth: "0%",
    turnover: "0x",
    topCategory: "None",
  })
  const [isLoading, setIsLoading] = useState(false)
  const [salesData, setSalesData] = useState([])
  const [inventoryData, setInventoryData] = useState([])

  // Define the type for stock history entries
  type StockHistoryEntry = {
    description: string;
    // Add other properties if needed
  };

  // Define the type for restock alert entries
  type RestockAlertEntry = {
    message: string;
    // Add other properties if needed
  };

  // Update the state type
  const [stockHistory, setStockHistory] = useState<StockHistoryEntry[]>([])
  const [restockAlerts, setRestockAlerts] = useState<RestockAlertEntry[]>([])

  // Fetch insights when selectedStore or timeRange changes
  useEffect(() => {
    const fetchInsights = async () => {
      setIsLoading(true)
      try {
        // Check if filterInsightsByStore exists before calling it
        if (typeof filterInsightsByStore !== "function") {
          console.error("filterInsightsByStore is not a function")
          setStoreInsights({ revenueGrowth: "0%", turnover: "0x", topCategory: "None" })
          return
        }

        const insights = await filterInsightsByStore(selectedStore)
        setStoreInsights(insights)

        // Fetch sales data
        await fetchSalesData()

        // Fetch inventory data
        await fetchInventoryData()

        // Fetch stock adjustment history
        await fetchStockHistory()

        // Fetch restock alerts
        await fetchRestockAlerts()
      } catch (error) {
        console.error("Error fetching insights:", error)
        showToast("Failed to fetch insights data", "error")
        setStoreInsights({ revenueGrowth: "0%", turnover: "0x", topCategory: "None" })
      } finally {
        setIsLoading(false)
      }
    }

    fetchInsights()
  }, [selectedStore, timeRange, filterInsightsByStore, stores])

  // Fetch sales data from the API
  const fetchSalesData = async () => {
    try {
      const token = localStorage.getItem("token")
      if (!token) {
        showToast("Authentication token not found", "error")
        return
      }

      if (selectedStore === "all") {
        // For "all" stores, we could aggregate data from all user's stores
        // This is a simplified implementation - just setting empty data
        setSalesData([])
        return
      }

      // Verify the selected store belongs to the user's stores
      const storeExists = stores.some((store) => String(store.id) === selectedStore)
      if (!storeExists) {
        console.warn(`Store ${selectedStore} does not belong to the current user`)
        setSalesData([])
        return
      }

      // Determine which report to fetch based on timeRange
      const reportType = timeRange === "7d" ? "daily" : "monthly"

      const response = await fetch(
        `https://sarismart-backend.onrender.com/api/v1/stores/${selectedStore}/reports/${reportType}`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      )

      if (!response.ok) {
        throw new Error(`Failed to fetch sales data: ${response.status}`)
      }

      const data = await response.json()
      setSalesData(data)
    } catch (error) {
      console.error("Error fetching sales data:", error)
      setSalesData([])
    }
  }

  // Fetch inventory data from the API
  const fetchInventoryData = async () => {
    try {
      const token = localStorage.getItem("token")
      if (!token) {
        showToast("Authentication token not found", "error")
        return
      }

      if (selectedStore === "all") {
        // For "all" stores, we could aggregate data from all user's stores
        // This is a simplified implementation - just setting empty data
        setInventoryData([])
        return
      }

      // Verify the selected store belongs to the user's stores
      const storeExists = stores.some((store) => String(store.id) === selectedStore)
      if (!storeExists) {
        console.warn(`Store ${selectedStore} does not belong to the current user`)
        setInventoryData([])
        return
      }

      const response = await fetch(
        `https://sarismart-backend.onrender.com/api/v1/stores/${selectedStore}/reports/inventory`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      )

      if (!response.ok) {
        throw new Error(`Failed to fetch inventory data: ${response.status}`)
      }

      const data = await response.json()
      setInventoryData(data)
    } catch (error) {
      console.error("Error fetching inventory data:", error)
      setInventoryData([])
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
          })
        )
        // Flatten the aggregated data and map it to the desired format
        const aggregatedData = allStockHistory.flat().map((entry) => ({
          description: `${entry.productName || entry.name || "Unknown Product"} stock has been updated to ${
            entry.newStock || "N/A"
          } on ${new Date(entry.timestamp).toLocaleString()}`,
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

      // Map data to the desired message format
      if (Array.isArray(data) && data.length > 0) {
        setStockHistory(data.map((entry) => ({
          description: `${entry.productName || entry.name || "Unknown Product"} stock has been updated to ${
            entry.newStock || "N/A"
          } on ${new Date(entry.timestamp).toLocaleString()}`,
        })))
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
        setRestockAlerts([])
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
      setRestockAlerts(data)
    } catch (error) {
      console.error("Error fetching restock alerts:", error)
      setRestockAlerts([])
    }
  }

  // Load sample data for demonstration
  const loadSampleData = () => {
    showToast("Sample data loaded for demonstration", "info")
    // This would be replaced with real data in production
  }

  return (
    <main className="flex-1 p-4 md:p-6">
      {/* Page Header */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">Insights</h1>
          <p className="text-muted-foreground">Analyze your business performance</p>
        </div>
        <div className="flex flex-wrap gap-2">
          <StoreSelector />
        </div>
      </div>

      {/* Main Content */}
      <Tabs defaultValue="overview" className="space-y-4">
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="inventory">Inventory</TabsTrigger>
        </TabsList>

        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="h-8 w-8 animate-spin text-[#008080]" />
          </div>
        ) : (
          <>
            <TabsContent value="overview" className="space-y-4">
              {/* KPI Cards */}
              <div className="grid gap-4 md:grid-cols-3">
                <MetricCard
                  title="Revenue Growth"
                  value={storeInsights.revenueGrowth}
                  change=""
                  icon={<LineChart className="h-8 w-8 text-muted-foreground/60" />}
                />
                <MetricCard
                  title="Inventory Turnover"
                  value={storeInsights.turnover}
                  change=""
                  icon={<BarChart3 className="h-8 w-8 text-muted-foreground/60" />}
                />
                <MetricCard
                  title="Top Category"
                  value={storeInsights.topCategory}
                  change=""
                  icon={<PieChart className="h-8 w-8 text-muted-foreground/60" />}
                />
              </div>

              {/* KPI Grid */}
              <Card>
                <CardHeader>
                  <CardTitle>Key Performance Indicators</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                    {[
                      { title: "Average Order Value", value: "₱0.00" },
                      { title: "Inventory Turnover", value: storeInsights.turnover },
                      { title: "Out of Stock Rate", value: "0%" },
                      { title: "Inventory Accuracy", value: "100%" },
                    ].map((kpi, index) => (
                      <div key={index} className="rounded-lg border p-3">
                        <div className="text-sm font-medium text-muted-foreground">{kpi.title}</div>
                        <div className="mt-1 text-2xl font-semibold">{kpi.value}</div>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="inventory" className="space-y-4">
              <Card>
                <CardHeader>
                  <CardTitle>Inventory Analytics</CardTitle>
                </CardHeader>
                <CardContent className="h-[300px] flex items-center justify-center">
                  {inventoryData.length > 0 ? (
                    <div>
                      {/* This would be replaced with a real chart in production */}
                      <p>Inventory data visualization would appear here</p>
                    </div>
                  ) : (
                    <div className="text-center">
                      <BarChart3 className="h-16 w-16 mx-auto text-muted-foreground/60" />
                      <p className="text-muted-foreground mt-4">No inventory data available yet</p>
                      <Button className="mt-4" variant="outline" onClick={loadSampleData}>
                        Load Sample Data
                      </Button>
                    </div>
                  )}
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Stock Adjustment History</CardTitle>
                </CardHeader>
                <CardContent>
                  {stockHistory.length > 0 ? (
                    <ul className="list-disc pl-5">
                      {stockHistory.map((entry, index) => (
                        <li key={index}>{entry.description}</li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-muted-foreground">No stock adjustment history available</p>
                  )}
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Restock Alerts</CardTitle>
                </CardHeader>
                <CardContent>
                  {restockAlerts.length > 0 ? (
                    <ul>
                      {restockAlerts.map((alert, index) => (
                        <li key={index}>{alert.message}</li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-muted-foreground">No restock alerts available</p>
                  )}
                </CardContent>
              </Card>
            </TabsContent>
          </>
        )}
      </Tabs>
    </main>
  )
}

// Simple reusable metric card component
type MetricCardProps = {
  title: string;
  value: string;
  change?: string;
  icon: React.ReactNode;
};

function MetricCard({ title, value, change, icon }: MetricCardProps) {
  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between pb-2">
        <CardTitle className="text-sm font-medium">{title}</CardTitle>
        {icon}
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold">{value}</div>
        {change && (
          <p className="text-xs text-muted-foreground">
            <span className="text-green-500">{change}</span> from last month
          </p>
        )}
      </CardContent>
    </Card>
  )
}
