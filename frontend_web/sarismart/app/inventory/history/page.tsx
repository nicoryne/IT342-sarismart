"use client"

import { useState, useEffect } from "react"
import { Search, Loader2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { useStoresContext } from "@/hooks/use-stores-context"
import { StoreSelector } from "@/components/store-selector"
import { showToast } from "@/components/ui/toast-notification"

// Define a type for transactions
type Transaction = {
  id: string;
  product: string;
  date?: string;
  createdAt?: string;
  store: string;
  store_id?: string | number;
  amount: number;
  status: string;
};

export default function HistoryPage() {
  const [timeRange, setTimeRange] = useState("30d")
  const { selectedStore, filterTransactionsByStore, stores } = useStoresContext()
  const [searchQuery, setSearchQuery] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [metrics, setMetrics] = useState({
    totalTransactions: 0,
    totalValue: 0,
    averageValue: 0,
  })

  // Fetch transactions when selectedStore or timeRange changes
  useEffect(() => {
    const fetchTransactions = async () => {
      setIsLoading(true)
      try {
        // Verify the selected store belongs to the user if it's not "all"
        if (selectedStore !== "all") {
          const storeExists = stores.some((store) => String(store.id) === selectedStore)
          if (!storeExists) {
            console.warn(`Store ${selectedStore} does not belong to the current user`)
            setTransactions([])
            setMetrics({
              totalTransactions: 0,
              totalValue: 0,
              averageValue: 0,
            })
            setIsLoading(false)
            return
          }
        }

        const data = await filterTransactionsByStore(selectedStore)

        // Process the data to ensure we're working with primitive values
        const processedData = Array.isArray(data)
          ? data.map((tx: Transaction) => ({
              id: tx.id?.toString() || "N/A",
              product: typeof tx.product === "string" ? tx.product : "N/A",
              date: tx.date || tx.createdAt || new Date().toISOString(),
              store: typeof tx.store === "string" ? tx.store : "N/A",
              amount: typeof tx.amount === "number" ? tx.amount : 0,
              status: typeof tx.status === "string" ? tx.status : "Unknown",
            }))
          : []

        setTransactions(processedData)

        // Calculate metrics
        const totalValue = processedData.reduce((sum, tx) => sum + (tx.amount || 0), 0)
        setMetrics({
          totalTransactions: processedData.length,
          totalValue,
          averageValue: processedData.length > 0 ? totalValue / processedData.length : 0,
        })
      } catch (error) {
        console.error("Error fetching transactions:", error)
        showToast("Failed to fetch transaction history", "error")
        setTransactions([])
      } finally {
        setIsLoading(false)
      }
    }

    fetchTransactions()
  }, [selectedStore, timeRange, filterTransactionsByStore, stores])

  // Filter transactions based on search query
  const filteredTransactions = transactions.filter(
    (tx) =>
      searchQuery === "" ||
      (tx.product && tx.product.toLowerCase().includes(searchQuery.toLowerCase())) ||
      (tx.id && tx.id.toLowerCase().includes(searchQuery.toLowerCase())),
  )

  // Simple status color function
  const getStatusColor = (status: string) => {
    switch (status) {
      case "Completed":
        return "bg-green-100 text-green-800"
      case "Processing":
        return "bg-blue-100 text-blue-800"
      case "Pending":
        return "bg-yellow-100 text-yellow-800"
      case "Refunded":
        return "bg-red-100 text-red-800"
      default:
        return "bg-gray-100 text-gray-800"
    }
  }

  // Format currency
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
      minimumFractionDigits: 2,
    }).format(value)
  }

  return (
    <main className="flex-1 p-4 md:p-6">
      {/* Page Header */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">Transaction History</h1>
          <p className="text-muted-foreground">View your inventory transactions</p>
        </div>
        <div className="flex flex-wrap gap-2">
          <StoreSelector />
          <Select value={timeRange} onValueChange={setTimeRange}>
            <SelectTrigger className="w-[150px]">
              <SelectValue placeholder="Time period" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="7d">Last 7 days</SelectItem>
              <SelectItem value="30d">Last 30 days</SelectItem>
              <SelectItem value="90d">Last 90 days</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      {/* Search and Filters */}
      <div className="mb-4">
        <div className="relative">
          <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
          <Input
            type="search"
            placeholder="Search transactions..."
            className="pl-8 w-full md:w-[300px]"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      {/* Transactions Table */}
      <Card>
        <CardHeader>
          <CardTitle>Transactions</CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="flex justify-center py-8">
              <Loader2 className="h-8 w-8 animate-spin text-[#008080]" />
            </div>
          ) : (
            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>ID</TableHead>
                    <TableHead>Product</TableHead>
                    <TableHead>Date</TableHead>
                    <TableHead>Store</TableHead>
                    <TableHead className="text-right">Amount</TableHead>
                    <TableHead>Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredTransactions.length > 0 ? (
                    filteredTransactions.map((transaction) => (
                      <TableRow key={transaction.id || Math.random().toString()}>
                        <TableCell className="font-medium">{transaction.id || "N/A"}</TableCell>
                        <TableCell>{transaction.product || "N/A"}</TableCell>
                        <TableCell>
                          {transaction.date ? new Date(transaction.date).toLocaleDateString() : "N/A"}
                        </TableCell>
                        <TableCell>{transaction.store || "N/A"}</TableCell>
                        <TableCell className="text-right">{formatCurrency(transaction.amount || 0)}</TableCell>
                        <TableCell>
                          <Badge className={getStatusColor(transaction.status)}>
                            {transaction.status || "Unknown"}
                          </Badge>
                        </TableCell>
                      </TableRow>
                    ))
                  ) : (
                    <TableRow>
                      <TableCell colSpan={6} className="text-center py-6 text-muted-foreground">
                        No transactions found
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </div>
          )}

          {/* Simple pagination */}
          {filteredTransactions.length > 0 && (
            <div className="flex items-center justify-end space-x-2 py-4">
              <Button variant="outline" size="sm" disabled>
                Previous
              </Button>
              <Button variant="outline" size="sm" disabled>
                Next
              </Button>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Summary Cards */}
      <div className="mt-6 grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Total Transactions</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{metrics.totalTransactions}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Total Value</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{formatCurrency(metrics.totalValue)}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Average Value</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{formatCurrency(metrics.averageValue)}</div>
          </CardContent>
        </Card>
      </div>
    </main>
  )
}
