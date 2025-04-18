"use client"

import { useState } from "react"
import { Calendar, Download, Filter, LineChart, PieChart, BarChart3, RefreshCw, Share2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useStores } from "@/hooks/use-stores"

export default function InsightsPage() {
  const [timeRange, setTimeRange] = useState("30d")
  const [refreshing, setRefreshing] = useState(false)
  const [selectedStore, setSelectedStore] = useState("all")
  const { filterInsightsByStore } = useStores()

  // Get insights for the selected store
  const storeInsights = filterInsightsByStore(selectedStore)

  const handleRefresh = () => {
    setRefreshing(true)
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
          <div className="flex items-center gap-2">
            <Button variant="outline" size="sm" onClick={handleRefresh} disabled={refreshing}>
              {refreshing ? (
                <RefreshCw className="mr-2 h-4 w-4 animate-spin" />
              ) : (
                <RefreshCw className="mr-2 h-4 w-4" />
              )}
              Refresh
            </Button>
            <Button variant="outline" size="sm">
              <Filter className="mr-2 h-4 w-4" />
              Filter
            </Button>
            <Button variant="outline" size="sm">
              <Share2 className="mr-2 h-4 w-4" />
              Share
            </Button>
          </div>
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

      <Tabs defaultValue="overview" className="space-y-4">
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="sales">Sales</TabsTrigger>
          <TabsTrigger value="inventory">Inventory</TabsTrigger>
          <TabsTrigger value="customers">Customers</TabsTrigger>
        </TabsList>

        <TabsContent value="overview" className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-base">Revenue Trends</CardTitle>
                <CardDescription>Monthly revenue performance</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="h-[200px] w-full bg-[#f8f9fa] rounded-md flex items-center justify-center">
                  <LineChart className="h-16 w-16 text-muted-foreground/60" />
                </div>
              </CardContent>
              <CardFooter className="flex justify-between text-xs text-muted-foreground">
                <div>
                  YoY Growth: <span className="text-[#008080] font-medium">{storeInsights.revenueGrowth}</span>
                </div>
                <Button variant="ghost" size="sm" className="h-8 text-xs">
                  <Download className="mr-2 h-3 w-3" />
                  Export
                </Button>
              </CardFooter>
            </Card>

            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-base">Inventory Turnover</CardTitle>
                <CardDescription>Stock movement efficiency</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="h-[200px] w-full bg-[#f8f9fa] rounded-md flex items-center justify-center">
                  <BarChart3 className="h-16 w-16 text-muted-foreground/60" />
                </div>
              </CardContent>
              <CardFooter className="flex justify-between text-xs text-muted-foreground">
                <div>
                  Average Turnover: <span className="text-[#008080] font-medium">{storeInsights.turnover}</span>
                </div>
                <Button variant="ghost" size="sm" className="h-8 text-xs">
                  <Download className="mr-2 h-3 w-3" />
                  Export
                </Button>
              </CardFooter>
            </Card>

            <Card>
              <CardHeader className="pb-2">
                <CardTitle className="text-base">Category Distribution</CardTitle>
                <CardDescription>Sales by product category</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="h-[200px] w-full bg-[#f8f9fa] rounded-md flex items-center justify-center">
                  <PieChart className="h-16 w-16 text-muted-foreground/60" />
                </div>
              </CardContent>
              <CardFooter className="flex justify-between text-xs text-muted-foreground">
                <div>
                  Top Category: <span className="text-[#008080] font-medium">{storeInsights.topCategory}</span>
                </div>
                <Button variant="ghost" size="sm" className="h-8 text-xs">
                  <Download className="mr-2 h-3 w-3" />
                  Export
                </Button>
              </CardFooter>
            </Card>
          </div>

          <Card>
            <CardHeader>
              <CardTitle>Key Performance Indicators</CardTitle>
              <CardDescription>Track your most important business metrics</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                {[
                  { title: "Average Order Value", value: "$89.42", change: "+5.2%", period: "vs. last period" },
                  { title: "Inventory Turnover", value: "4.3x", change: "+0.6x", period: "vs. last period" },
                  { title: "Stock to Sales Ratio", value: "1.8", change: "-0.3", period: "vs. last period" },
                  { title: "Out of Stock Rate", value: "3.5%", change: "-2.1%", period: "vs. last period" },
                  { title: "Inventory Accuracy", value: "97.2%", change: "+1.3%", period: "vs. last period" },
                  { title: "Shrinkage Rate", value: "1.2%", change: "-0.8%", period: "vs. last period" },
                  {
                    title: "Days Inventory Outstanding",
                    value: "32 days",
                    change: "-4 days",
                    period: "vs. last period",
                  },
                  { title: "Perfect Order Rate", value: "94.3%", change: "+3.7%", period: "vs. last period" },
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

        <TabsContent value="sales" className="space-y-4">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>Sales Analytics</CardTitle>
                  <CardDescription>Detailed sales performance metrics</CardDescription>
                </div>
                <div className="flex items-center gap-2">
                  <Button variant="outline" size="sm">
                    <Calendar className="mr-2 h-4 w-4" />
                    Date Range
                  </Button>
                  <Button variant="outline" size="sm">
                    <Download className="mr-2 h-4 w-4" />
                    Export
                  </Button>
                </div>
              </div>
            </CardHeader>
            <CardContent className="h-[500px] flex items-center justify-center">
              <div className="text-center space-y-2">
                <LineChart className="h-16 w-16 mx-auto text-muted-foreground/60" />
                <p className="text-muted-foreground">Detailed sales analytics will appear here</p>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="inventory" className="space-y-4">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>Inventory Analytics</CardTitle>
                  <CardDescription>Track inventory performance and trends</CardDescription>
                </div>
                <div className="flex items-center gap-2">
                  <Button variant="outline" size="sm">
                    <Filter className="mr-2 h-4 w-4" />
                    Categories
                  </Button>
                  <Button variant="outline" size="sm">
                    <Download className="mr-2 h-4 w-4" />
                    Export
                  </Button>
                </div>
              </div>
            </CardHeader>
            <CardContent className="h-[500px] flex items-center justify-center">
              <div className="text-center space-y-2">
                <BarChart3 className="h-16 w-16 mx-auto text-muted-foreground/60" />
                <p className="text-muted-foreground">Inventory analytics will appear here</p>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="customers" className="space-y-4">
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <CardTitle>Customer Insights</CardTitle>
                  <CardDescription>Understand your customer behavior</CardDescription>
                </div>
                <div className="flex items-center gap-2">
                  <Button variant="outline" size="sm">
                    <Filter className="mr-2 h-4 w-4" />
                    Segment
                  </Button>
                  <Button variant="outline" size="sm">
                    <Download className="mr-2 h-4 w-4" />
                    Export
                  </Button>
                </div>
              </div>
            </CardHeader>
            <CardContent className="h-[500px] flex items-center justify-center">
              <div className="text-center space-y-2">
                <PieChart className="h-16 w-16 mx-auto text-muted-foreground/60" />
                <p className="text-muted-foreground">Customer insights will appear here</p>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </main>
  )
}
