"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { Filter, Plus, X, AlertCircle } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Separator } from "@/components/ui/separator"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Label } from "@/components/ui/label"
import { useStoresContext } from "@/hooks/use-stores-context"
import { StoreSelector } from "@/components/store-selector"
import { showToast } from "@/components/ui/toast-notification"
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"

export default function ProductsPage() {
  const { stores, selectedStore, setSelectedStore, filterProductsByStore, addStore, updateStore, deleteStore } =
    useStoresContext()
  const [category, setCategory] = useState("all")
  const [isAddProductOpen, setIsAddProductOpen] = useState(false)

  const [isAddStoreOpen, setIsAddStoreOpen] = useState(false)
  const [newStore, setNewStore] = useState({
    name: "",
    location: "",
  })
  const [isUpdateStoreOpen, setIsUpdateStoreOpen] = useState(false)
  const [storeToUpdate, setStoreToUpdate] = useState<{ id: string | number; name: string; location: string } | null>(
    null,
  )
  const [isDeleteStoreOpen, setIsDeleteStoreOpen] = useState(false)
  const [storeToDelete, setStoreToDelete] = useState<{ id: string | number; name: string } | null>(null)

  const [newProduct, setNewProduct] = useState({
    name: "",
    category: "",
    price: "",
    stock: "",
    reorderLevel: "",
    description: "",
    barcode: "",
  })

  // Add these state variables after the other state declarations
  const [isEditProductOpen, setIsEditProductOpen] = useState(false)
  const [productToEdit, setProductToEdit] = useState<any>(null)
  const [originalStock, setOriginalStock] = useState<number>(0)

  const [products, setProducts] = useState([])
  const [filteredProducts, setFilteredProducts] = useState<Product[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)
  const [formSuccess, setFormSuccess] = useState<string | null>(null)
  const [activeTab, setActiveTab] = useState("all")

  const [inventoryMetrics, setInventoryMetrics] = useState({
    totalProducts: 0,
    categories: 0,
    lowStock: 0,
    inventoryValue: 0,
  })

  // Define a type for the product if not already defined
  type Product = {
    id: string | number
    name: string
    category: string
    price: number
    stock: number
    reorderLevel: number
    barcode?: string
    description?: string
    store_id: string | number // Added store_id
    store_name?: string // Added store_name
  }

  const [searchQuery, setSearchQuery] = useState("")

  const fetchProducts = async () => {
    setIsLoading(true)
    try {
      const token = localStorage.getItem("token")
      if (!token) {
        showToast("Authentication token not found", "error")
        return
      }

      let allProducts = []

      if (selectedStore === "all") {
        // Fetch products from each of the user's stores and combine them
        for (const store of stores) {
          try {
            const storeResponse = await fetch(
              `https://sarismart-backend.onrender.com/api/v1/stores/${store.id}/products`,
              {
                method: "GET",
                headers: {
                  Authorization: `Bearer ${token}`,
                },
              },
            )

            if (!storeResponse.ok) {
              console.error(`Failed to fetch products for store ${store.id}: ${storeResponse.status}`)
              continue
            }

            const storeProducts = await storeResponse.json()
            // Add store information to each product
            const productsWithStore = storeProducts.map((product: Product) => ({
              ...product,
              store_id: store.id,
              store_name: store.name,
            }))
            allProducts.push(...productsWithStore)
          } catch (error) {
            console.error(`Error fetching products for store ${store.id}:`, error)
          }
        }
      } else {
        // If a specific store is selected
        const storeInfo = stores.find((s) => s.id.toString() === selectedStore.toString())
        if (!storeInfo) {
          showToast("Store not found", "error")
          setIsLoading(false)
          return
        }

        const response = await fetch(`https://sarismart-backend.onrender.com/api/v1/stores/${selectedStore}/products`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })

        if (!response.ok) {
          throw new Error("Failed to fetch products")
        }

        const data = await response.json()
        // Add store information to each product
        allProducts = data.map((product: Product) => ({
          ...product,
          store_id: storeInfo.id,
          store_name: storeInfo.name,
        }))
      }

      setProducts(allProducts)
      filterProductsByTab(allProducts, activeTab)
      calculateInventoryMetrics(allProducts)
    } catch (error) {
      console.error("Error fetching products:", error)
      showToast(`Failed to fetch products: ${error instanceof Error ? error.message : "Unknown error"}`, "error")
      setProducts([])
      setFilteredProducts([])
    } finally {
      setIsLoading(false)
    }
  }

  const filterProductsByTab = (productsList: Product[], tab: string) => {
    // First filter by search query if one exists
    let filtered = productsList
    if (searchQuery.trim() !== "") {
      const query = searchQuery.toLowerCase()
      filtered = productsList.filter(
        (product) =>
          product.name.toLowerCase().includes(query) ||
          product.category.toLowerCase().includes(query) ||
          (product.barcode && product.barcode.toLowerCase().includes(query)) ||
          (product.description && product.description.toLowerCase().includes(query)),
      )
    }

    // Then filter by tab
    switch (tab) {
      case "in-stock":
        filtered = filtered.filter((product) => product.stock > product.reorderLevel)
        break
      case "low-stock":
        filtered = filtered.filter((product) => product.stock <= product.reorderLevel && product.stock > 0)
        break
      case "out-of-stock":
        filtered = filtered.filter((product) => product.stock <= 0)
        break
      default:
        // "all" tab - no additional filtering needed
        break
    }

    setFilteredProducts(filtered)
  }

  const calculateInventoryMetrics = (productData: Product[]) => {
    const uniqueCategories = new Set(productData.map((product) => product.category))
    const lowStockItems = productData.filter((product) => product.stock <= product.reorderLevel && product.stock > 0)
    const totalValue = productData.reduce((sum, product) => sum + Number(product.price) * Number(product.stock), 0)

    // Explicitly type storeMetrics
    const storeMetrics: Record<
      string | number,
      { totalProducts: number; outOfStock: number; lowStock: number; value: number }
    > = {}

    stores.forEach((store) => {
      const storeProducts = productData.filter((product) => product.store_id.toString() === store.id.toString())
      const outOfStock = storeProducts.filter((product) => product.stock <= 0).length
      const lowStock = storeProducts.filter(
        (product) => product.stock <= product.reorderLevel && product.stock > 0,
      ).length
      const storeValue = storeProducts.reduce((sum, product) => sum + Number(product.price) * Number(product.stock), 0)

      storeMetrics[store.id] = {
        totalProducts: storeProducts.length,
        outOfStock,
        lowStock,
        value: storeValue,
      }
    })

    setInventoryMetrics({
      totalProducts: productData.length,
      categories: uniqueCategories.size,
      lowStock: lowStockItems.length,
      inventoryValue: totalValue,
    })
  }

  useEffect(() => {
    fetchProducts()
  }, [selectedStore])

  useEffect(() => {
    filterProductsByTab(products, activeTab)
  }, [activeTab, category, searchQuery, products])

  const handleTabChange = (value: string) => {
    setActiveTab(value)
    filterProductsByTab(products, value)
  }

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value)
  }

  // Add barcode validation function after the other validation functions
  const validateBarcode = (barcode: string) => {
    if (!barcode) return true // Barcode is optional

    // Common barcode formats:
    // EAN-13: 13 digits
    // UPC-A: 12 digits
    // Code-39: Variable length, alphanumeric
    // Code-128: Variable length, full ASCII

    // For simplicity, we'll validate that it's either:
    // 1. A numeric barcode of common lengths (8, 12, 13, 14 digits)
    // 2. An alphanumeric code with reasonable length (up to 30 chars)

    const numericBarcodeRegex = /^\d{8}$|^\d{12,14}$/
    const alphanumericBarcodeRegex = /^[A-Z0-9-]{1,30}$/i

    return numericBarcodeRegex.test(barcode) || alphanumericBarcodeRegex.test(barcode)
  }

  // Add barcode error state
  const [barcodeError, setBarcodeError] = useState<string | null>(null)

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target

    if (name === "barcode") {
      setBarcodeError(null)
      if (value && !validateBarcode(value)) {
        setBarcodeError("Please enter a valid barcode format")
      }
    }

    setNewProduct((prev) => ({
      ...prev,
      [name]: value,
    }))
    setFormError(null)
  }

  const handleStoreInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setNewStore((prev) => ({ ...prev, [name]: value }))
  }

  const handleAddStore = (e: React.FormEvent) => {
    e.preventDefault()
    addStore(newStore)

    const toast = document.createElement("div")
    toast.className = "fixed top-4 right-4 bg-green-600 text-white px-4 py-2 rounded shadow-lg z-50"
    toast.textContent = `Store "${newStore.name}" added successfully`
    document.body.appendChild(toast)
    setTimeout(() => {
      toast.style.opacity = "0"
      toast.style.transition = "opacity 0.5s ease"
      setTimeout(() => document.body.removeChild(toast), 500)
    }, 2000)

    setNewStore({ name: "", location: "" })
    setIsAddStoreOpen(false)
  }

  const handleOpenUpdateModal = (store: { id: string | number; name: string; location: string }) => {
    setStoreToUpdate(store)
    setIsUpdateStoreOpen(true)
  }

  const handleUpdateStore = (e: React.FormEvent) => {
    e.preventDefault()
    if (storeToUpdate) {
      updateStore(storeToUpdate.id, { name: storeToUpdate.name, location: storeToUpdate.location })
      setIsUpdateStoreOpen(false)
    }
  }

  const handleOpenDeleteModal = (store: { id: string | number; name: string }) => {
    setStoreToDelete(store)
    setIsDeleteStoreOpen(true)
  }

  const handleConfirmDelete = () => {
    if (storeToDelete) {
      deleteStore(storeToDelete.id)
      setIsDeleteStoreOpen(false)
    }
  }

  const validateForm = () => {
    if (!newProduct.name.trim()) {
      setFormError("Product name is required")
      return false
    }
    if (!newProduct.category) {
      setFormError("Category is required")
      return false
    }
    if (!newProduct.price || isNaN(Number(newProduct.price)) || Number(newProduct.price) <= 0) {
      setFormError("Price must be a positive number")
      return false
    }
    if (!newProduct.stock || isNaN(Number(newProduct.stock)) || Number(newProduct.stock) < 0) {
      setFormError("Stock must be a non-negative number")
      return false
    }
    if (!newProduct.reorderLevel || isNaN(Number(newProduct.reorderLevel)) || Number(newProduct.reorderLevel) < 0) {
      setFormError("Reorder level must be a non-negative number")
      return false
    }
    if (newProduct.barcode && !validateBarcode(newProduct.barcode)) {
      setFormError("Please enter a valid barcode format")
      return false
    }
    return true
  }

  const handleAddProduct = async (e: React.FormEvent) => {
    e.preventDefault()

    setFormError(null)
    setFormSuccess(null)

    if (selectedStore === "all") {
      setFormError("Please select a specific store to add a product")
      return
    }

    if (!validateForm()) {
      return
    }

    setIsSubmitting(true)

    try {
      const token = localStorage.getItem("token")
      if (!token) {
        setFormError("Authentication token not found. Please log in again.")
        return
      }

      const productData = {
        name: newProduct.name,
        category: newProduct.category,
        price: Number.parseFloat(newProduct.price),
        stock: Number.parseInt(newProduct.stock),
        reorderLevel: Number.parseInt(newProduct.reorderLevel),
        description: newProduct.description || "",
        barcode: newProduct.barcode || null,
        sold: 0,
        store_id: Number.parseInt(selectedStore),
      }

      console.log("Sending product data:", JSON.stringify(productData))

      const response = await fetch(`https://sarismart-backend.onrender.com/api/v1/stores/${selectedStore}/products`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(productData),
      })

      const responseData = await response.json()

      if (!response.ok) {
        throw new Error(responseData.message || responseData.error || "Failed to add product")
      }

      setFormSuccess(`Product ${newProduct.name} added successfully!`)
      showToast(`Product ${newProduct.name} added successfully`, "success")

      setTimeout(() => {
        setNewProduct({
          name: "",
          category: "",
          price: "",
          stock: "",
          reorderLevel: "",
          description: "",
          barcode: "",
        })
        setIsAddProductOpen(false)
        setFormSuccess(null)

        fetchProducts()
      }, 1500)
    } catch (error) {
      console.error("Error adding product:", error)
      const errorMessage = error instanceof Error ? error.message : "Unknown error occurred"
      setFormError(`Failed to add product: ${errorMessage}`)
      showToast(`Failed to add product: ${errorMessage}`, "error")
    } finally {
      setIsSubmitting(false)
    }
  }

  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false)
  const [productToDelete, setProductToDelete] = useState<any>(null)

  // Add this function to handle opening the edit modal
  const handleEditProduct = (product: any) => {
    if (selectedStore === "all" && !isUserProduct(product)) {
      showToast("You can only edit products from your own stores", "error")
      return
    }

    setProductToEdit({
      ...product,
      price: Number.parseFloat(product.price).toFixed(2),
    })
    setOriginalStock(Number(product.stock))
    setIsEditProductOpen(true)
  }

  // Updated handleUpdateProduct function with a simpler approach
  const handleUpdateProduct = async (e: React.FormEvent) => {
    e.preventDefault()

    setFormError(null)
    setFormSuccess(null)

    if (!validateEditForm()) {
      return
    }

    setIsSubmitting(true)

    try {
      const token = localStorage.getItem("token")
      if (!token) {
        setFormError("Authentication token not found. Please log in again.")
        return
      }

      const storeId = selectedStore === "all" ? productToEdit.store_id : selectedStore

      // First, update the product details (excluding stock)
      const productData = {
        name: productToEdit.name,
        category: productToEdit.category,
        price: Number.parseFloat(productToEdit.price),
        reorderLevel: Number.parseInt(productToEdit.reorderLevel),
        description: productToEdit.description || "",
        barcode: productToEdit.barcode || null,
      }

      const updateResponse = await fetch(
        `https://sarismart-backend.onrender.com/api/v1/stores/${storeId}/owner/products/${productToEdit.id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(productData),
        },
      )

      if (!updateResponse.ok) {
        // Check if there's content to parse
        const text = await updateResponse.text()
        let errorMessage = "Failed to update product"

        if (text) {
          try {
            const errorData = JSON.parse(text)
            errorMessage = errorData.message || errorData.error || errorMessage
          } catch (parseError) {
            console.error("Error parsing error response:", parseError)
            // Use the text as is if it's not valid JSON
            errorMessage = text || errorMessage
          }
        }

        throw new Error(errorMessage)
      }

      // Check if stock has changed
      const newStock = Number(productToEdit.stock)
      if (newStock !== originalStock) {
        // Use the correct API format with quantity as a query parameter
        const stockUpdateUrl = new URL(
          `https://sarismart-backend.onrender.com/api/v1/stores/${storeId}/products/${productToEdit.id}/stock`,
        )

        // Add quantity as a query parameter
        stockUpdateUrl.searchParams.append("quantity", newStock.toString())

        console.log("Stock update URL:", stockUpdateUrl.toString())

        const stockUpdateResponse = await fetch(stockUpdateUrl.toString(), {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${token}`,
          },
          // No body needed as we're using query parameters
        })

        if (!stockUpdateResponse.ok) {
          // Check if there's content to parse
          const text = await stockUpdateResponse.text()
          console.error("Stock update error response:", text)

          let errorMessage = "Failed to update stock level"
          if (text) {
            try {
              const errorData = JSON.parse(text)
              errorMessage = errorData.message || errorData.error || errorMessage
            } catch (parseError) {
              console.error("Error parsing error response:", parseError)
              errorMessage = text || errorMessage
            }
          }

          throw new Error(errorMessage)
        }
      }

      setFormSuccess(`Product ${productToEdit.name} updated successfully!`)
      showToast(`Product ${productToEdit.name} updated successfully`, "success")

      setTimeout(() => {
        setIsEditProductOpen(false)
        setProductToEdit(null)
        setFormSuccess(null)
        fetchProducts()
      }, 1500)
    } catch (error) {
      console.error("Error updating product:", error)
      const errorMessage = error instanceof Error ? error.message : "Unknown error occurred"
      setFormError(`Failed to update product: ${errorMessage}`)
      showToast(`Failed to update product: ${errorMessage}`, "error")
    } finally {
      setIsSubmitting(false)
    }
  }

  const [editBarcodeError, setEditBarcodeError] = useState<string | null>(null)

  // Update the handleEditInputChange function to validate barcode
  const handleEditInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target

    if (name === "barcode") {
      setEditBarcodeError(null)
      if (value && !validateBarcode(value)) {
        setEditBarcodeError("Please enter a valid barcode format")
      }
    }

    setProductToEdit((prev: Product) => ({
      ...prev,
      [name]: value,
    }))
    setFormError(null)
  }

  // Add this function to validate the edit form
  const validateEditForm = () => {
    if (!productToEdit.name.trim()) {
      setFormError("Product name is required")
      return false
    }
    if (!productToEdit.category) {
      setFormError("Category is required")
      return false
    }
    if (!productToEdit.price || isNaN(Number(productToEdit.price)) || Number(productToEdit.price) <= 0) {
      setFormError("Price must be a positive number")
      return false
    }
    if (!productToEdit.stock || isNaN(Number(productToEdit.stock)) || Number(productToEdit.stock) < 0) {
      setFormError("Stock must be a non-negative number")
      return false
    }
    if (
      !productToEdit.reorderLevel ||
      isNaN(Number(productToEdit.reorderLevel)) ||
      Number(productToEdit.reorderLevel) < 0
    ) {
      setFormError("Reorder level must be a non-negative number")
      return false
    }
    if (productToEdit.barcode && !validateBarcode(productToEdit.barcode)) {
      setFormError("Please enter a valid barcode format")
      return false
    }
    return true
  }

  const handleDeleteProduct = (product: any) => {
    if (selectedStore === "all" && !isUserProduct(product)) {
      showToast("You can only delete products from your own stores", "error")
      return
    }

    setProductToDelete(product)
    setIsDeleteModalOpen(true)
  }

  const confirmDeleteProduct = async () => {
    if (!productToDelete) return

    try {
      const token = localStorage.getItem("token")
      if (!token) {
        showToast("Authentication token not found", "error")
        return
      }

      const storeId = selectedStore === "all" ? productToDelete.store_id : selectedStore

      const response = await fetch(
        `https://sarismart-backend.onrender.com/api/v1/stores/${storeId}/products/${productToDelete.id}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      )

      if (!response.ok) {
        throw new Error("Failed to delete product")
      }

      showToast(`Product "${productToDelete.name}" deleted successfully`, "success")

      fetchProducts()
    } catch (error) {
      console.error("Error deleting product:", error)
      showToast(`Failed to delete product: ${error instanceof Error ? error.message : "Unknown error"}`, "error")
    } finally {
      setIsDeleteModalOpen(false)
      setProductToDelete(null)
    }
  }

  const isUserProduct = (product: any): boolean => {
    if (selectedStore === "all") {
      return stores.some((store) => store.id.toString() === product.store_id.toString())
    }
    return true
  }

  return (
    <main className="flex-1 p-4 md:p-8">
      <div className="mb-6 flex items-center justify-between">
        <StoreSelector />
        <div className="flex items-center gap-2">
          {selectedStore !== "all" && (
            <Button className="bg-[#008080] hover:bg-[#005F6B]" onClick={() => setIsAddProductOpen(true)}>
              <Plus className="mr-2 h-4 w-4" />
              Add Product
            </Button>
          )}
        </div>
      </div>

      <div className="flex flex-col space-y-4 md:flex-row md:items-center md:justify-between md:space-y-0">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Product Inventory Management</h1>
          <p className="text-muted-foreground">Manage your product catalog and inventory across all stores</p>
        </div>
      </div>

      <Separator className="my-6" />

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
            <div className="text-2xl font-bold">{inventoryMetrics.totalProducts}</div>
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
            <div className="text-2xl font-bold">{inventoryMetrics.categories}</div>
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
            <div className="text-2xl font-bold">{inventoryMetrics.lowStock}</div>
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
            <div className="text-2xl font-bold">₱{inventoryMetrics.inventoryValue.toFixed(2)}</div>
          </CardContent>
        </Card>
      </div>

      <div className="flex flex-col space-y-4 sm:flex-row sm:items-center sm:justify-between sm:space-y-0">
        <div className="flex items-center gap-2">
          <div className="relative w-full md:w-[300px]">
            <Filter className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              type="search"
              placeholder="Search products..."
              className="w-full pl-8 md:w-[300px]"
              value={searchQuery}
              onChange={handleSearchChange}
            />
          </div>
          <Button
            variant="outline"
            size="icon"
            onClick={() => {
              fetchProducts()
              showToast("Refreshing product data...", "info")
            }}
            disabled={isLoading}
            title="Refresh products"
          >
            {isLoading ? (
              <div className="h-4 w-4 animate-spin rounded-full border-2 border-[#008080] border-t-transparent" />
            ) : (
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="h-4 w-4"
              >
                <path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8" />
                <path d="M21 3v5h-5" />
                <path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16" />
                <path d="M8 16H3v5" />
              </svg>
            )}
          </Button>
        </div>

        <div className="flex items-center gap-2"></div>
      </div>

      <Tabs defaultValue="all" className="mt-6" onValueChange={handleTabChange}>
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
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {isLoading ? (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8">
                        Loading products...
                      </TableCell>
                    </TableRow>
                  ) : filteredProducts.length > 0 ? (
                    filteredProducts.map((product: any) => {
                      const canModify = isUserProduct(product)
                      return (
                        <TableRow key={product.id}>
                          <TableCell>
                            <div className="font-medium">{product.name}</div>
                            {product.barcode && (
                              <div className="text-xs text-muted-foreground">Barcode: {product.barcode}</div>
                            )}
                            {selectedStore === "all" && (
                              <div className="text-xs text-muted-foreground">
                                Store:{" "}
                                {product.store_name ||
                                  stores.find((s) => s.id.toString() === product.store_id.toString())?.name ||
                                  `Store #${product.store_id}`}
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
                          <TableCell className="text-right">
                            <div className="flex justify-end gap-2">
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => handleEditProduct(product)}
                                disabled={!canModify}
                                title={!canModify ? "You can only edit products from your own stores" : "Edit product"}
                              >
                                Edit
                              </Button>
                              <Button
                                variant="destructive"
                                size="sm"
                                onClick={() => handleDeleteProduct(product)}
                                disabled={!canModify}
                                title={
                                  !canModify ? "You can only delete products from your own stores" : "Delete product"
                                }
                              >
                                Delete
                              </Button>
                            </div>
                          </TableCell>
                        </TableRow>
                      )
                    })
                  ) : (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8 text-muted-foreground">
                        {selectedStore === "all" ? (
                          "No products found across all stores."
                        ) : (
                          <>
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
                          </>
                        )}
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
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {isLoading ? (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8">
                        Loading products...
                      </TableCell>
                    </TableRow>
                  ) : filteredProducts.length > 0 ? (
                    filteredProducts.map((product: any) => {
                      const canModify = isUserProduct(product)
                      return (
                        <TableRow key={product.id}>
                          <TableCell>
                            <div className="font-medium">{product.name}</div>
                            {product.barcode && (
                              <div className="text-xs text-muted-foreground">Barcode: {product.barcode}</div>
                            )}
                            {selectedStore === "all" && (
                              <div className="text-xs text-muted-foreground">
                                Store:{" "}
                                {product.store_name ||
                                  stores.find((s) => s.id.toString() === product.store_id.toString())?.name ||
                                  `Store #${product.store_id}`}
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
                          <TableCell className="text-right">
                            <div className="flex justify-end gap-2">
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => handleEditProduct(product)}
                                disabled={!canModify}
                                title={!canModify ? "You can only edit products from your own stores" : "Edit product"}
                              >
                                Edit
                              </Button>
                              <Button
                                variant="destructive"
                                size="sm"
                                onClick={() => handleDeleteProduct(product)}
                                disabled={!canModify}
                                title={
                                  !canModify ? "You can only delete products from your own stores" : "Delete product"
                                }
                              >
                                Delete
                              </Button>
                            </div>
                          </TableCell>
                        </TableRow>
                      )
                    })
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
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {isLoading ? (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8">
                        Loading products...
                      </TableCell>
                    </TableRow>
                  ) : filteredProducts.length > 0 ? (
                    filteredProducts.map((product: any) => {
                      const canModify = isUserProduct(product)
                      return (
                        <TableRow key={product.id}>
                          <TableCell>
                            <div className="font-medium">{product.name}</div>
                            {product.barcode && (
                              <div className="text-xs text-muted-foreground">Barcode: {product.barcode}</div>
                            )}
                            {selectedStore === "all" && (
                              <div className="text-xs text-muted-foreground">
                                Store:{" "}
                                {product.store_name ||
                                  stores.find((s) => s.id.toString() === product.store_id.toString())?.name ||
                                  `Store #${product.store_id}`}
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
                          <TableCell className="text-right">
                            <div className="flex justify-end gap-2">
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => handleEditProduct(product)}
                                disabled={!canModify}
                                title={!canModify ? "You can only edit products from your own stores" : "Edit product"}
                              >
                                Edit
                              </Button>
                              <Button
                                variant="destructive"
                                size="sm"
                                onClick={() => handleDeleteProduct(product)}
                                disabled={!canModify}
                                title={
                                  !canModify ? "You can only delete products from your own stores" : "Delete product"
                                }
                              >
                                Delete
                              </Button>
                            </div>
                          </TableCell>
                        </TableRow>
                      )
                    })
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
                    <TableHead className="text-right">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {isLoading ? (
                    <TableRow>
                      <TableCell colSpan={7} className="text-center py-8">
                        Loading products...
                      </TableCell>
                    </TableRow>
                  ) : filteredProducts.length > 0 ? (
                    filteredProducts.map((product: any) => {
                      const canModify = isUserProduct(product)
                      return (
                        <TableRow key={product.id}>
                          <TableCell>
                            <div className="font-medium">{product.name}</div>
                            {product.barcode && (
                              <div className="text-xs text-muted-foreground">Barcode: {product.barcode}</div>
                            )}
                            {selectedStore === "all" && (
                              <div className="text-xs text-muted-foreground">
                                Store:{" "}
                                {product.store_name ||
                                  stores.find((s) => s.id.toString() === product.store_id.toString())?.name ||
                                  `Store #${product.store_id}`}
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
                          <TableCell className="text-right">
                            <div className="flex justify-end gap-2">
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => handleEditProduct(product)}
                                disabled={!canModify}
                                title={!canModify ? "You can only edit products from your own stores" : "Edit product"}
                              >
                                Edit
                              </Button>
                              <Button
                                variant="destructive"
                                size="sm"
                                onClick={() => handleDeleteProduct(product)}
                                disabled={!canModify}
                                title={
                                  !canModify ? "You can only delete products from your own stores" : "Delete product"
                                }
                              >
                                Delete
                              </Button>
                            </div>
                          </TableCell>
                        </TableRow>
                      )
                    })
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

      {isAddProductOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="relative w-full max-w-[500px] rounded-lg bg-white p-5 shadow-lg">
            <button
              className="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none"
              onClick={() => {
                setIsAddProductOpen(false)
                setFormError(null)
                setFormSuccess(null)
              }}
            >
              <X className="h-4 w-4" />
              <span className="sr-only">Close</span>
            </button>

            <div className="mb-4">
              <h2 className="text-lg font-semibold leading-none tracking-tight">Add New Product</h2>
              <p className="text-sm text-muted-foreground">Fill in the product details below.</p>
            </div>

            {formError && (
              <Alert variant="destructive" className="mb-4">
                <AlertCircle className="h-4 w-4" />
                <AlertTitle>Error</AlertTitle>
                <AlertDescription>{formError}</AlertDescription>
              </Alert>
            )}

            {formSuccess && (
              <Alert className="mb-4 bg-green-50 text-green-800 border-green-200">
                <AlertTitle>Success</AlertTitle>
                <AlertDescription>{formSuccess}</AlertDescription>
              </Alert>
            )}

            <form onSubmit={handleAddProduct}>
              <div className="grid gap-3 py-2">
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
                  <Label htmlFor="barcode">Barcode</Label>
                  <Input
                    id="barcode"
                    name="barcode"
                    placeholder="Enter product barcode"
                    value={newProduct.barcode}
                    onChange={handleInputChange}
                    required
                  />
                  {barcodeError && <p className="text-xs text-red-500">{barcodeError}</p>}
                  <p className="text-xs text-gray-500">
                    Common formats: EAN-13 (13 digits), UPC-A (12 digits), or alphanumeric code
                  </p>
                </div>
                <div className="space-y-1">
                  <Label htmlFor="category">Category</Label>
                  <Input
                    id="category"
                    name="category"
                    placeholder="Enter product category"
                    value={newProduct.category}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="grid grid-cols-3 gap-3">
                  <div className="space-y-1">
                    <Label htmlFor="price">Price (₱)</Label>
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
                    <Label htmlFor="reorderLevel">Reorder Level</Label>
                    <Input
                      id="reorderLevel"
                      name="reorderLevel"
                      type="number"
                      min="0"
                      placeholder="0"
                      value={newProduct.reorderLevel}
                      onChange={handleInputChange}
                      required
                    />
                  </div>
                </div>
                <div className="space-y-1">
                  <Label htmlFor="description">Description (Optional)</Label>
                  <Input
                    id="description"
                    name="description"
                    placeholder="Enter product description"
                    value={newProduct.description}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="space-y-1">
                  <Label htmlFor="image">Product Image (Optional)</Label>
                  <Input id="image" type="file" className="cursor-pointer" />
                </div>
              </div>
              <div className="mt-4 flex justify-end gap-2">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => {
                    setIsAddProductOpen(false)
                    setFormError(null)
                    setFormSuccess(null)
                  }}
                >
                  Cancel
                </Button>
                <Button type="submit" className="bg-[#008080] hover:bg-[#005F6B]" disabled={isSubmitting}>
                  {isSubmitting ? "Adding..." : "Add Product"}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {isDeleteModalOpen && productToDelete && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="relative w-full max-w-[400px] rounded-lg bg-white p-5 shadow-lg">
            <button
              className="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none"
              onClick={() => {
                setIsDeleteModalOpen(false)
                setProductToDelete(null)
              }}
            >
              <X className="h-4 w-4" />
              <span className="sr-only">Close</span>
            </button>

            <div className="mb-4">
              <h2 className="text-lg font-semibold leading-none tracking-tight">Delete Product</h2>
              <p className="text-sm text-muted-foreground">
                Are you sure you want to delete the product <strong>{productToDelete.name}</strong>? This action cannot
                be undone.
              </p>
            </div>

            <div className="mt-4 flex justify-end gap-2">
              <Button
                type="button"
                variant="outline"
                onClick={() => {
                  setIsDeleteModalOpen(false)
                  setProductToDelete(null)
                }}
              >
                Cancel
              </Button>
              <Button type="button" className="bg-red-600 hover:bg-red-700" onClick={confirmDeleteProduct}>
                Delete
              </Button>
            </div>
          </div>
        </div>
      )}

      {isEditProductOpen && productToEdit && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="relative w-full max-w-[500px] rounded-lg bg-white p-5 shadow-lg">
            <button
              className="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none"
              onClick={() => {
                setIsEditProductOpen(false)
                setProductToEdit(null)
                setFormError(null)
                setFormSuccess(null)
              }}
            >
              <X className="h-4 w-4" />
              <span className="sr-only">Close</span>
            </button>

            <div className="mb-4">
              <h2 className="text-lg font-semibold leading-none tracking-tight">Edit Product</h2>
              <p className="text-sm text-muted-foreground">Update the product details below.</p>
            </div>

            {formError && (
              <Alert variant="destructive" className="mb-4">
                <AlertCircle className="h-4 w-4" />
                <AlertTitle>Error</AlertTitle>
                <AlertDescription>{formError}</AlertDescription>
              </Alert>
            )}

            {formSuccess && (
              <Alert className="mb-4 bg-green-50 text-green-800 border-green-200">
                <AlertTitle>Success</AlertTitle>
                <AlertDescription>{formSuccess}</AlertDescription>
              </Alert>
            )}

            <form onSubmit={handleUpdateProduct}>
              <div className="grid gap-3 py-2">
                <div className="space-y-1">
                  <Label htmlFor="edit-name">Product Name</Label>
                  <Input
                    id="edit-name"
                    name="name"
                    placeholder="Enter product name"
                    value={productToEdit.name}
                    onChange={handleEditInputChange}
                    required
                  />
                </div>
                <div className="space-y-1">
                  <Label htmlFor="edit-barcode">Barcode (Optional)</Label>
                  <Input
                    id="edit-barcode"
                    name="barcode"
                    placeholder="Enter product barcode"
                    value={productToEdit.barcode || ""}
                    onChange={handleEditInputChange}
                  />
                  {editBarcodeError && <p className="text-xs text-red-500">{editBarcodeError}</p>}
                  <p className="text-xs text-gray-500">
                    Common formats: EAN-13 (13 digits), UPC-A (12 digits), or alphanumeric code
                  </p>
                </div>
                <div className="space-y-1">
                  <Label htmlFor="edit-category">Category</Label>
                  <Input
                    id="edit-category"
                    name="category"
                    placeholder="Enter product category"
                    value={productToEdit.category}
                    onChange={handleEditInputChange}
                    required
                  />
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <div className="space-y-1">
                    <Label htmlFor="edit-price">Price (₱)</Label>
                    <Input
                      id="edit-price"
                      name="price"
                      type="number"
                      step="0.01"
                      min="0"
                      placeholder="0.00"
                      value={productToEdit.price}
                      onChange={handleEditInputChange}
                      required
                    />
                  </div>
                  <div className="space-y-1">
                    <Label htmlFor="edit-reorderLevel">Reorder Level</Label>
                    <Input
                      id="edit-reorderLevel"
                      name="reorderLevel"
                      type="number"
                      min="0"
                      placeholder="0"
                      value={productToEdit.reorderLevel}
                      onChange={handleEditInputChange}
                      required
                    />
                  </div>
                </div>
                <div className="space-y-1">
                  <Label htmlFor="edit-description">Description (Optional)</Label>
                  <Input
                    id="edit-description"
                    name="description"
                    placeholder="Enter product description"
                    value={productToEdit.description || ""}
                    onChange={handleEditInputChange}
                  />
                </div>
                <div className="space-y-1">
                  <Label htmlFor="edit-stock">Add Stock Quantity</Label>
                  <Input
                    id="edit-stock"
                    name="stock"
                    type="number"
                    min="0"
                    placeholder="0"
                    value={productToEdit.stock}
                    onChange={handleEditInputChange}
                    required
                  />
                  <p className="text-xs text-gray-500">Current stock: {originalStock}. Enter the quantity to add.</p>
                </div>
              </div>
              <div className="mt-4 flex justify-end gap-2">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => {
                    setIsEditProductOpen(false)
                    setFormError(null)
                    setFormSuccess(null)
                  }}
                >
                  Cancel
                </Button>
                <Button type="submit" className="bg-[#008080] hover:bg-[#005F6B]" disabled={isSubmitting}>
                  {isSubmitting ? "Updating..." : "Update Product"}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </main>
  )
}
