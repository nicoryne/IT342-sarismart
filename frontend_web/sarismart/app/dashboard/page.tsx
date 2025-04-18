"use client"

import { BarChart3, Clock, Download, Plus, ShoppingCart, Users } from "lucide-react"
import { Home,LineChart,Package,History,LogOut,} from "lucide-react"
  

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"

// Simple placeholder components to replace the missing ones
function DashboardHeader() {
  return (
    <header className="sticky top-0 z-40 border-b bg-white">
      <div className="container flex h-14 items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="text-lg font-bold">
            <span className="text-[#008080]">Sari</span>
            <span>Smart</span>
          </span>
        </div>
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="sm">
            Search
          </Button>
          <Button variant="ghost" size="sm">
            Notifications
          </Button>
          <Button variant="ghost" size="sm">
            Profile
          </Button>
        </div>
      </div>
    </header>
  )
}

function DashboardNav() {
    return (
    <div className="flex flex-col gap-2 p-4 pt-0 mt-6">
        <div className="space-y-1">
          <Button variant="ghost" className="w-full justify-start gap-3 text-lg font-medium">
            <Home className="h-6 w-6" />
            Dashboard
          </Button>
          <Button variant="ghost" className="w-full justify-start gap-3 text-lg font-medium">
            <LineChart className="h-6 w-6" />
            Insights
          </Button>
          <Button variant="ghost" className="w-full justify-start gap-3 text-lg font-medium">
            <Package className="h-6 w-6" />
            Products
          </Button>
          <Button variant="ghost" className="w-full justify-start gap-3 text-lg font-medium">
            <History className="h-6 w-6" />
            History
          </Button>
          <Button variant="ghost" className="w-full justify-start gap-3 text-lg font-medium text-red-500">
            <LogOut className="h-6 w-6" />
            Sign Out
          </Button>
        </div>
      </div>
    )
  }
  
  

function Overview() {
  return (
    <div className="h-[350px] w-full">
      <div className="flex h-full flex-col items-center justify-center text-muted-foreground">
        <p>Monthly revenue chart</p>
        <p className="text-sm">(Chart visualization would appear here)</p>
      </div>
    </div>
  )
}

function RecentSales() {
  return (
    <div className="space-y-8">
      <div className="flex items-center">
        <div className="h-9 w-9 rounded-full bg-gray-200"></div>
        <div className="ml-4 space-y-1">
          <p className="text-sm font-medium leading-none">John Doe</p>
          <p className="text-sm text-muted-foreground">john.doe@example.com</p>
        </div>
        <div className="ml-auto font-medium">+$1,999.00</div>
      </div>
      <div className="flex items-center">
        <div className="h-9 w-9 rounded-full bg-gray-200"></div>
        <div className="ml-4 space-y-1">
          <p className="text-sm font-medium leading-none">Alice Smith</p>
          <p className="text-sm text-muted-foreground">alice.smith@example.com</p>
        </div>
        <div className="ml-auto font-medium">+$1,499.00</div>
      </div>
      <div className="flex items-center">
        <div className="h-9 w-9 rounded-full bg-gray-200"></div>
        <div className="ml-4 space-y-1">
          <p className="text-sm font-medium leading-none">Robert Johnson</p>
          <p className="text-sm text-muted-foreground">robert.johnson@example.com</p>
        </div>
        <div className="ml-auto font-medium">+$899.00</div>
      </div>
    </div>
  )
}

