  "use client"

  import { useState, useEffect } from "react"
  import { Search, Loader2, Calendar, Download, RefreshCw, ShoppingCart, Package } from "lucide-react"

  import { Button } from "@/components/ui/button"
  import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
  import { Input } from "@/components/ui/input"
  import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
  import { Badge } from "@/components/ui/badge"
  import { useStoresContext } from "@/hooks/use-stores-context"
  import { StoreSelector } from "@/components/store-selector"
  import { showToast } from "@/components/ui/toast-notification"
  import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
  import { ScrollArea } from "@/components/ui/scroll-area"
  import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"

  // Define types based on the provided schema
  type Product = {
    id: number
    barcode: string
    name: string
    category: string
    description: string
    price: number
    stock: number
    sold: number
    reorderLevel: number
  }

  type CartItem = {
    id: number
    product: Product
    quantity: number
    subtotal: number
  }

  type Cart = {
    id: number
    cartName: string
    totalPrice: number
    totalItems: number
    cartItems: CartItem[]
    seller?: {
      supabaseUid: string
      email: string
      fullName: string
    }
    store?: {
      id: number
      storeName: string
      location: string
    }
    createdAt?: string // Adding this as it's likely needed for date filtering
  }

  export default function HistoryPage() {
    const [timeRange, setTimeRange] = useState("30d")
    const { selectedStore, stores } = useStoresContext()
    const [searchQuery, setSearchQuery] = useState("")
    const [isLoading, setIsLoading] = useState(false)
    const [isRefreshing, setIsRefreshing] = useState(false)
    const [carts, setCarts] = useState<Cart[]>([])
    const [metrics, setMetrics] = useState({
      totalCarts: 0,
      totalValue: 0,
      averageValue: 0,
      totalItems: 0,
    })
    const [lastUpdated, setLastUpdated] = useState<Date>(new Date())
    const [selectedCart, setSelectedCart] = useState<Cart | null>(null)
    const [isCartDetailsOpen, setIsCartDetailsOpen] = useState(false)

    // Fetch carts when selectedStore or timeRange changes
    useEffect(() => {
      fetchCarts()
    }, [selectedStore, timeRange])

    const fetchCarts = async () => {
      setIsLoading(true)
      try {
        // Get token from local storage
        const token = localStorage.getItem("token")
        if (!token) {
          showToast("You need to be logged in to view cart history", "error")
          setIsLoading(false)
          return
        }

        let url = ""

        // Determine which API endpoint to use based on selection
        if (selectedStore === "all") {
          // Get user ID from token
          const base64Url = token.split(".")[1]
          const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/")
          const jsonPayload = decodeURIComponent(
            window
              .atob(base64)
              .split("")
              .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
              .join(""),
          )
          const payload = JSON.parse(jsonPayload)
          const ownerId = payload.sub

          url = `https://sarismart-backend.onrender.com/api/v1/carts/owner/${ownerId}`
        } else {
          url = `https://sarismart-backend.onrender.com/api/v1/carts/store/${selectedStore}`
        }

        // Add time range filter if needed
        if (timeRange !== "all") {
          const days = Number.parseInt(timeRange.replace("d", ""))
          const fromDate = new Date()
          fromDate.setDate(fromDate.getDate() - days)
          url += `?fromDate=${fromDate.toISOString()}`
        }

        const response = await fetch(url, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        })

        if (!response.ok) {
          throw new Error(`Failed to fetch carts: ${response.status}`)
        }

        const data = await response.json()

        // Ensure we have an array of carts
        const cartsData = Array.isArray(data) ? data : []
        setCarts(cartsData)
        setLastUpdated(new Date())

        // Calculate metrics
        const totalValue = cartsData.reduce((sum, cart) => sum + (cart.totalPrice || 0), 0)
        const totalItems = cartsData.reduce((sum, cart) => sum + (cart.totalItems || 0), 0)

        setMetrics({
          totalCarts: cartsData.length,
          totalValue,
          averageValue: cartsData.length > 0 ? totalValue / cartsData.length : 0,
          totalItems,
        })
      } catch (error) {
        console.error("Error fetching carts:", error)
        showToast("Failed to fetch cart history", "error")
        setCarts([])
      } finally {
        setIsLoading(false)
        setIsRefreshing(false)
      }
    }

    const handleRefresh = () => {
      setIsRefreshing(true)
      fetchCarts()
    }

    // Filter carts based on search query
    const filteredCarts = carts.filter(
      (cart) =>
        searchQuery === "" ||
        (cart.cartName && cart.cartName.toLowerCase().includes(searchQuery.toLowerCase())) ||
        (cart.store?.storeName && cart.store.storeName.toLowerCase().includes(searchQuery.toLowerCase())) ||
        (cart.seller?.fullName && cart.seller.fullName.toLowerCase().includes(searchQuery.toLowerCase())) ||
        (cart.id && cart.id.toString().includes(searchQuery)),
    )

    // Format currency
    const formatCurrency = (value: number) => {
      return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "PHP",
        minimumFractionDigits: 2,
      }).format(value)
    }

    // Format date with time
    const formatDateTime = (dateString?: string) => {
      if (!dateString) return "N/A"

      try {
        const date = new Date(dateString)
        return new Intl.DateTimeFormat("en-US", {
          year: "numeric",
          month: "short",
          day: "numeric",
          hour: "2-digit",
          minute: "2-digit",
        }).format(date)
      } catch (e) {
        return "Invalid date"
      }
    }

    // Open cart details dialog
    const openCartDetails = (cart: Cart) => {
      setSelectedCart(cart)
      setIsCartDetailsOpen(true)
    }

    return (
      <main className="flex-1 p-4 md:p-6 space-y-6">
        {/* Page Header with improved layout */}
        <div className="flex flex-col space-y-4 md:flex-row md:items-center md:justify-between">
          <div>
            <h1 className="text-2xl font-bold tracking-tight">Transaction History</h1>
            <p className="text-muted-foreground">View and manage your sales history across all stores</p>
          </div>
          <div className="flex flex-wrap items-center gap-2">
            <StoreSelector />
            <Button variant="outline" size="sm" onClick={handleRefresh} disabled={isRefreshing} className="h-9">
              {isRefreshing ? <Loader2 className="h-4 w-4 animate-spin mr-1" /> : <RefreshCw className="h-4 w-4 mr-1" />}
              Refresh
            </Button>
          </div>
        </div>

        {/* Metrics Cards with improved design */}
        <div className="grid gap-4 md:grid-cols-4">
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Total Carts</CardTitle>
              <CardDescription>Last updated: {lastUpdated.toLocaleTimeString()}</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{metrics.totalCarts}</div>
              <p className="text-xs text-muted-foreground mt-1">
                For the last {timeRange === "30d" ? "30 days" : timeRange}
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Total Items Sold</CardTitle>
              <CardDescription>All products combined</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{metrics.totalItems}</div>
              <p className="text-xs text-muted-foreground mt-1">Across {metrics.totalCarts} carts</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Total Revenue</CardTitle>
              <CardDescription>All sales combined</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-[#008080]">{formatCurrency(metrics.totalValue)}</div>
              <p className="text-xs text-muted-foreground mt-1">{metrics.totalCarts} transactions</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Average Cart Value</CardTitle>
              <CardDescription>Per transaction</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{formatCurrency(metrics.averageValue)}</div>
              <p className="text-xs text-muted-foreground mt-1">
                {metrics.averageValue > 0 ? "↑" : "↓"} from previous period
              </p>
            </CardContent>
          </Card>
        </div>

        {/* Filters and Search with improved layout */}
        <Card>
          <CardHeader>
            <CardTitle>Cart History</CardTitle>
            <CardDescription>Browse and filter your sales history</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex flex-col space-y-3 md:flex-row md:items-center md:space-y-0 md:space-x-4">
              {/* Search */}
              <div className="relative flex-1">
                <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input
                  type="search"
                  placeholder="Search by cart name, store, or seller..."
                  className="pl-8 w-full"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
              </div>

              {/* Time Range Filter */}
              <div className="flex items-center space-x-2">
                <Popover>
                  <PopoverTrigger asChild>
                    <Button variant="outline" size="sm" className="h-9">
                      <Calendar className="h-4 w-4 mr-2" />
                      {timeRange === "7d"
                        ? "Last 7 days"
                        : timeRange === "30d"
                          ? "Last 30 days"
                          : timeRange === "90d"
                            ? "Last 90 days"
                            : "All time"}
                    </Button>
                  </PopoverTrigger>
                  <PopoverContent className="w-auto p-0" align="end">
                    <div className="p-3 space-y-3">
                      <h4 className="font-medium text-sm">Select time range</h4>
                      <div className="grid grid-cols-2 gap-2">
                        <Button
                          variant={timeRange === "7d" ? "default" : "outline"}
                          size="sm"
                          onClick={() => setTimeRange("7d")}
                        >
                          Last 7 days
                        </Button>
                        <Button
                          variant={timeRange === "30d" ? "default" : "outline"}
                          size="sm"
                          onClick={() => setTimeRange("30d")}
                        >
                          Last 30 days
                        </Button>
                        <Button
                          variant={timeRange === "90d" ? "default" : "outline"}
                          size="sm"
                          onClick={() => setTimeRange("90d")}
                        >
                          Last 90 days
                        </Button>
                        <Button
                          variant={timeRange === "all" ? "default" : "outline"}
                          size="sm"
                          onClick={() => setTimeRange("all")}
                        >
                          All time
                        </Button>
                      </div>
                    </div>
                  </PopoverContent>
                </Popover>
              </div>
            </div>

            {/* Carts Table with improved design */}
            {isLoading ? (
              <div className="flex justify-center py-12">
                <Loader2 className="h-8 w-8 animate-spin text-[#008080]" />
              </div>
            ) : (
              <div className="rounded-md border">
                <ScrollArea className="h-[500px]">
                  <Table>
                    <TableHeader className="bg-muted/50 sticky top-0">
                      <TableRow>
                        <TableHead className="w-[80px]">ID</TableHead>
                        <TableHead>Cart Name</TableHead>
                        <TableHead className="hidden md:table-cell">Store</TableHead>
                        <TableHead className="hidden md:table-cell">Seller</TableHead>
                        <TableHead className="text-center">Items</TableHead>
                        <TableHead className="text-right">Total</TableHead>
                        <TableHead className="w-[100px]">Actions</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {filteredCarts.length > 0 ? (
                        filteredCarts.map((cart) => (
                          <TableRow key={cart.id} className="cursor-pointer hover:bg-muted/50">
                            <TableCell className="font-medium">{cart.id}</TableCell>
                            <TableCell>
                              <div className="font-medium">{cart.cartName || `Cart #${cart.id}`}</div>
                              <div className="text-xs text-muted-foreground md:hidden">
                                {cart.store?.storeName || "Unknown Store"}
                              </div>
                            </TableCell>
                            <TableCell className="hidden md:table-cell">
                              {cart.store?.storeName || "Unknown Store"}
                            </TableCell>
                            <TableCell className="hidden md:table-cell">
                              {cart.seller?.fullName || "Unknown Seller"}
                            </TableCell>
                            <TableCell className="text-center">
                              <Badge variant="outline" className="bg-blue-50 text-blue-700 border-blue-200">
                                {cart.totalItems} {cart.totalItems === 1 ? "item" : "items"}
                              </Badge>
                            </TableCell>
                            <TableCell className="text-right font-medium text-[#008080]">
                              {formatCurrency(cart.totalPrice || 0)}
                            </TableCell>
                            <TableCell>
                              <Button
                                variant="ghost"
                                size="sm"
                                className="h-8 w-8 p-0"
                                onClick={() => openCartDetails(cart)}
                              >
                                <span className="sr-only">View details</span>
                                <Package className="h-4 w-4" />
                              </Button>
                            </TableCell>
                          </TableRow>
                        ))
                      ) : (
                        <TableRow>
                          <TableCell colSpan={7} className="h-24 text-center">
                            <div className="flex flex-col items-center justify-center py-4">
                              <ShoppingCart className="h-8 w-8 text-muted-foreground mb-2" />
                              <p className="text-muted-foreground mb-2">No carts found</p>
                              <p className="text-xs text-muted-foreground">Try adjusting your search or filters</p>
                            </div>
                          </TableCell>
                        </TableRow>
                      )}
                    </TableBody>
                  </Table>
                </ScrollArea>
              </div>
            )}

            {/* Pagination info */}
            {filteredCarts.length > 0 && (
              <div className="flex items-center justify-between py-2">
                <p className="text-sm text-muted-foreground">
                  Showing <span className="font-medium">{filteredCarts.length}</span> of{" "}
                  <span className="font-medium">{carts.length}</span> carts
                </p>
                <div className="flex items-center space-x-2">
                  <Button variant="outline" size="sm" disabled>
                    Previous
                  </Button>
                  <Button variant="outline" size="sm" disabled>
                    Next
                  </Button>
                </div>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Cart Details Dialog */}
        <Dialog open={isCartDetailsOpen} onOpenChange={setIsCartDetailsOpen}>
          <DialogContent className="max-w-3xl">
            <DialogHeader>
              <DialogTitle>Cart Details</DialogTitle>
            </DialogHeader>

            {selectedCart && (
              <div className="space-y-4">
                {/* Cart Info */}
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <h3 className="text-sm font-medium text-muted-foreground">Cart Name</h3>
                    <p className="text-base font-medium">{selectedCart.cartName || `Cart #${selectedCart.id}`}</p>
                  </div>
                  <div>
                    <h3 className="text-sm font-medium text-muted-foreground">Store</h3>
                    <p className="text-base font-medium">{selectedCart.store?.storeName || "Unknown Store"}</p>
                  </div>
                  <div>
                    <h3 className="text-sm font-medium text-muted-foreground">Seller</h3>
                    <p className="text-base font-medium">{selectedCart.seller?.fullName || "Unknown Seller"}</p>
                  </div>
                  <div>
                    <h3 className="text-sm font-medium text-muted-foreground">Date</h3>
                    <p className="text-base font-medium">{formatDateTime(selectedCart.createdAt)}</p>
                  </div>
                </div>

                {/* Cart Items */}
                <div>
                  <h3 className="text-sm font-medium mb-2">Items ({selectedCart.totalItems})</h3>
                  <div className="rounded-md border">
                    <Table>
                      <TableHeader className="bg-muted/50">
                        <TableRow>
                          <TableHead>Product</TableHead>
                          <TableHead className="text-center">Quantity</TableHead>
                          <TableHead className="text-right">Price</TableHead>
                          <TableHead className="text-right">Subtotal</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {selectedCart.cartItems && selectedCart.cartItems.length > 0 ? (
                          selectedCart.cartItems.map((item) => (
                            <TableRow key={item.id}>
                              <TableCell>
                                <div className="font-medium">{item.product.name}</div>
                                <div className="text-xs text-muted-foreground">
                                  {item.product.category} • #{item.product.barcode}
                                </div>
                              </TableCell>
                              <TableCell className="text-center">{item.quantity}</TableCell>
                              <TableCell className="text-right">{formatCurrency(item.product.price)}</TableCell>
                              <TableCell className="text-right font-medium">{formatCurrency(item.subtotal)}</TableCell>
                            </TableRow>
                          ))
                        ) : (
                          <TableRow>
                            <TableCell colSpan={4} className="text-center py-4 text-muted-foreground">
                              No items found in this cart
                            </TableCell>
                          </TableRow>
                        )}
                      </TableBody>
                    </Table>
                  </div>
                </div>

                {/* Cart Summary */}
                <div className="flex justify-between items-center pt-2 border-t">
                  <div>
                    <p className="text-sm text-muted-foreground">Total Items: {selectedCart.totalItems}</p>
                  </div>
                  <div className="text-right">
                    <p className="text-sm text-muted-foreground">Total</p>
                    <p className="text-xl font-bold text-[#008080]">{formatCurrency(selectedCart.totalPrice)}</p>
                  </div>
                </div>
              </div>
            )}
          </DialogContent>
        </Dialog>
      </main>
    )
  }
