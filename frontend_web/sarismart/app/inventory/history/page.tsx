"use client"

import { useState } from "react"
import { ArrowUpDown, Calendar, ChevronDown, Filter } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"
import { useStores } from "@/hooks/use-stores"

export default function HistoryPage() {
  // STEP 1: Set up state for filtering and data display
  const [timeRange, setTimeRange] = useState("30d") // Time range for data filtering
  const [status, setStatus] = useState("all") // Transaction status filter
  const [selectedStore, setSelectedStore] = useState("all") // Currently selected store

  // STEP 2: Get store data and filtering functions from the custom hook
  const { filterTransactionsByStore }: { filterTransactionsByStore: (store: string, transactions: Transaction[]) => Transaction[] } = useStores() // Temporaty Fix because of Error:  const { filterTransactionsByStore } = useStores()

  // STEP 3: In a real implementation, this would fetch transaction data from your backend API
  // Example: const [transactions, setTransactions] = useState([]);
  // useEffect(() => {
  //   async function fetchTransactions() {
  //     const response = await fetch(`/api/stores/${selectedStore}/transactions?timeRange=${timeRange}&status=${status}`);
  //     const data = await response.json();
  //     setTransactions(data);
  //   }
  //   fetchTransactions();
  // }, [selectedStore, timeRange, status]);
  
  interface Transaction {
    id: string
    product: string
    date: string
    customer: string
    amount: number
    type: string
    status: string
  }
  const transactions: Transaction[] = []

  // STEP 4: Filter transactions based on selected store
  const filteredTransactions = filterTransactionsByStore(selectedStore, transactions)

  // STEP 5: Helper function to determine badge color based on transaction status
  const getStatusColor = (status: string) => {
    switch (status) {
      case "Completed":
        return "bg-green-100 text-green-800"
      case "Processing":
        return "bg-blue-100 text-blue-800"
      case "Pending":
        return "bg-yellow-100 text-yellow-800"
      case "Refunded":
        return "bg-orange-100 text-orange-800"
      case "Cancelled":
        return "bg-red-100 text-red-800"
      default:
        return "bg-gray-100 text-gray-800"
    }
  }

  // STEP 6: Helper function to determine badge color based on transaction type
  const getTypeColor = (type: string) => {
    switch (type) {
      case "Purchase":
        return "bg-[#40E0D0]/20 text-[#005F6B]"
      case "Refund":
        return "bg-orange-100 text-orange-800"
      case "Cancellation":
        return "bg-red-100 text-red-800"
      default:
        return "bg-gray-100 text-gray-800"
    }
  }

  return (
    <main className="flex-1 p-4 md:p-8">
      <div className="flex flex-col space-y-4 md:flex-row md:items-center md:justify-between md:space-y-0">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Inventory Transaction History</h1>
          <p className="text-muted-foreground">View and manage your inventory transaction history</p>
        </div>
        <div className="flex items-center gap-2">
          {/* STEP 7: Date range picker for transaction history */}
          <Button variant="outline">
            <Calendar className="mr-2 h-4 w-4" />
            Date Range
          </Button>
        </div>
      </div>

      {/* STEP 8: Transaction History Card */}
      <Card className="mt-6">
        <CardHeader className="pb-1">
          <CardTitle>Transaction History</CardTitle>
          <CardDescription>A complete history of all inventory transactions.</CardDescription>
        </CardHeader>
        <CardContent>
          {/* STEP 9: Transaction filtering controls */}
          <div className="flex flex-col space-y-4 sm:flex-row sm:items-center sm:justify-between sm:space-y-0 py-4">
            <div className="flex items-center gap-2">
              {/* STEP 10: Search input for filtering transactions */}
              <div className="relative w-full md:w-[300px]">
                <Filter className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                <Input type="search" placeholder="Search transactions..." className="w-full pl-8 md:w-[300px]" />
              </div>
            </div>

            <div className="flex items-center gap-2">
              {/* STEP 11: Time range filter dropdown */}
              <Select value={timeRange} onValueChange={setTimeRange}>
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="Time period" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="7d">Last 7 days</SelectItem>
                  <SelectItem value="30d">Last 30 days</SelectItem>
                  <SelectItem value="90d">Last 90 days</SelectItem>
                  <SelectItem value="12m">Last 12 months</SelectItem>
                  <SelectItem value="custom">Custom range</SelectItem>
                </SelectContent>
              </Select>

              {/* STEP 12: Status filter dropdown */}
              <Select value={status} onValueChange={setStatus}>
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="Status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Statuses</SelectItem>
                  <SelectItem value="completed">Completed</SelectItem>
                  <SelectItem value="processing">Processing</SelectItem>
                  <SelectItem value="pending">Pending</SelectItem>
                  <SelectItem value="refunded">Refunded</SelectItem>
                  <SelectItem value="cancelled">Cancelled</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          {/* STEP 13: Transactions table */}
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="w-[100px]">
                    {/* STEP 14: Sortable column headers */}
                    <Button variant="ghost" className="p-0 font-medium">
                      ID
                      <ArrowUpDown className="ml-2 h-4 w-4" />
                    </Button>
                  </TableHead>
                  <TableHead>
                    <Button variant="ghost" className="p-0 font-medium">
                      Product
                      <ArrowUpDown className="ml-2 h-4 w-4" />
                    </Button>
                  </TableHead>
                  <TableHead>
                    <Button variant="ghost" className="p-0 font-medium">
                      Date
                      <ArrowUpDown className="ml-2 h-4 w-4" />
                    </Button>
                  </TableHead>
                  <TableHead>Store</TableHead>
                  <TableHead className="text-right">Amount</TableHead>
                  <TableHead>Type</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {/* STEP 15: Map through transactions to display in table */}
                {/* In a real implementation, this would map over transactions fetched from an API */}
                {filteredTransactions.length > 0 ? (
                  filteredTransactions.map((transaction) => (
                    <TableRow key={transaction.id}>
                      <TableCell className="font-medium">{transaction.id}</TableCell>
                      <TableCell>{transaction.product}</TableCell>
                      <TableCell>{transaction.date}</TableCell>
                      <TableCell>{transaction.customer}</TableCell>
                      <TableCell className="text-right">{transaction.amount}</TableCell>
                      <TableCell>
                        <Badge className={`${getTypeColor(transaction.type)}`}>{transaction.type}</Badge>
                      </TableCell>
                      <TableCell>
                        <Badge className={`${getStatusColor(transaction.status)}`}>{transaction.status}</Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        <Button variant="ghost" size="sm">
                          <ChevronDown className="h-4 w-4" />
                          <span className="sr-only">More</span>
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={8} className="text-center py-8 text-muted-foreground">
                      {/* STEP 16: Empty state message when no transactions are found */}
                      No transactions found for the selected filters.
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </div>

          {/* STEP 17: Pagination controls */}
          <div className="mt-4 flex items-center justify-end space-x-2 py-4">
            <div className="flex-1 text-sm text-muted-foreground">
              Showing <strong>{filteredTransactions.length}</strong> of <strong>0</strong> transactions
            </div>
            <Pagination>
              <PaginationContent>
                <PaginationItem>
                  <PaginationPrevious href="#" />
                </PaginationItem>
                <PaginationItem>
                  <PaginationLink href="#" isActive>
                    1
                  </PaginationLink>
                </PaginationItem>
                <PaginationItem>
                  <PaginationEllipsis />
                </PaginationItem>
                <PaginationItem>
                  <PaginationNext href="#" />
                </PaginationItem>
              </PaginationContent>
            </Pagination>
          </div>
        </CardContent>
      </Card>

      {/* STEP 18: Transaction metrics cards */}
      <div className="mt-8 grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {/* STEP 19: Total Transactions Card */}
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Total Transactions</CardTitle>
          </CardHeader>
          <CardContent>
            {/* STEP 20: Display total transactions count from backend data */}
            <div className="text-2xl font-bold">0</div>
            <p className="text-xs text-muted-foreground">
              {/* STEP 21: In a real implementation, this would show actual growth data from your backend */}
              <span className="text-green-500">+0%</span> from last month
            </p>
          </CardContent>
        </Card>

        {/* STEP 22: Inventory Value Change Card */}
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Inventory Value Change</CardTitle>
          </CardHeader>
          <CardContent>
            {/* STEP 23: Display inventory value change from backend data */}
            <div className="text-2xl font-bold">$0.00</div>
            <p className="text-xs text-muted-foreground">
              {/* STEP 24: In a real implementation, this would show actual growth data from your backend */}
              <span className="text-green-500">+0%</span> from last month
            </p>
          </CardContent>
        </Card>

        {/* STEP 25: Adjustment Rate Card */}
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Adjustment Rate</CardTitle>
          </CardHeader>
          <CardContent>
            {/* STEP 26: Display adjustment rate from backend data */}
            <div className="text-2xl font-bold">0%</div>
            <p className="text-xs text-muted-foreground">
              {/* STEP 27: In a real implementation, this would show actual growth data from your backend */}
              <span className="text-green-500">+0%</span> from last month
            </p>
          </CardContent>
        </Card>

        {/* STEP 28: Average Transaction Value Card */}
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium">Average Transaction Value</CardTitle>
          </CardHeader>
          <CardContent>
            {/* STEP 29: Display average transaction value from backend data */}
            <div className="text-2xl font-bold">$0.00</div>
            <p className="text-xs text-muted-foreground">
              {/* STEP 30: In a real implementation, this would show actual growth data from your backend */}
              <span className="text-green-500">+0%</span> from last month
            </p>
          </CardContent>
        </Card>
      </div>
    </main>
  )
}
