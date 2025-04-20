"use client"

import type React from "react"

import { useState } from "react"
import { Filter, Plus, X } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Separator } from "@/components/ui/separator"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Label } from "@/components/ui/label"
import { Checkbox } from "@/components/ui/checkbox"
import { useStores } from "@/hooks/use-stores"

export default function ProductsPage() {
  // STEP 1: Set up state for filtering and product management
  const [selectedStore, setSelectedStore] = useState("all") // Currently selected store
  const [category, setCategory] = useState("all") // Selected category filter
  const [isAddProductOpen, setIsAddProductOpen] = useState(false) // Controls the Add Product modal visibility

  // STEP 2: State for the new product form
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

  // STEP 3: Get store data and filtering functions from the custom hook
  const { stores, filterProductsByStore } = useStores()

  // STEP 4: Handle form input changes for the Add Product form
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setNewProduct((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  // STEP 5: Handle form submission for adding a new product
  const handleAddProduct = (e: React.FormEvent) => {
    e.preventDefault()

    // STEP 6: In a real implementation, this would make an API call to create a product in the database
    // Example: const response = await fetch('/api/products', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify(productData)
    // });

    // Create a copy of the product data with an auto-generated SKU
    const productData = {
      ...newProduct,
      sku: "PRD-001", // In a real app, this would be generated on the server
    }

    console.log("New product data:", productData)
    // Here you would typically send the data to your backend

    // STEP 7: Show success message
    const toast = document.createElement("div")
    toast.className = "fixed top-4 right-4 bg-green-600 text-white px-4 py-2 rounded shadow-lg z-50"
    toast.textContent = `Product ${newProduct.name} added successfully`
    document.body.appendChild(toast)
    setTimeout(() => {
      toast.style.opacity = "0"
      toast.style.transition = "opacity 0.5s ease"
      setTimeout(() => document.body.removeChild(toast), 500)
    }, 2000)

    // STEP 8: Reset form and close modal
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

  // STEP 9: Get inventory data for the selected store
  // In a real implementation, this would fetch data from your backend API
  // Example: useEffect(() => { async function fetchData() { const response = await fetch(`/api/stores/${selectedStore}/inventory`); const data = await response.json(); setStoreInventory(data); } fetchData(); }, [selectedStore]);
  const storeInventory = filterProductsByStore(selectedStore)

  return (
    <main className="flex-1 p-4 md:p-8">
      <div className="flex flex-col space-y-4 md:flex-row md:items-center md:justify-between md:space-y-0">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Product Inventory Management</h1>
          <p className="text-muted-foreground">Manage your product catalog and inventory across all stores</p>
        </div>
        <div className="flex items-center gap-2">
          {/* STEP 10: Button to open the Add Product modal */}
          <Button className="bg-[#008080] hover:bg-[#005F6B]" onClick={() => setIsAddProductOpen(true)}>
            <Plus className="mr-2 h-4 w-4" />
            Add Product
          </Button>
        </div>

        {/* STEP 11: Add Product Modal - Only shown when isAddProductOpen is true */}
        {isAddProductOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
            <div className="relative w-full max-w-[500px] rounded-lg bg-white p-5 shadow-lg">
              {/* STEP 12: Close button for the modal */}
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

              {/* STEP 13: Form with onSubmit handler to process the form submission */}
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
                      <Input id="sku" name="sku" value="PRD-001" disabled className="bg-gray-100" />
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-3">
                    <div className="space-y-1">
                      <Label htmlFor="category">Category</Label>
                      {/* STEP 14: Category dropdown */}
                      <Select
                        value={newProduct.category}
                        onValueChange={(value) => setNewProduct((prev) => ({ ...prev, category: value }))}
                      >
                        <SelectTrigger id="category">
                          <SelectValue placeholder="Select category" />
                        </SelectTrigger>
                        <SelectContent>
                          {/* STEP 15: In a real implementation, these would be fetched from an API */}
                          {/* Example: const [categories, setCategories] = useState([]); useEffect(() => { async function fetchCategories() { const response = await fetch('/api/categories'); const data = await response.json(); setCategories(data); } fetchCategories(); }, []); */}
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
                      {/* STEP 16: Supplier dropdown */}
                      <Select
                        value={newProduct.supplier}
                        onValueChange={(value) => setNewProduct((prev) => ({ ...prev, supplier: value }))}
                      >
                        <SelectTrigger id="supplier">
                          <SelectValue placeholder="Select supplier" />
                        </SelectTrigger>
                        <SelectContent>
                          {/* STEP 17: In a real implementation, these would be fetched from an API */}
                          {/* Example: const [suppliers, setSuppliers] = useState([]); useEffect(() => { async function fetchSuppliers() { const response = await fetch('/api/suppliers'); const data = await response.json(); setSuppliers(data); } fetchSuppliers(); }, []); */}
                          <SelectItem value="supplier1">Supplier 1</SelectItem>
                          <SelectItem value="supplier2">Supplier 2</SelectItem>
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
                    {/* STEP 18: Store availability checkboxes */}
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

      {/* STEP 19: Product Inventory Overview Cards */}
      <div className="mb-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {/* STEP 20: Total Products Card */}
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
            {/* STEP 21: Display total products count from backend data */}
            <div className="text-2xl font-bold">{storeInventory.totalProducts}</div>
            <p className="text-xs text-muted-foreground">
              {/* STEP 22: In a real implementation, this would show actual growth data from your backend */}
              <span className="text-[#40E0D0]">+0 items</span> from last month
            </p>
          </CardContent>
        </Card>

        {/* STEP 23: Categories Card */}
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
            {/* STEP 24: Display categories count from backend data */}
            <div className="text-2xl font-bold">{storeInventory.categories}</div>
            <p className="text-xs text-muted-foreground">
              {/* STEP 25: In a real implementation, this would show actual growth data from your backend */}
              <span className="text-[#40E0D0]">+0 categories</span> from last month
            </p>
          </CardContent>
        </Card>

        {/* STEP 26: Low Stock Items Card */}
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
            {/* STEP 27: Display low stock count from backend data */}
            <div className="text-2xl font-bold">{storeInventory.lowStock}</div>
            <p className="text-xs text-muted-foreground">
              {/* STEP 28: In a real implementation, this would show actual growth data from your backend */}
              <span className="text-red-500">+0 items</span> from last week
            </p>
          </CardContent>
        </Card>

        {/* STEP 29: Inventory Value Card */}
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
            {/* STEP 30: Display inventory value from backend data */}
            <div className="text-2xl font-bold">{storeInventory.inventoryValue}</div>
            <p className="text-xs text-muted-foreground">
              {/* STEP 31: In a real implementation, this would show actual growth data from your backend */}
              <span className="text-[#40E0D0]">+0%</span> from last month
            </p>
          </CardContent>
        </Card>
      </div>

      {/* STEP 32: Product filtering controls */}
      <div className="flex flex-col space-y-4 sm:flex-row sm:items-center sm:justify-between sm:space-y-0">
        <div className="flex items-center gap-2">
          {/* STEP 33: Search input for filtering products */}
          <div className="relative w-full md:w-[300px]">
            <Filter className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input type="search" placeholder="Search products..." className="w-full pl-8 md:w-[300px]" />
          </div>
        </div>

        <div className="flex items-center gap-2">
          {/* STEP 34: Category filter dropdown */}
          <Select value={category} onValueChange={setCategory}>
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Category" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Categories</SelectItem>
              {/* STEP 35: In a real implementation, these would be fetched from an API */}
              {/* Example: const [categories, setCategories] = useState([]); useEffect(() => { async function fetchCategories() { const response = await fetch('/api/categories'); const data = await response.json(); setCategories(data); } fetchCategories(); }, []); */}
              <SelectItem value="electronics">Electronics</SelectItem>
              <SelectItem value="clothing">Clothing</SelectItem>
              <SelectItem value="food">Food & Beverage</SelectItem>
              <SelectItem value="home">Home Goods</SelectItem>
            </SelectContent>
          </Select>

          {/* STEP 36: Stock status filter dropdown */}
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

      {/* STEP 37: Product tabs for different views */}
      <Tabs defaultValue="all" className="mt-6">
        <TabsList>
          <TabsTrigger value="all">All Products</TabsTrigger>
          <TabsTrigger value="active">Active</TabsTrigger>
          <TabsTrigger value="low-stock">Low Stock</TabsTrigger>
          <TabsTrigger value="out-of-stock">Out of Stock</TabsTrigger>
        </TabsList>

        {/* STEP 38: All Products Tab */}
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
                  {/* STEP 39: In a real implementation, this would map over products fetched from an API */}
                  {/* Example: const [products, setProducts] = useState([]); useEffect(() => { async function fetchProducts() { const response = await fetch(`/api/stores/${selectedStore}/products`); const data = await response.json(); setProducts(data); } fetchProducts(); }, [selectedStore, category]); */}
                  <TableRow>
                    <TableCell colSpan={9} className="text-center py-8 text-muted-foreground">
                      No products found. Add products to see them here.
                      <div className="mt-2">
                        <Button
                          size="sm"
                          className="bg-[#008080] hover:bg-[#005F6B]"
                          onClick={() => setIsAddProductOpen(true)}
                        >
                          <Plus className="mr-2 h-4 w-4" />
                          Add Product
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        {/* STEP 40: Active Products Tab */}
        <TabsContent value="active" className="mt-4">
          <Card>
            <CardContent className="p-6">
              <div className="text-center text-muted-foreground">
                {/* STEP 41: In a real implementation, this would show active products */}
                {/* Example: const [activeProducts, setActiveProducts] = useState([]); useEffect(() => { async function fetchActiveProducts() { const response = await fetch(`/api/stores/${selectedStore}/products/active`); const data = await response.json(); setActiveProducts(data); } fetchActiveProducts(); }, [selectedStore]); */}
                No active products found.
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* STEP 42: Low Stock Tab */}
        <TabsContent value="low-stock" className="mt-4">
          <Card>
            <CardContent className="p-6">
              <div className="text-center text-muted-foreground">
                {/* STEP 43: In a real implementation, this would show low stock products */}
                {/* Example: const [lowStockProducts, setLowStockProducts] = useState([]); useEffect(() => { async function fetchLowStockProducts() { const response = await fetch(`/api/stores/${selectedStore}/products/low-stock`); const data = await response.json(); setLowStockProducts(data); } fetchLowStockProducts(); }, [selectedStore]); */}
                No low stock products found.
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        {/* STEP 44: Out of Stock Tab */}
        <TabsContent value="out-of-stock" className="mt-4">
          <Card>
            <CardContent className="p-6">
              <div className="text-center text-muted-foreground">
                {/* STEP 45: In a real implementation, this would show out of stock products */}
                {/* Example: const [outOfStockProducts, setOutOfStockProducts] = useState([]); useEffect(() => { async function fetchOutOfStockProducts() { const response = await fetch(`/api/stores/${selectedStore}/products/out-of-stock`); const data = await response.json(); setOutOfStockProducts(data); } fetchOutOfStockProducts(); }, [selectedStore]); */}
                No out of stock products found.
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </main>
  )
}
