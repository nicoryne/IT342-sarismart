"use client"

import type React from "react"

import { useState } from "react"
import { Store, Plus, Pencil, Trash2, X, Loader2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useStoresContext } from "@/hooks/use-stores-context"
import { showToast } from "@/components/ui/toast-notification"
import { MapSelector } from "@/components/map-selector"

export function StoreSelector() {
  const { stores, selectedStore, setSelectedStore, addStore, updateStore, deleteStore, isLoading } = useStoresContext()

  // State for store management modals
  const [isAddStoreOpen, setIsAddStoreOpen] = useState(false)
  const [newStore, setNewStore] = useState({
    name: "",
    location: "",
    latitude: 0,
    longitude: 0,
  })
  const [isUpdateStoreOpen, setIsUpdateStoreOpen] = useState(false)
  const [storeToUpdate, setStoreToUpdate] = useState<{
    id: string | number
    name: string
    location: string
    latitude?: number
    longitude?: number
  } | null>(null)
  const [isDeleteStoreOpen, setIsDeleteStoreOpen] = useState(false)
  const [storeToDelete, setStoreToDelete] = useState<{ id: string | number; name: string } | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)

  // Handle input changes in the form fields
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setNewStore((prev) => ({ ...prev, [name]: value }))
  }

  // Handle form submission when the Add Store button is clicked
  const handleAddStore = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsSubmitting(true)
    try {
      await addStore(newStore)
      showToast(`Store "${newStore.name}" added successfully`, "success")
      setNewStore({ name: "", location: "", latitude: 0, longitude: 0 })
      setIsAddStoreOpen(false)
    } catch (error) {
      console.error("Error adding store:", error)
      showToast(`Failed to add store: ${error instanceof Error ? error.message : "Unknown error"}`, "error")
    } finally {
      setIsSubmitting(false)
    }
  }

  // Handle opening the update modal
  const handleOpenUpdateModal = (store: {
    id: string | number
    name: string
    location: string
    latitude?: number
    longitude?: number
  }) => {
    setStoreToUpdate(store)
    setIsUpdateStoreOpen(true)
  }

  // Handle updating a store
  const handleUpdateStore = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!storeToUpdate) return

    setIsSubmitting(true)
    try {
      await updateStore(storeToUpdate.id, {
        name: storeToUpdate.name,
        location: storeToUpdate.location,
        latitude: storeToUpdate.latitude,
        longitude: storeToUpdate.longitude,
      })
      showToast(`Store "${storeToUpdate.name}" updated successfully`, "success")
      setIsUpdateStoreOpen(false)
    } catch (error) {
      console.error("Error updating store:", error)
      showToast(`Failed to update store: ${error instanceof Error ? error.message : "Unknown error"}`, "error")
    } finally {
      setIsSubmitting(false)
    }
  }

  // Handle opening the delete modal
  const handleOpenDeleteModal = (store: { id: string | number; name: string }) => {
    setStoreToDelete(store)
    setIsDeleteStoreOpen(true)
  }

  // Handle confirming the deletion
  const handleConfirmDelete = async () => {
    if (!storeToDelete) return

    setIsSubmitting(true)
    try {
      await deleteStore(storeToDelete.id)
      showToast(`Store "${storeToDelete.name}" deleted successfully`, "success")
      setIsDeleteStoreOpen(false)
    } catch (error) {
      console.error("Error deleting store:", error)
      showToast(`Failed to delete store: ${error instanceof Error ? error.message : "Unknown error"}`, "error")
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <>
      <div className="flex items-center">
        <Store className="mr-2 h-5 w-5 text-[#008080]" />
        {isLoading ? (
          <div className="flex items-center">
            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
            <span>Loading stores...</span>
          </div>
        ) : (
          <Select
            value={selectedStore}
            onValueChange={(value) => {
              if (value === "add-store") {
                setIsAddStoreOpen(true)
                return
              }

              setSelectedStore(value)
              showToast(
                `Switched to ${value === "all" ? "All Stores" : stores.find((s) => String(s.id) === value)?.name}`,
                "info",
              )
            }}
          >
            <SelectTrigger className="w-[220px]">
              <SelectValue placeholder="Select store" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Stores</SelectItem>
              {stores.map((store) => (
                <div key={store.id} className="flex items-center justify-between">
                  <SelectItem value={String(store.id)}>{store.name}</SelectItem>
                  <div className="flex gap-2">
                    <button
                      onClick={(e) => {
                        e.stopPropagation()
                        handleOpenUpdateModal(store)
                      }}
                      className="text-blue-500 p-1 hover:bg-blue-50 rounded"
                    >
                      <Pencil className="h-4 w-4" />
                    </button>
                    <button
                      onClick={(e) => {
                        e.stopPropagation()
                        handleOpenDeleteModal(store)
                      }}
                      className="text-red-500 p-1 hover:bg-red-50 rounded"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              ))}
              <SelectItem value="add-store" className="text-[#008080] font-medium">
                <div className="flex items-center">
                  <Plus className="mr-2 h-4 w-4" />
                  Add Store
                </div>
              </SelectItem>
            </SelectContent>
          </Select>
        )}
      </div>

      {/* Add Store Modal */}
      {isAddStoreOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="relative w-full max-w-[600px] rounded-lg bg-white p-5 shadow-lg max-h-[90vh] overflow-y-auto">
            <button
              className="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none"
              onClick={() => setIsAddStoreOpen(false)}
            >
              <X className="h-4 w-4" />
              <span className="sr-only">Close</span>
            </button>

            <div className="mb-4">
              <h2 className="text-lg font-semibold leading-none tracking-tight">Add New Store</h2>
              <p className="text-sm text-muted-foreground">Enter the store name and select its location on the map.</p>
            </div>

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

                {/* Map selector component */}
                <div className="space-y-1">
                  <Label htmlFor="location-map">Store Location</Label>
                  <MapSelector
                    onLocationSelect={(lat, lng, address) => {
                      setNewStore((prev) => ({
                        ...prev,
                        location: address,
                        latitude: lat,
                        longitude: lng,
                      }))
                    }}
                  />
                </div>

                <div className="space-y-1">
                  <Label htmlFor="location">Location Address</Label>
                  <Input
                    id="location"
                    name="location"
                    placeholder="Address will be filled automatically from map selection"
                    value={newStore.location}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>
              <div className="mt-4 flex justify-end gap-2">
                <Button type="button" variant="outline" onClick={() => setIsAddStoreOpen(false)}>
                  Cancel
                </Button>
                <Button type="submit" className="bg-[#008080] hover:bg-[#005F6B]" disabled={isSubmitting}>
                  {isSubmitting ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Adding...
                    </>
                  ) : (
                    "Add Store"
                  )}
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Update Store Modal */}
      {isUpdateStoreOpen && storeToUpdate && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="relative w-full max-w-[600px] rounded-lg bg-white p-5 shadow-lg max-h-[90vh] overflow-y-auto">
            <button
              className="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:pointer-events-none"
              onClick={() => setIsUpdateStoreOpen(false)}
            >
              <X className="h-4 w-4" />
              <span className="sr-only">Close</span>
            </button>

            <div className="mb-4">
              <h2 className="text-lg font-semibold leading-none tracking-tight">Update Store</h2>
              <p className="text-sm text-muted-foreground">Modify the store details and location below.</p>
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

                {/* Map selector component */}
                <div className="space-y-1">
                  <Label htmlFor="update-location-map">Store Location</Label>
                  <MapSelector
                    initialLatitude={storeToUpdate.latitude}
                    initialLongitude={storeToUpdate.longitude}
                    onLocationSelect={(lat, lng, address) => {
                      setStoreToUpdate(
                        (prev) =>
                          prev && {
                            ...prev,
                            location: address || prev.location,
                            latitude: lat,
                            longitude: lng,
                          },
                      )
                    }}
                  />
                </div>

                <div className="space-y-1">
                  <Label htmlFor="update-location">Location Address</Label>
                  <Input
                    id="update-location"
                    name="location"
                    placeholder="Address will be filled automatically from map selection"
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
                <Button type="submit" className="bg-[#008080] hover:bg-[#005F6B]" disabled={isSubmitting}>
                  {isSubmitting ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Updating...
                    </>
                  ) : (
                    "Update Store"
                  )}
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
                Are you sure you want to delete the store <strong>{storeToDelete.name}</strong>? This action cannot be
                undone.
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
                disabled={isSubmitting}
              >
                {isSubmitting ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Deleting...
                  </>
                ) : (
                  "Delete"
                )}
              </Button>
            </div>
          </div>
        </div>
      )}
    </>
  )
}
