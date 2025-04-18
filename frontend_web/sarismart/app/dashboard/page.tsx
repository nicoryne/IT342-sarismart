"use client"

import { useState } from "react"
import { useStores } from "@/hooks/use-stores"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Badge } from "@/components/ui/badge"
import Verification from '@/components/inventory/verification'

export default function DashboardPage() {
  const [timeRange, setTimeRange] = useState("2w")
  const [selectedStore, setSelectedStore] = useState("all")
  const { stores, filterProductsByStore } = useStores()

  // Get data for the selected store
  const storeData = filterProductsByStore(selectedStore)

  return (
    <main className="flex-1 overflow-auto p-4 md:p-6">
      {/* Store Overview Cards */}
      <div className="mb-6">
        <h2 className="mb-4 text-lg font-medium">
          {selectedStore === "all"
            ? "All Stores Inventory Overview"
            : `${stores.find((s) => s.id === selectedStore)?.name} Inventory Overview`}
        </h2>
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
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
              <div className="text-2xl font-bold">{storeData.totalProducts}</div>
              <p className="text-xs text-muted-foreground">
                <span className="text-[#40E0D0]">+12 items</span> from last month
              </p>
            </CardContent>
          </Card>

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
                <path d="M16 16v1a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h2m5.66 0H14a2 2 0 0 1 2 2v3.34" />
                <path d="M3 15h10" />
                <path d="M16 8l2-3 2 3" />
                <path d="M18 5v12" />
              </svg>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{storeData.lowStock}</div>
              <p className="text-xs text-muted-foreground">
                <span className="text-red-500">+8 items</span> from last week
              </p>
            </CardContent>
          </Card>

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
              <div className="text-2xl font-bold">15</div>
              <p className="text-xs text-muted-foreground">
                <span className="text-red-500">+3 items</span> from yesterday
              </p>
            </CardContent>
          </Card>

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
              <div className="text-2xl font-bold">{storeData.inventoryValue}</div>
              <p className="text-xs text-muted-foreground">
                <span className="text-[#40E0D0]">+5.1%</span> from last month
              </p>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Store Inventory Comparison (visible only when "All Stores" is selected) */}
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
                {stores.map((store) => (
                  <TableRow key={store.id}>
                    <TableCell className="font-medium">{store.name}</TableCell>
                    <TableCell>{store.location}</TableCell>
                    <TableCell>{Math.floor(Math.random() * 300) + 200}</TableCell>
                    <TableCell>{Math.floor(Math.random() * 15) + 5}</TableCell>
                    <TableCell>{Math.floor(Math.random() * 5) + 1}</TableCell>
                    <TableCell>${(Math.random() * 300000 + 100000).toFixed(0)}</TableCell>
                    <TableCell>
                      <Badge className="bg-green-100 text-green-800">Active</Badge>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      )}

      <Tabs defaultValue="inventory" className="space-y-4">
        <TabsList>
          <TabsTrigger value="inventory">Inventory</TabsTrigger>
          <TabsTrigger value="low-stock">Low Stock</TabsTrigger>
          <TabsTrigger value="orders">Incoming Orders</TabsTrigger>
          <TabsTrigger value="transfers">Store Transfers</TabsTrigger>
        </TabsList>

        <TabsContent value="inventory" className="space-y-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Inventory Items</CardTitle>
              <div className="flex items-center gap-2">
                <Input placeholder="Search products..." className="w-[250px]" />
                <Select defaultValue="all">
                  <SelectTrigger className="w-[150px]">
                    <SelectValue placeholder="Category" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Categories</SelectItem>
                    <SelectItem value="electronics">Electronics</SelectItem>
                    <SelectItem value="clothing">Clothing</SelectItem>
                    <SelectItem value="food">Food & Beverage</SelectItem>
                    <SelectItem value="home">Home Goods</SelectItem>
                  </SelectContent>
                </Select>
                <Button size="sm">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="24"
                    height="24"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    className="mr-2 h-4 w-4"
                  >
                    <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                    <polyline points="17 8 12 3 7 8" />
                    <line x1="12" x2="12" y1="3" y2="15" />
                  </svg>
                  Export
                </Button>
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
                  {[
                    {
                      sku: "PRD-001",
                      name: "Wireless Headphones",
                      category: "Electronics",
                      stock: 45,
                      reorderLevel: 20,
                      price: "$89.99",
                      status: "In Stock",
                    },
                    {
                      sku: "PRD-002",
                      name: "Smart Watch",
                      category: "Electronics",
                      stock: 18,
                      reorderLevel: 15,
                      price: "$199.99",
                      status: "Low Stock",
                    },
                    {
                      sku: "PRD-003",
                      name: "Cotton T-Shirt",
                      category: "Clothing",
                      stock: 120,
                      reorderLevel: 30,
                      price: "$24.99",
                      status: "In Stock",
                    },
                    {
                      sku: "PRD-004",
                      name: "Organic Coffee",
                      category: "Food & Beverage",
                      stock: 0,
                      reorderLevel: 25,
                      price: "$12.99",
                      status: "Out of Stock",
                    },
                    {
                      sku: "PRD-005",
                      name: "Ceramic Mug Set",
                      category: "Home Goods",
                      stock: 32,
                      reorderLevel: 20,
                      price: "$34.99",
                      status: "In Stock",
                    },
                    {
                      sku: "PRD-006",
                      name: "Bluetooth Speaker",
                      category: "Electronics",
                      stock: 12,
                      reorderLevel: 15,
                      price: "$79.99",
                      status: "Low Stock",
                    },
                    {
                      sku: "PRD-007",
                      name: "Denim Jeans",
                      category: "Clothing",
                      stock: 85,
                      reorderLevel: 25,
                      price: "$59.99",
                      status: "In Stock",
                    },
                  ].map((product) => (
                    <TableRow key={product.sku}>
                      <TableCell className="font-medium">{product.sku}</TableCell>
                      <TableCell>{product.name}</TableCell>
                      <TableCell>{product.category}</TableCell>
                      <TableCell>{product.stock}</TableCell>
                      <TableCell>{product.reorderLevel}</TableCell>
                      <TableCell>{product.price}</TableCell>
                      <TableCell>
                        <Badge
                          className={
                            product.status === "In Stock"
                              ? "bg-green-100 text-green-800"
                              : product.status === "Low Stock"
                                ? "bg-yellow-100 text-yellow-800"
                                : "bg-red-100 text-red-800"
                          }
                        >
                          {product.status}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        <Button variant="ghost" size="sm">
                          Edit
                        </Button>
                        <Button variant="ghost" size="sm">
                          View
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

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
                  {[
                    {
                      sku: "PRD-002",
                      name: "Smart Watch",
                      stock: 18,
                      reorderLevel: 15,
                      supplier: "TechGadgets Inc.",
                      lastOrdered: "2023-04-15",
                    },
                    {
                      sku: "PRD-006",
                      name: "Bluetooth Speaker",
                      stock: 12,
                      reorderLevel: 15,
                      supplier: "AudioPro Ltd.",
                      lastOrdered: "2023-04-10",
                    },
                    {
                      sku: "PRD-009",
                      name: "Wireless Charger",
                      stock: 8,
                      reorderLevel: 10,
                      supplier: "TechGadgets Inc.",
                      lastOrdered: "2023-04-05",
                    },
                    {
                      sku: "PRD-012",
                      name: "Premium Tea Set",
                      stock: 5,
                      reorderLevel: 8,
                      supplier: "HomeStyle Co.",
                      lastOrdered: "2023-04-01",
                    },
                  ].map((product) => (
                    <TableRow key={product.sku}>
                      <TableCell className="font-medium">{product.sku}</TableCell>
                      <TableCell>{product.name}</TableCell>
                      <TableCell>{product.stock}</TableCell>
                      <TableCell>{product.reorderLevel}</TableCell>
                      <TableCell>{product.supplier}</TableCell>
                      <TableCell>{product.lastOrdered}</TableCell>
                      <TableCell className="text-right">
                        <Button size="sm" className="bg-[#008080] hover:bg-[#005F6B]">
                          Reorder
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

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
                  {[
                    {
                      id: "ORD-2023-042",
                      supplier: "TechGadgets Inc.",
                      items: 12,
                      orderDate: "2023-04-18",
                      arrival: "2023-04-25",
                      status: "In Transit",
                    },
                    {
                      id: "ORD-2023-041",
                      supplier: "HomeStyle Co.",
                      items: 8,
                      orderDate: "2023-04-15",
                      arrival: "2023-04-22",
                      status: "Processing",
                    },
                    {
                      id: "ORD-2023-040",
                      supplier: "AudioPro Ltd.",
                      items: 15,
                      orderDate: "2023-04-12",
                      arrival: "2023-04-20",
                      status: "In Transit",
                    },
                  ].map((order) => (
                    <TableRow key={order.id}>
                      <TableCell className="font-medium">{order.id}</TableCell>
                      <TableCell>{order.supplier}</TableCell>
                      <TableCell>{order.items}</TableCell>
                      <TableCell>{order.orderDate}</TableCell>
                      <TableCell>{order.arrival}</TableCell>
                      <TableCell>
                        <Badge
                          className={
                            order.status === "In Transit"
                              ? "bg-blue-100 text-blue-800"
                              : order.status === "Processing"
                                ? "bg-yellow-100 text-yellow-800"
                                : "bg-green-100 text-green-800"
                          }
                        >
                          {order.status}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        <Button variant="ghost" size="sm">
                          Details
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="transfers" className="space-y-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Store Transfers</CardTitle>
              <Button size="sm" className="bg-[#008080] hover:bg-[#005F6B]">
                New Transfer
              </Button>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Transfer ID</TableHead>
                    <TableHead>From Store</TableHead>
                    <TableHead>To Store</TableHead>
                    <TableHead>Items</TableHead>
                    <TableHead>Request Date</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {[
                    {
                      id: "TRF-2023-015",
                      from: "Downtown Branch",
                      to: "Westside Mall",
                      items: 5,
                      date: "2023-04-18",
                      status: "Pending",
                    },
                    {
                      id: "TRF-2023-014",
                      from: "North Point",
                      to: "South Center",
                      items: 8,
                      date: "2023-04-15",
                      status: "In Transit",
                    },
                    {
                      id: "TRF-2023-013",
                      from: "Eastside Plaza",
                      to: "Downtown Branch",
                      items: 3,
                      date: "2023-04-12",
                      status: "Completed",
                    },
                  ].map((transfer) => (
                    <TableRow key={transfer.id}>
                      <TableCell className="font-medium">{transfer.id}</TableCell>
                      <TableCell>{transfer.from}</TableCell>
                      <TableCell>{transfer.to}</TableCell>
                      <TableCell>{transfer.items}</TableCell>
                      <TableCell>{transfer.date}</TableCell>
                      <TableCell>
                        <Badge
                          className={
                            transfer.status === "Pending"
                              ? "bg-yellow-100 text-yellow-800"
                              : transfer.status === "In Transit"
                                ? "bg-blue-100 text-blue-800"
                                : "bg-green-100 text-green-800"
                          }
                        >
                          {transfer.status}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        <Button variant="ghost" size="sm">
                          Details
                        </Button>
                        {transfer.status === "Pending" && (
                          <Button variant="ghost" size="sm">
                            Approve
                          </Button>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </main>
  )
}
