"use client"

import type React from "react"

import { useState } from "react"
import Image from "next/image"
import { ChevronDown, Menu, Plus, Store, X, Pencil, Trash2 } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { useStoresContext } from "@/hooks/use-stores-context"

export default function InventoryHeader() {
  const { stores, selectedStore, setSelectedStore, addStore, updateStore, deleteStore } = useStoresContext() // Use selectedStore and setter

  // STEP 1: Set up state for the Add Store modal
  const [isAddStoreOpen, setIsAddStoreOpen] = useState(false) // Controls the visibility of the Add Store modal

  // STEP 2: Create state for the new store form data
  const [newStore, setNewStore] = useState<{
    name: string
    location: string
  }>({
    name: "",
    location: "",
  })

  const [isUpdateStoreOpen, setIsUpdateStoreOpen] = useState(false) // Controls the visibility of the Update Store modal
  const [storeToUpdate, setStoreToUpdate] = useState<{ id: string | number; name: string; location: string } | null>(
    null,
  )

  const [isDeleteStoreOpen, setIsDeleteStoreOpen] = useState(false) // Controls the visibility of the Delete Store modal
  const [storeToDelete, setStoreToDelete] = useState<{ id: string | number; name: string } | null>(null)

  // Handle opening the update modal
  const handleOpenUpdateModal = (store: { id: string | number; name: string; location: string }) => {
    setStoreToUpdate(store)
    setIsUpdateStoreOpen(true)
  }

  // Handle updating a store
  const handleUpdateStore = (e: React.FormEvent) => {
    e.preventDefault()
    if (storeToUpdate) {
      updateStore(storeToUpdate.id, { name: storeToUpdate.name, location: storeToUpdate.location })
      setIsUpdateStoreOpen(false)
    }
  }

  // Handle opening the delete modal
  const handleOpenDeleteModal = (store: { id: string | number; name: string }) => {
    setStoreToDelete(store)
    setIsDeleteStoreOpen(true)
  }

  // Handle confirming the deletion
  const handleConfirmDelete = () => {
    if (storeToDelete) {
      deleteStore(storeToDelete.id)
      setIsDeleteStoreOpen(false)
    }
  }

  // Handle deleting a store
  const handleDeleteStore = (storeId: string | number) => {
    if (confirm("Are you sure you want to delete this store?")) {
      deleteStore(storeId)
    }
  }

  // STEP 4: Handle input changes in the form fields
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setNewStore((prev) => ({ ...prev, [name]: value }))
  }

  // STEP 5: Handle form submission when the Add Store button is clicked
  const handleAddStore = (e: React.FormEvent) => {
    e.preventDefault() // Prevent the default form submission behavior

    // STEP 6: Call the addStore function from useStores hook to add the new store
    addStore(newStore)

    // STEP 7: Show a success message using a toast notification
    const toast = document.createElement("div")
    toast.className = "fixed top-4 right-4 bg-green-600 text-white px-4 py-2 rounded shadow-lg z-50"
    toast.textContent = `Store "${newStore.name}" added successfully`
    document.body.appendChild(toast)

    // STEP 8: Fade out and remove the toast after a delay
    setTimeout(() => {
      toast.style.opacity = "0"
      toast.style.transition = "opacity 0.5s ease"
      setTimeout(() => document.body.removeChild(toast), 500)
    }, 2000)

    // STEP 9: Reset the form and close the modal
    setNewStore({ name: "", location: "" })
    setIsAddStoreOpen(false)
  }

  return (
    <header className="relative flex h-16 items-center justify-between border-b bg-white px-4 md:px-6">
      {/* Left Section */}
      <div className="flex items-center md:hidden">
        <Button variant="ghost" size="icon" className="mr-2">
          <Menu className="h-6 w-6" />
          <span className="sr-only">Toggle menu</span>
        </Button>
      </div>

      {/* Center Section - Store Selector */}
      <div className="absolute left-1/2 transform -translate-x-1/2">
        <div className="flex items-center">
          <Store className="mr-2 h-5 w-5 text-[#008080]" />
          {/* STEP 10: Set up the store selector dropdown */}
          <Select
            value={selectedStore}
            onValueChange={(value) => {
              // STEP 11: If "Add Store" is selected, open the Add Store modal
              if (value === "add-store") {
                setIsAddStoreOpen(true)
                return
              }

              // Otherwise, update the selected store
              setSelectedStore(value) // Update selectedStore in context
              // Show visual feedback with a toast notification
              const toast = document.createElement("div")
              toast.className = "fixed top-4 right-4 bg-[#008080] text-white px-4 py-2 rounded shadow-lg z-50"
              toast.textContent = `Switched to ${
                value === "all" ? "All Stores" : stores.find((s) => String(s.id) === value)?.name
              }`
              document.body.appendChild(toast)
              setTimeout(() => {
                toast.style.opacity = "0"
                toast.style.transition = "opacity 0.5s ease"
                setTimeout(() => document.body.removeChild(toast), 500)
              }, 2000)
            }}
          >
            <SelectTrigger className="w-[180px] border-none">
              <SelectValue placeholder="Select store" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Stores</SelectItem>
              {/* STEP 12: Map through the stores to create dropdown options */}
              {stores.map((store) => (
                <div key={store.id} className="flex items-center justify-between">
                  <SelectItem value={String(store.id)}>{store.name}</SelectItem>
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleOpenUpdateModal(store)}
                      className="text-blue-500 p-1 hover:bg-blue-50 rounded"
                    >
                      <Pencil className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => handleOpenDeleteModal(store)}
                      className="text-red-500 p-1 hover:bg-red-50 rounded"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              ))}
              {/* STEP 13: Add the "Add Store" option at the bottom of the dropdown */}
              <SelectItem value="add-store" className="text-[#008080] font-medium">
                <div className="flex items-center">
                  <Plus className="mr-2 h-4 w-4" />
                  Add Store
                </div>
              </SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      {/* Right Section - Profile */}
      <div className="flex items-center gap-4 ml-auto">
        <div className="flex items-center gap-2">
          <div className="relative h-8 w-8 overflow-hidden rounded-full">
            <Image
              src="/placeholder.svg?height=32&width=32"
              alt="User"
              width={32}
              height={32}
              className="object-cover"
            />
          </div>
          <div className="hidden md:block">
            <div className="text-sm font-medium">Rahimah</div>
            <div className="text-xs text-gray-500">Admin</div>
          </div>
          <ChevronDown className="h-4 w-4 text-gray-500" />
        </div>
      </div>

      {/* STEP 14: Add Store Modal - Only shown when isAddStoreOpen is true */}
      {isAddStoreOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="relative w-full max-w-[400px] rounded-lg bg-white p-5 shadow-lg">
            {/* STEP 15: Close button for the modal */}
            <button
              className="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none"
              onClick={() => setIsAddStoreOpen(false)}
            >
              <X className="h-4 w-4" />
              <span className="sr-only">Close</span>
            </button>

            <div className="mb-4">
              <h2 className="text-lg font-semibold leading-none tracking-tight">Add New Store</h2>
              <p className="text-sm text-muted-foreground">
                Enter the store name and location. Store ID will be generated automatically.
              </p>
            </div>

            {/* STEP 16: Form with onSubmit handler to process the form submission */}
            <form onSubmit={handleAddStore}>
              <div className="grid gap-4 py-2">
                <div className="space-y-1">
                  <Label htmlFor="name">Store Name</Label>
                  <Input
                    id="name"
                    name="name"
                    placeholder="Enter store name (e.g., Downtown Branch)"
                    value={newStore.name}
                    onChange={handleInputChange}
                    required
                  />
                </div>
                <div className="space-y-1">
                  <Label htmlFor="location">Store Location</Label>
                  <Input
                    id="location"
                    name="location"
                    placeholder="Enter store location (e.g., City Center)"
                    value={newStore.location}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>
              <div className="mt-4 flex justify-end gap-2">
                {/* STEP 17: Cancel button to close the modal without saving */}
                <Button type="button" variant="outline" onClick={() => setIsAddStoreOpen(false)}>
                  Cancel
                </Button>
                {/* STEP 18: Submit button to save the new store - type="submit" triggers the form's onSubmit */}
                <Button type="submit" className="bg-[#008080] hover:bg-[#005F6B]">
                  Add Store
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Update Store Modal */}
      {isUpdateStoreOpen && storeToUpdate && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="relative w-full max-w-[400px] rounded-lg bg-white p-5 shadow-lg">
            <button
              className="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none"
              onClick={() => setIsUpdateStoreOpen(false)}
            >
              <X className="h-4 w-4" />
              <span className="sr-only">Close</span>
            </button>

            <div className="mb-4">
              <h2 className="text-lg font-semibold leading-none tracking-tight">Update Store</h2>
              <p className="text-sm text-muted-foreground">Modify the store details below.</p>
            </div>

            <form onSubmit={handleUpdateStore}>
              <div className="grid gap-4 py-2">
                <div className="space-y-1">
                  <Label htmlFor="update-name">Store Name</Label>
                  <Input
                    id="update-name"
                    name="name"
                    placeholder="Enter store name"
                    value={storeToUpdate.name}
                    onChange={(e) => setStoreToUpdate((prev) => prev && { ...prev, name: e.target.value })}
                    required
                  />
                </div>
                <div className="space-y-1">
                  <Label htmlFor="update-location">Store Location</Label>
                  <Input
                    id="update-location"
                    name="location"
                    placeholder="Enter store location"
                    value={storeToUpdate.location}
                    onChange={(e) => setStoreToUpdate((prev) => prev && { ...prev, location: e.target.value })}
                    required
                  />
                </div>
              </div>
              <div className="mt-4 flex justify-end gap-2">
                <Button type="button" variant="outline" onClick={() => setIsUpdateStoreOpen(false)}>
                  Cancel
                </Button>
                <Button type="submit" className="bg-[#008080] hover:bg-[#005F6B]">
                  Update Store
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Store Modal */}
      {isDeleteStoreOpen && storeToDelete && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="relative w-full max-w-[400px] rounded-lg bg-white p-5 shadow-lg">
            <button
              className="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none"
              onClick={() => setIsDeleteStoreOpen(false)}
            >
              <X className="h-4 w-4" />
              <span className="sr-only">Close</span>
            </button>

            <div className="mb-4">
              <h2 className="text-lg font-semibold leading-none tracking-tight">Delete Store</h2>
              <p className="text-sm text-muted-foreground">
                Are you sure you want to delete the store <strong>{storeToDelete.name}</strong>? This action cannot be undone.
              </p>
            </div>

            <div className="mt-4 flex justify-end gap-2">
              <Button type="button" variant="outline" onClick={() => setIsDeleteStoreOpen(false)}>
                Cancel
              </Button>
              <Button
                type="button"
                className="bg-red-600 hover:bg-red-700"
                onClick={handleConfirmDelete}
              >
                Delete
              </Button>
            </div>
          </div>
        </div>
      )}
    </header>
  )
}
