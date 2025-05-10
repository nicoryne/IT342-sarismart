"use client"
import { useEffect, useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Badge } from "@/components/ui/badge"
import { useStoresContext } from "@/hooks/use-stores-context"
import { StoreSelector } from "@/components/store-selector"
import { Loader2, RefreshCw } from "lucide-react"
import { showToast } from "@/components/ui/toast-notification"
import { Button } from "@/components/ui/button"

// Define a type for the product
type Product = {
  id: string
  name: string
  price: number
  stock: number
  reorderLevel: number
  barcode?: string
  category?: string
  store_id?: string // Add store_id
  store_name?: string // Add store_name
}

export default function DashboardPage() {
  const { stores, selectedStore, setSelectedStore, filterProductsByStore, isLoading } = useStoresContext()

  const [storeData, setStoreData] = useState({
    totalProducts: 0,
    lowStock: 0,
    inventoryValue: 0,
  })
  const [products, setProducts] = useState<Product[]>([])
  const [loadingProducts, setLoadingProducts] = useState(false)
  const [lowStockItems, setLowStockItems] = useState([])
  const [outOfStockCount, setOutOfStockCount] = useState(0)
  const [isRefreshing, setIsRefreshing] = useState(false)

  // Fetch store metrics when selectedStore changes
  useEffect(() => {
    setStoreData(filterProductsByStore(selectedStore))
    fetchProducts()
  }, [selectedStore, filterProductsByStore])

  // Fetch products for the selected store
  const fetchProducts = async () => {
    setLoadingProducts(true)
    try {
      const token = localStorage.getItem("token")
      if (!token) {
        showToast("Authentication token not found", "error")
        return
      }

      let allProducts = []

      if (selectedStore === "all") {
        // If "all" is selected, fetch products from each of the user's stores
        if (stores.length === 0) {
          setProducts([])
          setLoadingProducts(false)
          return
        }

        // Create an array of promises to fetch products from each store
        const productPromises = stores.map((store) =>
          fetch(`https://sarismart-backend.onrender.com/api/v1/stores/${store.id}/products`, {
            method: "GET",
            headers: {
              Authorization: `Bearer ${token}`,
            },
          })
            .then((response) => {
              if (!response.ok) {
                throw new Error(`Failed to fetch products for store ${store.id}: ${response.status}`)
              }
              return response.json()
            })
            .then((storeProducts: Product[]) => {
              // Add store information to each product
              return storeProducts.map((product: Product) => ({
                ...product,
                store_id: store.id,
                store_name: store.name,
              }))
            })
            .catch((error) => {
              console.error(`Error fetching products for store ${store.id}:`, error)
              return [] // Return empty array on error for this store
            }),
        )

        // Wait for all product fetches to complete
        const productsArrays = await Promise.all(productPromises)

        // Combine all products into a single array
        allProducts = productsArrays.flat()
      } else {
        // Fetch products for the specific selected store
        const response = await fetch(`https://sarismart-backend.onrender.com/api/v1/stores/${selectedStore}/products`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })

        if (!response.ok) {
          throw new Error(`Failed to fetch products: ${response.status}`)
        }

        const storeProducts = await response.json()

        // Add store information to each product
        const storeName = stores.find((s) => String(s.id) === selectedStore)?.name || "Unknown Store"
        allProducts = storeProducts.map((product: Product) => ({
          ...product,
          store_id: selectedStore,
          store_name: storeName,
        }))
      }

      setProducts(allProducts)

      // Calculate metrics directly from the fetched data
      const outOfStock = allProducts.filter((product: Product) => product.stock <= 0).length
      setOutOfStockCount(outOfStock)

      // Filter low stock items
      const lowStock = allProducts.filter(
        (product: Product) => product.stock <= product.reorderLevel && product.stock > 0,
      )
      setLowStockItems(lowStock)

      // Calculate inventory value
      const inventoryValue = allProducts.reduce(
        (sum: number, product: Product) => sum + Number(product.price) * Number(product.stock),
        0,
      )

      // Update store data with real values
      setStoreData({
        totalProducts: allProducts.length,
        lowStock: lowStock.length,
        inventoryValue: inventoryValue,
      })
    } catch (error) {
      console.error("Error fetching products:", error)
      showToast(`Failed to fetch products: ${error instanceof Error ? error.message : "Unknown error"}`, "error")
    } finally {
      setLoadingProducts(false)
      setIsRefreshing(false)
    }
  }

  // Handle manual refresh
  const handleRefresh = () => {
    setIsRefreshing(true)
    showToast("Refreshing dashboard data...", "info")
    fetchProducts()
  }

  // Format currency
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "PHP",
      minimumFractionDigits: 2,
    }).format(value)
  }

  return (
    <main className="flex-1 overflow-auto p-4 md:p-6">
      <div className="mb-6 flex items-center justify-between">
        <StoreSelector />
        <Button
          variant="outline"
          size="icon"
          onClick={handleRefresh}
          disabled={isLoading || loadingProducts || isRefreshing}
          title="Refresh dashboard"
        >
          {isLoading || loadingProducts || isRefreshing ? (
            <Loader2 className="h-4 w-4 animate-spin" />
          ) : (
            <RefreshCw className="h-4 w-4" />
          )}
        </Button>
      </div>

      <div className="mb-6">
        <h2 className="mb-4 text-lg font-medium">
          {selectedStore === "all"
            ? "All Stores Inventory Overview"
            : `${stores.find((s) => String(s.id) === selectedStore)?.name || "Store"} Inventory Overview`}
        </h2>

        {isLoading || loadingProducts ? (
          <div className="flex justify-center py-8">
            <Loader2 className="h-8 w-8 animate-spin text-[#008080]" />
          </div>
        ) : (
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
                <div className="text-2xl font-bold">{outOfStockCount}</div>
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
                <div className="text-2xl font-bold">{formatCurrency(storeData.inventoryValue)}</div>
              </CardContent>
            </Card>
          </div>
        )}
      </div>

      {selectedStore === "all" && (
        <Card className="mb-6">
          <CardHeader>
            <CardTitle>Store Inventory Comparison</CardTitle>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="flex justify-center py-8">
                <Loader2 className="h-8 w-8 animate-spin text-[#008080]" />
              </div>
            ) : (
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
                  {stores.length > 0 ? (
                    stores.map((store) => {
                      const storeMetrics = filterProductsByStore(String(store.id))
                      const storeProducts = products.filter((p) => p.store_id === store.id)
                      const outOfStockCount = storeProducts.filter((p) => p.stock <= 0).length

                      return (
                        <TableRow key={store.id}>
                          <TableCell className="font-medium">{store.name}</TableCell>
                          <TableCell>{store.location}</TableCell>
                          <TableCell>{storeMetrics.totalProducts}</TableCell>
                          <TableCell>{storeMetrics.lowStock}</TableCell>
                          <TableCell>{outOfStockCount}</TableCell>
                          <TableCell>{formatCurrency(storeMetrics.inventoryValue)}</TableCell>
                          <TableCell>
                            <Badge className="bg-green-100 text-green-800">Active</Badge>
                          </TableCell>
                        </TableRow>
                      )
                    })
                  ) : (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8 text-muted-foreground">
                        No stores found. Add stores to see comparison data.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>
      )}

      <Tabs defaultValue="all" className="space-y-4">
        <TabsList>
          <TabsTrigger value="all">All Products</TabsTrigger>
          <TabsTrigger value="in-stock">In Stock</TabsTrigger>
          <TabsTrigger value="low-stock">Low Stock</TabsTrigger>
          <TabsTrigger value="out-of-stock">Out of Stock</TabsTrigger>
        </TabsList>

        <TabsContent value="all" className="mt-4">
          <Card>
            <CardContent className="p-0">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Product</TableHead>
                    <TableHead>Category</TableHead>
                    <TableHead className="text-center">Stock</TableHead>
                    <TableHead className="text-center">Reorder Level</TableHead>
                    <TableHead className="text-right">Price</TableHead>
                    <TableHead>Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {loadingProducts ? (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8">
                        Loading products...
                      </TableCell>
                    </TableRow>
                  ) : products.length > 0 ? (
                    products.map((product: any) => (
                      <TableRow key={product.id}>
                        <TableCell>
                          <div className="font-medium">{product.name}</div>
                          {product.barcode && (
                            <div className="text-xs text-muted-foreground">Barcode: {product.barcode}</div>
                          )}
                          {selectedStore === "all" && (
                            <div className="text-xs text-muted-foreground">
                              Store: {product.store_name || `Store #${product.store_id}`}
                            </div>
                          )}
                        </TableCell>
                        <TableCell>{product.category}</TableCell>
                        <TableCell className="text-center">{product.stock}</TableCell>
                        <TableCell className="text-center">{product.reorderLevel}</TableCell>
                        <TableCell className="text-right">₱{Number.parseFloat(product.price).toFixed(2)}</TableCell>
                        <TableCell>
                          {product.stock <= 0 ? (
                            <span className="inline-flex items-center rounded-full bg-red-100 px-2.5 py-0.5 text-xs font-medium text-red-800">
                              Out of stock
                            </span>
                          ) : product.stock <= product.reorderLevel ? (
                            <span className="inline-flex items-center rounded-full bg-yellow-100 px-2.5 py-0.5 text-xs font-medium text-yellow-800">
                              Low stock
                            </span>
                          ) : (
                            <span className="inline-flex items-center rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-medium text-green-800">
                              In stock
                            </span>
                          )}
                        </TableCell>
                      </TableRow>
                    ))
                  ) : (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8 text-muted-foreground">
                        No products found.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="in-stock" className="mt-4">
          <Card>
            <CardContent className="p-0">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Product</TableHead>
                    <TableHead>Category</TableHead>
                    <TableHead className="text-center">Stock</TableHead>
                    <TableHead className="text-center">Reorder Level</TableHead>
                    <TableHead className="text-right">Price</TableHead>
                    <TableHead>Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {loadingProducts ? (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8">
                        Loading products...
                      </TableCell>
                    </TableRow>
                  ) : products.filter((p) => p.stock > p.reorderLevel).length > 0 ? (
                    products
                      .filter((p) => p.stock > p.reorderLevel)
                      .map((product: any) => (
                        <TableRow key={product.id}>
                          <TableCell>
                            <div className="font-medium">{product.name}</div>
                            {product.barcode && (
                              <div className="text-xs text-muted-foreground">Barcode: {product.barcode}</div>
                            )}
                            {selectedStore === "all" && (
                              <div className="text-xs text-muted-foreground">
                                Store: {product.store_name || `Store #${product.store_id}`}
                              </div>
                            )}
                          </TableCell>
                          <TableCell>{product.category}</TableCell>
                          <TableCell className="text-center">{product.stock}</TableCell>
                          <TableCell className="text-center">{product.reorderLevel}</TableCell>
                          <TableCell className="text-right">₱{Number.parseFloat(product.price).toFixed(2)}</TableCell>
                          <TableCell>
                            <span className="inline-flex items-center rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-medium text-green-800">
                              In stock
                            </span>
                          </TableCell>
                        </TableRow>
                      ))
                  ) : (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8 text-muted-foreground">
                        No in-stock products found.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="low-stock" className="mt-4">
          <Card>
            <CardContent className="p-0">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Product</TableHead>
                    <TableHead>Category</TableHead>
                    <TableHead className="text-center">Stock</TableHead>
                    <TableHead className="text-center">Reorder Level</TableHead>
                    <TableHead className="text-right">Price</TableHead>
                    <TableHead>Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {loadingProducts ? (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8">
                        Loading products...
                      </TableCell>
                    </TableRow>
                  ) : lowStockItems.length > 0 ? (
                    lowStockItems.map((product: any) => (
                      <TableRow key={product.id}>
                        <TableCell>
                          <div className="font-medium">{product.name}</div>
                          {product.barcode && (
                            <div className="text-xs text-muted-foreground">Barcode: {product.barcode}</div>
                          )}
                          {selectedStore === "all" && (
                            <div className="text-xs text-muted-foreground">
                              Store: {product.store_name || `Store #${product.store_id}`}
                            </div>
                          )}
                        </TableCell>
                        <TableCell>{product.category}</TableCell>
                        <TableCell className="text-center">{product.stock}</TableCell>
                        <TableCell className="text-center">{product.reorderLevel}</TableCell>
                        <TableCell className="text-right">₱{Number.parseFloat(product.price).toFixed(2)}</TableCell>
                        <TableCell>
                          <span className="inline-flex items-center rounded-full bg-yellow-100 px-2.5 py-0.5 text-xs font-medium text-yellow-800">
                            Low stock
                          </span>
                        </TableCell>
                      </TableRow>
                    ))
                  ) : (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8 text-muted-foreground">
                        No low stock products found.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="out-of-stock" className="mt-4">
          <Card>
            <CardContent className="p-0">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Product</TableHead>
                    <TableHead>Category</TableHead>
                    <TableHead className="text-center">Stock</TableHead>
                    <TableHead className="text-center">Reorder Level</TableHead>
                    <TableHead className="text-right">Price</TableHead>
                    <TableHead>Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {loadingProducts ? (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8">
                        Loading products...
                      </TableCell>
                    </TableRow>
                  ) : products.filter((p) => p.stock <= 0).length > 0 ? (
                    products
                      .filter((p) => p.stock <= 0)
                      .map((product: any) => (
                        <TableRow key={product.id}>
                          <TableCell>
                            <div className="font-medium">{product.name}</div>
                            {product.barcode && (
                              <div className="text-xs text-muted-foreground">Barcode: {product.barcode}</div>
                            )}
                            {selectedStore === "all" && (
                              <div className="text-xs text-muted-foreground">
                                Store: {product.store_name || `Store #${product.store_id}`}
                              </div>
                            )}
                          </TableCell>
                          <TableCell>{product.category}</TableCell>
                          <TableCell className="text-center">{product.stock}</TableCell>
                          <TableCell className="text-center">{product.reorderLevel}</TableCell>
                          <TableCell className="text-right">₱{Number.parseFloat(product.price).toFixed(2)}</TableCell>
                          <TableCell>
                            <span className="inline-flex items-center rounded-full bg-red-100 px-2.5 py-0.5 text-xs font-medium text-red-800">
                              Out of stock
                            </span>
                          </TableCell>
                        </TableRow>
                      ))
                  ) : (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8 text-muted-foreground">
                        No out of stock products found.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </main>
  )
}