export default function DashboardPage() {
  return (
    <div className="flex min-h-screen flex-col">
      <DashboardHeader />
      <div className="container flex-1 items-start md:grid md:grid-cols-[220px_1fr] md:gap-6 lg:grid-cols-[240px_1fr] lg:gap-10">
        <aside className="fixed top-14 z-30 -ml-2 hidden h-[calc(100vh-3.5rem)] w-full shrink-0 overflow-y-auto border-r md:sticky md:block">
          <DashboardNav />
        </aside>
        <main className="flex w-full flex-col overflow-hidden">
          <div className="flex-1 space-y-4 p-8 pt-6">
            <div className="flex items-center justify-between space-y-2">
              <h2 className="text-3xl font-bold tracking-tight">Dashboard</h2>
              <div className="flex items-center space-x-2">
                <Button variant="outline" size="sm" className="h-9">
                  <Download className="mr-2 h-4 w-4" />
                  Download
                </Button>
                <Button size="sm" className="h-9 bg-[#008080] hover:bg-[#005F6B]">
                  <Plus className="mr-2 h-4 w-4" />
                  Add New
                </Button>
              </div>
            </div>
            <Tabs defaultValue="overview" className="space-y-4">
              <TabsList>
                <TabsTrigger value="overview">Overview</TabsTrigger>
                <TabsTrigger value="analytics">Analytics</TabsTrigger>
                <TabsTrigger value="reports">Reports</TabsTrigger>
                <TabsTrigger value="notifications">Notifications</TabsTrigger>
              </TabsList>
              <TabsContent value="overview" className="space-y-4">
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                  <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                      <CardTitle className="text-sm font-medium">Total Revenue</CardTitle>
                      <ShoppingCart className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">$45,231.89</div>
                      <p className="text-xs text-muted-foreground">+20.1% from last month</p>
                    </CardContent>
                  </Card>
                  <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                      <CardTitle className="text-sm font-medium">Subscriptions</CardTitle>
                      <Users className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">+2350</div>
                      <p className="text-xs text-muted-foreground">+180.1% from last month</p>
                    </CardContent>
                  </Card>
                  <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                      <CardTitle className="text-sm font-medium">Sales</CardTitle>
                      <BarChart3 className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">+12,234</div>
                      <p className="text-xs text-muted-foreground">+19% from last month</p>
                    </CardContent>
                  </Card>
                  <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                      <CardTitle className="text-sm font-medium">Active Now</CardTitle>
                      <Clock className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                      <div className="text-2xl font-bold">+573</div>
                      <p className="text-xs text-muted-foreground">+201 since last hour</p>
                    </CardContent>
                  </Card>
                </div>
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
                  <Card className="col-span-4">
                    <CardHeader>
                      <CardTitle>Overview</CardTitle>
                    </CardHeader>
                    <CardContent className="pl-2">
                      <Overview />
                    </CardContent>
                  </Card>
                  <Card className="col-span-3">
                    <CardHeader>
                      <CardTitle>Recent Sales</CardTitle>
                      <CardDescription>You made 265 sales this month.</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <RecentSales />
                    </CardContent>
                  </Card>
                </div>
              </TabsContent>
              <TabsContent value="analytics" className="space-y-4">
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
                  <Card className="col-span-7">
                    <CardHeader>
                      <CardTitle>Analytics</CardTitle>
                      <CardDescription>Detailed analytics and performance metrics.</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="h-[400px] w-full rounded-md border border-dashed flex items-center justify-center text-muted-foreground">
                        Analytics charts and data will be displayed here
                      </div>
                    </CardContent>
                  </Card>
                </div>
              </TabsContent>
              <TabsContent value="reports" className="space-y-4">
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
                  <Card className="col-span-7">
                    <CardHeader>
                      <CardTitle>Reports</CardTitle>
                      <CardDescription>View and download your reports.</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="h-[400px] w-full rounded-md border border-dashed flex items-center justify-center text-muted-foreground">
                        Reports will be displayed here
                      </div>
                    </CardContent>
                  </Card>
                </div>
              </TabsContent>
              <TabsContent value="notifications" className="space-y-4">
                <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
                  <Card className="col-span-7">
                    <CardHeader>
                      <CardTitle>Notifications</CardTitle>
                      <CardDescription>Manage your notification settings.</CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="h-[400px] w-full rounded-md border border-dashed flex items-center justify-center text-muted-foreground">
                        Notification settings will be displayed here
                      </div>
                    </CardContent>
                  </Card>
                </div>
              </TabsContent>
            </Tabs>
          </div>
        </main>
      </div>
    </div>
  )
}
