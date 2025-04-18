"use client"

import type React from "react"

import { useState } from "react"
import { Download, Filter, Plus, SlidersHorizontal, X } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Separator } from "@/components/ui/separator"
import { Badge } from "@/components/ui/badge"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Label } from "@/components/ui/label"
import { Checkbox } from "@/components/ui/checkbox"
import { useStores } from "@/hooks/use-stores"
import Image from "next/image"

export default function ProductsPage() {
  const [selectedStore, setSelectedStore] = useState("all")
  const [category, setCategory] = useState("all")
  const [isAddProductOpen, setIsAddProductOpen] = useState(false)
  const [newProduct, setNewProduct] = useState({
    name: "",
    sku: "",
    category: "",
    supplier: "",
    price: "",
    stock: "",
    reorderPoint: "",
    description: "",
  })

  const { stores, filterProductsByStore } = useStores()

  // Add this function to handle form input changes
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setNewProduct((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  // Add this function to handle form submission
  const handleAddProduct = (e: React.FormEvent) => {
    e.preventDefault()

    // Create a copy of the product data with an auto-generated SKU
    const productData = {
      ...newProduct,
      sku: "PRD-008", // In a real app, this would be generated on the server
    }

    console.log("New product data:", productData)
    // Here you would typically send the data to your backend

    // Show success message
    const toast = document.createElement("div")
    toast.className = "fixed top-4 right-4 bg-green-600 text-white px-4 py-2 rounded shadow-lg z-50"
    toast.textContent = `Product ${newProduct.name} added successfully`
    document.body.appendChild(toast)
    setTimeout(() => {
      toast.style.opacity = "0"
      toast.style.transition = "opacity 0.5s ease"
      setTimeout(() => document.body.removeChild(toast), 500)
    }, 2000)

    // Reset form and close modal
    setNewProduct({
      name: "",
      sku: "",
      category: "",
      supplier: "",
      price: "",
      stock: "",
      reorderPoint: "",
      description: "",
    })
    setIsAddProductOpen(false)
  }

  // Get inventory data for the selected store
  const storeInventory = filterProductsByStore(selectedStore)

  return (
    <main className="flex-1 p-4 md:p-8">
      <div className="flex flex-col space-y-4 md:flex-row md:items-center md:justify-between md:space-y-0">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Product Inventory Management</h1>
          <p className="text-muted-foreground">Manage your product catalog and inventory across all stores</p>
        </div>
        <div className="flex items-center gap-2">
          <Button className="bg-[#008080] hover:bg-[#005F6B]" onClick={() => setIsAddProductOpen(true)}>
            <Plus className="mr-2 h-4 w-4" />
            Add Product
          </Button>
          <Button variant="outline">
            <Download className="mr-2 h-4 w-4" />
            Export
          </Button>
        </div>

        {/* Custom Modal Implementation */}
        {isAddProductOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
            <div className="relative w-full max-w-[500px] rounded-lg bg-white p-5 shadow-lg">
              <button
                className="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none"
                onClick={() => setIsAddProductOpen(false)}
              >
                <X className="h-4 w-4" />
                <span className="sr-only">Close</span>
              </button>

              <div className="mb-4">
                <h2 className="text-lg font-semibold leading-none tracking-tight">Add New Product</h2>
                <p className="text-sm text-muted-foreground">Fill in the product details below.</p>
              </div>

              <form onSubmit={handleAddProduct}>
                <div className="grid gap-3 py-2">
                  <div className="grid grid-cols-2 gap-3">
                    <div className="space-y-1">
                      <Label htmlFor="name">Product Name</Label>
                      <Input
                        id="name"
                        name="name"
                        placeholder="Enter product name"
                        value={newProduct.name}
                        onChange={handleInputChange}
                        required
                      />
                    </div>
                    <div className="space-y-1">
                      <Label htmlFor="sku">SKU (Auto-generated)</Label>
                      <Input id="sku" name="sku" value="PRD-008" disabled className="bg-gray-100" />
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-3">
                    <div className="space-y-1">
                      <Label htmlFor="category">Category</Label>
                      <Select
                        value={newProduct.category}
                        onValueChange={(value) => setNewProduct((prev) => ({ ...prev, category: value }))}
                      >
                        <SelectTrigger id="category">
                          <SelectValue placeholder="Select category" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="electronics">Electronics</SelectItem>
                          <SelectItem value="clothing">Clothing</SelectItem>
                          <SelectItem value="food">Food & Beverage</SelectItem>
                          <SelectItem value="home">Home Goods</SelectItem>
                          <SelectItem value="other">Other</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                    <div className="space-y-1">
                      <Label htmlFor="supplier">Supplier</Label>
                      <Select
                        value={newProduct.supplier}
                        onValueChange={(value) => setNewProduct((prev) => ({ ...prev, supplier: value }))}
                      >
                        <SelectTrigger id="supplier">
                          <SelectValue placeholder="Select supplier" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="techgadgets">TechGadgets Inc.</SelectItem>
                          <SelectItem value="fashionwear">FashionWear Co.</SelectItem>
                          <SelectItem value="gourmetfoods">GourmetFoods Ltd.</SelectItem>
                          <SelectItem value="homestyle">HomeStyle Co.</SelectItem>
                          <SelectItem value="audiopro">AudioPro Ltd.</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                  </div>
                  <div className="grid grid-cols-3 gap-3">
                    <div className="space-y-1">
                      <Label htmlFor="price">Price ($)</Label>
                      <Input
                        id="price"
                        name="price"
                        type="number"
                        step="0.01"
                        min="0"
                        placeholder="0.00"
                        value={newProduct.price}
                        onChange={handleInputChange}
                        required
                      />
                    </div>
                    <div className="space-y-1">
                      <Label htmlFor="stock">Initial Stock</Label>
                      <Input
                        id="stock"
                        name="stock"
                        type="number"
                        min="0"
                        placeholder="0"
                        value={newProduct.stock}
                        onChange={handleInputChange}
                        required
                      />
                    </div>
                    <div className="space-y-1">
                      <Label htmlFor="reorderPoint">Reorder Point</Label>
                      <Input
                        id="reorderPoint"
                        name="reorderPoint"
                        type="number"
                        min="0"
                        placeholder="0"
                        value={newProduct.reorderPoint}
                        onChange={handleInputChange}
                        required
                      />
                    </div>
                  </div>
                  <div className="space-y-1">
                    <Label htmlFor="image">Product Image</Label>
                    <Input id="image" type="file" className="cursor-pointer" />
                  </div>
                  <div className="space-y-1">
                    <Label>Store Availability</Label>
                    <div className="grid grid-cols-2 gap-1 pt-1 sm:grid-cols-3">
                      {stores.map((store) => (
                        <div key={store.id} className="flex items-center gap-1.5">
                          <Checkbox id={`store-${store.id}`} />
                          <Label htmlFor={`store-${store.id}`} className="text-sm font-normal">
                            {store.name}
                          </Label>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
                <div className="mt-4 flex justify-end gap-2">
                  <Button type="button" variant="outline" onClick={() => setIsAddProductOpen(false)}>
                    Cancel
                  </Button>
                  <Button type="submit" className="bg-[#008080] hover:bg-[#005F6B]">
                    Add Product
                  </Button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>

      <Separator className="my-6" />

      {/* Product Inventory Overview */}
      <div className="mb-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
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
            <div className="text-2xl font-bold">{storeInventory.totalProducts}</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-[#40E0D0]">+12 items</span> from last month
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium">Categories</CardTitle>
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
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
              <line x1="3" y1="9" x2="21" y2="9" />
              <line x1="9" y1="21" x2="9" y2="9" />
            </svg>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{storeInventory.categories}</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-[#40E0D0]">+3 categories</span> from last month
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
            <div className="text-2xl font-bold">{storeInventory.lowStock}</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-red-500">+8 items</span> from last week
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
            <div className="text-2xl font-bold">{storeInventory.inventoryValue}</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-[#40E0D0]">+5.1%</span> from last month
            </p>
          </CardContent>
        </Card>
      </div>

      <div className="flex flex-col space-y-4 sm:flex-row sm:items-center sm:justify-between sm:space-y-0">
        <div className="flex items-center gap-2">
          <div className="relative w-full md:w-[300px]">
            <Filter className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input type="search" placeholder="Search products..." className="w-full pl-8 md:w-[300px]" />
          </div>
          <Button variant="outline" size="icon" className="ml-auto md:ml-0 flex-shrink-0">
            <Filter className="h-4 w-4" />
            <span className="sr-only">Filter</span>
          </Button>
          <Button variant="outline" size="icon" className="flex-shrink-0">
            <SlidersHorizontal className="h-4 w-4" />
            <span className="sr-only">Advanced filters</span>
          </Button>
        </div>

        <div className="flex items-center gap-2">
          <Select value={category} onValueChange={setCategory}>
            <SelectTrigger className="w-[180px]">
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

          <Select defaultValue="all">
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Stock Status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Status</SelectItem>
              <SelectItem value="in-stock">In Stock</SelectItem>
              <SelectItem value="low-stock">Low Stock</SelectItem>
              <SelectItem value="out-of-stock">Out of Stock</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      <Tabs defaultValue="all" className="mt-6">
        <TabsList>
          <TabsTrigger value="all">All Products</TabsTrigger>
          <TabsTrigger value="active">Active</TabsTrigger>
          <TabsTrigger value="low-stock">Low Stock</TabsTrigger>
          <TabsTrigger value="out-of-stock">Out of Stock</TabsTrigger>
        </TabsList>

        <TabsContent value="all" className="mt-4">
          <Card>
            <CardContent className="p-0">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="w-[80px]">SKU</TableHead>
                    <TableHead>Product</TableHead>
                    <TableHead>Category</TableHead>
                    <TableHead>Supplier</TableHead>
                    <TableHead className="text-center">Stock</TableHead>
                    <TableHead className="text-center">Reorder Point</TableHead>
                    <TableHead className="text-right">Price</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {[
                    {
                      sku: "PRD-001",
                      name: "Wireless Headphones",
                      image: "/placeholder.svg?height=40&width=40",
                      category: "Electronics",
                      supplier: "TechGadgets Inc.",
                      stock: 45,
                      reorderPoint: 20,
                      price: "$89.99",
                      status: "In Stock",
                    },
                    {
                      sku: "PRD-002",
                      name: "Smart Watch",
                      image: "/placeholder.svg?height=40&width=40",
                      category: "Electronics",
                      supplier: "TechGadgets Inc.",
                      stock: 18,
                      reorderPoint: 15,
                      price: "$199.99",
                      status: "Low Stock",
                    },
                    {
                      sku: "PRD-003",
                      name: "Cotton T-Shirt",
                      image: "/placeholder.svg?height=40&width=40",
                      category: "Clothing",
                      supplier: "FashionWear Co.",
                      stock: 120,
                      reorderPoint: 30,
                      price: "$24.99",
                      status: "In Stock",
                    },
                    {
                      sku: "PRD-004",
                      name: "Organic Coffee",
                      image: "/placeholder.svg?height=40&width=40",
                      category: "Food & Beverage",
                      supplier: "GourmetFoods Ltd.",
                      stock: 0,
                      reorderPoint: 25,
                      price: "$12.99",
                      status: "Out of Stock",
                    },
                    {
                      sku: "PRD-005",
                      name: "Ceramic Mug Set",
                      image: "/placeholder.svg?height=40&width=40",
                      category: "Home Goods",
                      supplier: "HomeStyle Co.",
                      stock: 32,
                      reorderPoint: 20,
                      price: "$34.99",
                      status: "In Stock",
                    },
                  ].map((product) => (
                    <TableRow key={product.sku}>
                      <TableCell className="font-medium">{product.sku}</TableCell>
                      <TableCell>
                        <div className="flex items-center gap-3">
                          <div className="h-10 w-10 overflow-hidden rounded-md">
                            <Image
                              src={product.image || "/placeholder.svg"}
                              alt={product.name}
                              width={40}
                              height={40}
                              className="h-full w-full object-cover"
                            />
                          </div>
                          <div className="font-medium">{product.name}</div>
                        </div>
                      </TableCell>
                      <TableCell>{product.category}</TableCell>
                      <TableCell>{product.supplier}</TableCell>
                      <TableCell className="text-center">{product.stock}</TableCell>
                      <TableCell className="text-center">{product.reorderPoint}</TableCell>
                      <TableCell className="text-right">{product.price}</TableCell>
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
                        <Button
                          variant="ghost"
                          size="sm"
                          className={product.stock < product.reorderPoint ? "" : "hidden"}
                        >
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

        <TabsContent value="active" className="mt-4">
          <Card>
            <CardContent className="p-6">
              <div className="text-center text-muted-foreground">Active products content</div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="low-stock" className="mt-4">
          <Card>
            <CardContent className="p-6">
              <div className="text-center text-muted-foreground">Low stock products content</div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="out-of-stock" className="mt-4">
          <Card>
            <CardContent className="p-6">
              <div className="text-center text-muted-foreground">Out of stock products content</div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </main>
  )
}
