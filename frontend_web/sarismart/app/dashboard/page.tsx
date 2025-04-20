"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Badge } from "@/components/ui/badge"
import { useStoresContext } from "@/hooks/use-stores-context"

export default function DashboardPage() {
  const { stores, selectedStore, filterProductsByStore } = useStoresContext() // Use selectedStore from context

  const storeData = filterProductsByStore(selectedStore) // Fetch data based on selectedStore

  return (
    <main className="flex-1 overflow-auto p-4 md:p-6">
      {/* STEP 4: Store Overview Cards - Display key metrics for the selected store */}
      <div className="mb-6">
        <h2 className="mb-4 text-lg font-medium">
          {selectedStore === "all"
            ? "All Stores Inventory Overview"
            : `${stores.find((s) => String(s.id) === selectedStore)?.name || "Store"} Inventory Overview`}
        </h2>
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {/* STEP 5: Total Products Card */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">Total Products</CardTitle>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="h-4 w-4 text-muted-foreground"
              >
                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
                <polyline points="3.29 7 12 12 20.71 7" />
              </svg>
            </CardHeader>
            <CardContent>
              {/* STEP 6: Display total products count from backend data */}
              <div className="text-2xl font-bold">{storeData.totalProducts}</div>
              <p className="text-xs text-muted-foreground">
                {/* STEP 7: In a real implementation, this would show actual growth data from your backend */}
                <span className="text-[#40E0D0]">+0 items</span> from last month
              </p>
            </CardContent>
          </Card>

          {/* STEP 8: Low Stock Items Card */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">Low Stock Items</CardTitle>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="h-4 w-4 text-muted-foreground"
              >
                <path d="M16 16v1a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h2m5.66 0H14a2 2 0 0 1-2 2v3.34" />
                <path d="M3 15h10" />
                <path d="M16 8l2-3 2 3" />
                <path d="M18 5v12" />
              </svg>
            </CardHeader>
            <CardContent>
              {/* STEP 9: Display low stock count from backend data */}
              <div className="text-2xl font-bold">{storeData.lowStock}</div>
              <p className="text-xs text-muted-foreground">
                {/* STEP 10: In a real implementation, this would show actual growth data from your backend */}
                <span className="text-red-500">+0 items</span> from last week
              </p>
            </CardContent>
          </Card>

          {/* STEP 11: Out of Stock Card */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">Out of Stock</CardTitle>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="h-4 w-4 text-muted-foreground"
              >
                <rect width="20" height="14" x="2" y="5" rx="2" />
                <line x1="2" x2="22" y1="10" y2="10" />
              </svg>
            </CardHeader>
            <CardContent>
              {/* STEP 12: Display out of stock count from backend data */}
              <div className="text-2xl font-bold">0</div>
              <p className="text-xs text-muted-foreground">
                {/* STEP 13: In a real implementation, this would show actual growth data from your backend */}
                <span className="text-red-500">+0 items</span> from yesterday
              </p>
            </CardContent>
          </Card>

          {/* STEP 14: Inventory Value Card */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">Inventory Value</CardTitle>
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="h-4 w-4 text-muted-foreground"
              >
                <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
              </svg>
            </CardHeader>
            <CardContent>
              {/* STEP 15: Display inventory value from backend data */}
              <div className="text-2xl font-bold">{storeData.inventoryValue}</div>
              <p className="text-xs text-muted-foreground">
                {/* STEP 16: In a real implementation, this would show actual growth data from your backend */}
                <span className="text-[#40E0D0]">+0%</span> from last month
              </p>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* STEP 17: Store Inventory Comparison - Only visible when "All Stores" is selected */}
      {selectedStore === "all" && (
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Store Inventory Comparison</CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Store</TableHead>
                  <TableHead>Location</TableHead>
                  <TableHead>Total Products</TableHead>
                  <TableHead>Low Stock</TableHead>
                  <TableHead>Out of Stock</TableHead>
                  <TableHead>Inventory Value</TableHead>
                  <TableHead>Status</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {/* STEP 18: Map through stores to display comparison data */}
                {/* In a real implementation, you would fetch this data from your backend API */}
                {/* Example: const [storeComparisons, setStoreComparisons] = useState([]); useEffect(() => { async function fetchData() { const response = await fetch('/api/stores/comparison'); const data = await response.json(); setStoreComparisons(data); } fetchData(); }, []); */}
                {stores.length > 0 ? (
                  stores.map((store) => (
                    <TableRow key={store.id}>
                      <TableCell className="font-medium">{store.name}</TableCell>
                      <TableCell>{store.location}</TableCell>
                      <TableCell>
                        {/* STEP 19: In a real implementation, this would show actual store data */}0
                      </TableCell>
                      <TableCell>
                        {/* STEP 20: In a real implementation, this would show actual store data */}0
                      </TableCell>
                      <TableCell>
                        {/* STEP 21: In a real implementation, this would show actual store data */}0
                      </TableCell>
                      <TableCell>
                        {/* STEP 22: In a real implementation, this would show actual store data */}
                        $0.00
                      </TableCell>
                      <TableCell>
                        <Badge className="bg-green-100 text-green-800">Active</Badge>
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={7} className="text-center py-8 text-muted-foreground">
                      No stores found. Add stores to see comparison data.
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      )}

      {/* STEP 23: Tabbed content for different inventory views */}
      <Tabs defaultValue="inventory" className="space-y-4">
        <TabsList>
          <TabsTrigger value="inventory">Inventory</TabsTrigger>
          <TabsTrigger value="low-stock">Low Stock</TabsTrigger>
          <TabsTrigger value="orders">Incoming Orders</TabsTrigger>
        </TabsList>

        {/* STEP 24: Inventory Items Tab */}
        <TabsContent value="inventory" className="space-y-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Inventory Items</CardTitle>
              <div className="flex items-center gap-2">
                {/* STEP 25: Search input for filtering products */}
                <Input placeholder="Search products..." className="w-[250px]" />
                {/* STEP 26: Category filter dropdown */}
                <Select defaultValue="all">
                  <SelectTrigger className="w-[150px]">
                    <SelectValue placeholder="Category" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Categories</SelectItem>
                    {/* STEP 27: In a real implementation, these would be fetched from an API */}
                    {/* Example: const [categories, setCategories] = useState([]); useEffect(() => { async function fetchCategories() { const response = await fetch('/api/categories'); const data = await response.json(); setCategories(data); } fetchCategories(); }, []); */}
                    <SelectItem value="electronics">Electronics</SelectItem>
                    <SelectItem value="clothing">Clothing</SelectItem>
                    <SelectItem value="food">Food & Beverage</SelectItem>
                    <SelectItem value="home">Home Goods</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>SKU</TableHead>
                    <TableHead>Product Name</TableHead>
                    <TableHead>Category</TableHead>
                    <TableHead>In Stock</TableHead>
                    <TableHead>Reorder Level</TableHead>
                    <TableHead>Unit Price</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {/* STEP 28: In a real implementation, this would map over products fetched from an API */}
                  {/* Example: const [products, setProducts] = useState([]); useEffect(() => { async function fetchProducts() { const response = await fetch(`/api/stores/${selectedStore}/products`); const data = await response.json(); setProducts(data); } fetchProducts(); }, [selectedStore]); */}
                  <TableRow>
                    <TableCell colSpan={8} className="text-center py-8 text-muted-foreground">
                      No products found. Add products to see them here.
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        {/* STEP 29: Low Stock Tab */}
        <TabsContent value="low-stock" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Low Stock Items</CardTitle>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>SKU</TableHead>
                    <TableHead>Product Name</TableHead>
                    <TableHead>Current Stock</TableHead>
                    <TableHead>Reorder Level</TableHead>
                    <TableHead>Supplier</TableHead>
                    <TableHead>Last Ordered</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {/* STEP 30: In a real implementation, this would map over low stock products fetched from an API */}
                  {/* Example: const [lowStockProducts, setLowStockProducts] = useState([]); useEffect(() => { async function fetchLowStockProducts() { const response = await fetch(`/api/stores/${selectedStore}/products/low-stock`); const data = await response.json(); setLowStockProducts(data); } fetchLowStockProducts(); }, [selectedStore]); */}
                  <TableRow>
                    <TableCell colSpan={7} className="text-center py-8 text-muted-foreground">
                      No low stock items found.
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        {/* STEP 31: Incoming Orders Tab */}
        <TabsContent value="orders" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Incoming Orders</CardTitle>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Order ID</TableHead>
                    <TableHead>Supplier</TableHead>
                    <TableHead>Items</TableHead>
                    <TableHead>Order Date</TableHead>
                    <TableHead>Expected Arrival</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {/* STEP 32: In a real implementation, this would map over orders fetched from an API */}
                  {/* Example: const [orders, setOrders] = useState([]); useEffect(() => { async function fetchOrders() { const response = await fetch(`/api/stores/${selectedStore}/orders/incoming`); const data = await response.json(); setOrders(data); } fetchOrders(); }, [selectedStore]); */}
                  <TableRow>
                    <TableCell colSpan={7} className="text-center py-8 text-muted-foreground">
                      No incoming orders found.
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </main>
  )
}
