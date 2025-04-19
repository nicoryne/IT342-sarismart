"use client"

import { useState } from "react"
import { Calendar, Filter, LineChart, PieChart, BarChart3, RefreshCw } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useStores } from "@/hooks/use-stores"

export default function InsightsPage() {
  // STEP 1: Set up state for filtering and data display
  const [timeRange, setTimeRange] = useState("30d") // Time range for data filtering
  const [refreshing, setRefreshing] = useState(false) // Loading state for refresh action
  const [selectedStore, setSelectedStore] = useState("all") // Currently selected store

  // STEP 2: Get store insights data from the custom hook
  const { filterInsightsByStore } = useStores()

  // STEP 3: Get insights for the selected store
  // In a real implementation, this would fetch data from your backend API
  // Example: useEffect(() => { async function fetchData() { const response = await fetch(`/api/stores/${selectedStore}/insights`); const data = await response.json(); setStoreInsights(data); } fetchData(); }, [selectedStore]);
  const storeInsights = filterInsightsByStore(selectedStore)

  // STEP 4: Handle refresh button click to fetch fresh data
  const handleRefresh = () => {
    setRefreshing(true)
    // STEP 5: In a real implementation, this would fetch fresh data from the API
    // Example: async function fetchInsights() {
    //   try {
    //     const response = await fetch(`/api/stores/${selectedStore}/insights`, { cache: 'no-store' });
    //     const data = await response.json();
    //     setStoreInsights(data);
    //   } catch (error) {
    //     console.error('Error refreshing insights:', error);
    //   } finally {
    //     setRefreshing(false);
    //   }
    // }
    // fetchInsights();

    // Simulate API call with timeout
    setTimeout(() => setRefreshing(false), 1500)
  }

  return (
    <main className="flex flex-1 flex-col gap-4 p-4 md:gap-8 md:p-8">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Insights</h1>
          <p className="text-muted-foreground">Analyze your business performance and make data-driven decisions.</p>
        </div>
        <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
          {/* STEP 6: Refresh button to fetch fresh data */}
          <Button variant="outline" size="sm" onClick={handleRefresh} disabled={refreshing}>
            {refreshing ? <RefreshCw className="mr-2 h-4 w-4 animate-spin" /> : <RefreshCw className="mr-2 h-4 w-4" />}
            Refresh
          </Button>
          {/* STEP 7: Time range selector for filtering data */}
          <Select value={timeRange} onValueChange={setTimeRange}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Select a timeframe" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="7d">Last 7 days</SelectItem>
              <SelectItem value="30d">Last 30 days</SelectItem>
              <SelectItem value="90d">Last 90 days</SelectItem>
              <SelectItem value="12m">Last 12 months</SelectItem>
              <SelectItem value="custom">Custom range</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      {/* STEP 8: Insights tabs for different views */}
      <Tabs defaultValue="overview" className="space-y-4">
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="sales">Sales</TabsTrigger>
          <TabsTrigger value="inventory">Inventory</TabsTrigger>
        </TabsList>

        {/* STEP 9: Overview Tab - Shows key metrics */}
        <TabsContent value="overview" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {/* STEP 10: Revenue Trends Card */}
            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-base">Revenue Trends</CardTitle>
                <CardDescription>Monthly revenue performance</CardDescription>
              </CardHeader>
              <CardContent>
                {/* STEP 11: In a real implementation, this would be a chart component using data from your backend */}
                {/* Example: const [revenueData, setRevenueData] = useState([]); useEffect(() => { async function fetchRevenueData() { const response = await fetch(`/api/stores/${selectedStore}/insights/revenue?timeRange=${timeRange}`); const data = await response.json(); setRevenueData(data); } fetchRevenueData(); }, [selectedStore, timeRange]); */}
                <div className="h-[200px] w-full bg-[#f8f9fa] rounded-md flex items-center justify-center">
                  <LineChart className="h-16 w-16 text-muted-foreground/60" />
                </div>
              </CardContent>
              <CardFooter className="flex justify-between text-xs text-muted-foreground">
                <div>
                  YoY Growth: <span className="text-[#008080] font-medium">{storeInsights.revenueGrowth}</span>
                </div>
              </CardFooter>
            </Card>

            {/* STEP 12: Inventory Turnover Card */}
            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-base">Inventory Turnover</CardTitle>
                <CardDescription>Stock movement efficiency</CardDescription>
              </CardHeader>
              <CardContent>
                {/* STEP 13: In a real implementation, this would be a chart component using data from your backend */}
                {/* Example: const [turnoverData, setTurnoverData] = useState([]); useEffect(() => { async function fetchTurnoverData() { const response = await fetch(`/api/stores/${selectedStore}/insights/turnover?timeRange=${timeRange}`); const data = await response.json(); setTurnoverData(data); } fetchTurnoverData(); }, [selectedStore, timeRange]); */}
                <div className="h-[200px] w-full bg-[#f8f9fa] rounded-md flex items-center justify-center">
                  <BarChart3 className="h-16 w-16 text-muted-foreground/60" />
                </div>
              </CardContent>
              <CardFooter className="flex justify-between text-xs text-muted-foreground">
                <div>
                  Average Turnover: <span className="text-[#008080] font-medium">{storeInsights.turnover}</span>
                </div>
              </CardFooter>
            </Card>

            {/* STEP 14: Category Distribution Card */}
            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-base">Category Distribution</CardTitle>
                <CardDescription>Sales by product category</CardDescription>
              </CardHeader>
              <CardContent>
                {/* STEP 15: In a real implementation, this would be a chart component using data from your backend */}
                {/* Example: const [categoryData, setCategoryData] = useState([]); useEffect(() => { async function fetchCategoryData() { const response = await fetch(`/api/stores/${selectedStore}/insights/categories?timeRange=${timeRange}`); const data = await response.json(); setCategoryData(data); } fetchCategoryData(); }, [selectedStore, timeRange]); */}
                <div className="h-[200px] w-full bg-[#f8f9fa] rounded-md flex items-center justify-center">
                  <PieChart className="h-16 w-16 text-muted-foreground/60" />
                </div>
              </CardContent>
              <CardFooter className="flex justify-between text-xs text-muted-foreground">
                <div>
                  Top Category: <span className="text-[#008080] font-medium">{storeInsights.topCategory}</span>
                </div>
              </CardFooter>
            </Card>
          </div>

          {/* STEP 16: KPI Card - Shows key performance indicators */}
          <Card>
            <CardHeader>
              <CardTitle>Key Performance Indicators</CardTitle>
              <CardDescription>Track your most important business metrics</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                {/* STEP 17: In a real implementation, these would be fetched from an API */}
                {/* Example: const [kpis, setKpis] = useState([]); useEffect(() => { async function fetchKpis() { const response = await fetch(`/api/stores/${selectedStore}/insights/kpis?timeRange=${timeRange}`); const data = await response.json(); setKpis(data); } fetchKpis(); }, [selectedStore, timeRange]); */}
                {[
                  { title: "Average Order Value", value: "$0.00", change: "+0%", period: "vs. last period" },
                  { title: "Inventory Turnover", value: "0x", change: "+0x", period: "vs. last period" },
                  { title: "Stock to Sales Ratio", value: "0", change: "+0", period: "vs. last period" },
                  { title: "Out of Stock Rate", value: "0%", change: "+0%", period: "vs. last period" },
                  { title: "Inventory Accuracy", value: "0%", change: "+0%", period: "vs. last period" },
                  { title: "Shrinkage Rate", value: "0%", change: "+0%", period: "vs. last period" },
                  {
                    title: "Days Inventory Outstanding",
                    value: "0 days",
                    change: "+0 days",
                    period: "vs. last period",
                  },
                  { title: "Perfect Order Rate", value: "0%", change: "+0%", period: "vs. last period" },
                ].map((kpi, index) => (
                  <div key={index} className="rounded-lg border p-3">
                    <div className="text-sm font-medium text-muted-foreground">{kpi.title}</div>
                    <div className="mt-1 flex items-baseline">
                      <div className="text-2xl font-semibold">{kpi.value}</div>
                      <div
                        className={`ml-2 text-xs font-medium ${
                          kpi.change.startsWith("+") ? "text-[#40E0D0]" : "text-red-500"
                        }`}
                      >
                        {kpi.change}
                      </div>
                    </div>
                    <div className="mt-1 text-xs text-muted-foreground">{kpi.period}</div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* STEP 18: Sales Analytics Tab */}
        <TabsContent value="sales" className="space-y-4">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>Sales Analytics</CardTitle>
                  <CardDescription>Detailed sales performance metrics</CardDescription>
                </div>
                <div className="flex items-center gap-2">
                  {/* STEP 19: Date range picker for sales data */}
                  <Button variant="outline" size="sm">
                    <Calendar className="mr-2 h-4 w-4" />
                    Date Range
                  </Button>
                </div>
              </div>
            </CardHeader>
            <CardContent className="h-[500px] flex items-center justify-center">
              {/* STEP 20: In a real implementation, this would be a chart component using data from your backend */}
              {/* Example: const [salesData, setSalesData] = useState([]); useEffect(() => { async function fetchSalesData() { const response = await fetch(`/api/stores/${selectedStore}/analytics/sales?timeRange=${timeRange}`); const data = await response.json(); setSalesData(data); } fetchSalesData(); }, [selectedStore, timeRange]); */}
              <div className="text-center space-y-2">
                <LineChart className="h-16 w-16 mx-auto text-muted-foreground/60" />
                <p className="text-muted-foreground">
                  No sales data available. Sales analytics will appear here when data is available.
                </p>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* STEP 21: Inventory Analytics Tab */}
        <TabsContent value="inventory" className="space-y-4">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>Inventory Analytics</CardTitle>
                  <CardDescription>Track inventory performance and trends</CardDescription>
                </div>
                <div className="flex items-center gap-2">
                  {/* STEP 22: Category filter for inventory data */}
                  <Button variant="outline" size="sm">
                    <Filter className="mr-2 h-4 w-4" />
                    Categories
                  </Button>
                </div>
              </div>
            </CardHeader>
            <CardContent className="h-[500px] flex items-center justify-center">
              {/* STEP 23: In a real implementation, this would be a chart component using data from your backend */}
              {/* Example: const [inventoryData, setInventoryData] = useState([]); useEffect(() => { async function fetchInventoryData() { const response = await fetch(`/api/stores/${selectedStore}/analytics/inventory?timeRange=${timeRange}`); const data = await response.json(); setInventoryData(data); } fetchInventoryData(); }, [selectedStore, timeRange]); */}
              <div className="text-center space-y-2">
                <BarChart3 className="h-16 w-16 mx-auto text-muted-foreground/60" />
                <p className="text-muted-foreground">
                  No inventory data available. Inventory analytics will appear here when data is available.
                </p>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </main>
  )
}
