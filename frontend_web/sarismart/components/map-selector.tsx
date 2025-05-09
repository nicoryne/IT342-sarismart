"use client"

import { useEffect, useRef, useState } from "react"
import L from "leaflet"
import "leaflet/dist/leaflet.css"
import { Button } from "@/components/ui/button"
import { MapPin } from "lucide-react"

// Define props interface for the component
interface MapSelectorProps {
  initialLatitude?: number
  initialLongitude?: number
  onLocationSelect: (latitude: number, longitude: number, address: string) => void
  className?: string
}

export function MapSelector({
  initialLatitude = 14.5995, // Default to Philippines
  initialLongitude = 120.9842,
  onLocationSelect,
  className = "",
}: MapSelectorProps) {
  const mapRef = useRef<HTMLDivElement>(null)
  const leafletMap = useRef<L.Map | null>(null)
  const marker = useRef<L.Marker | null>(null)
  const [address, setAddress] = useState<string>("")
  const [isLoading, setIsLoading] = useState<boolean>(false)

  // Initialize map when component mounts
  useEffect(() => {
    if (!mapRef.current || leafletMap.current) return

    // Fix Leaflet icon issue
    delete (L.Icon.Default.prototype as any)._getIconUrl
    L.Icon.Default.mergeOptions({
      iconRetinaUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png",
      iconUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png",
      shadowUrl: "https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png",
    })

    // Create map
    leafletMap.current = L.map(mapRef.current).setView([initialLatitude, initialLongitude], 13)

    // Add OpenStreetMap tile layer
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(leafletMap.current)

    // Add initial marker if coordinates are provided
    if (initialLatitude && initialLongitude) {
      marker.current = L.marker([initialLatitude, initialLongitude], {
        draggable: true,
      }).addTo(leafletMap.current)

      // Get initial address
      fetchAddress(initialLatitude, initialLongitude)
    }

    // Handle map click to place/move marker
    leafletMap.current.on("click", (e: L.LeafletMouseEvent) => {
      const { lat, lng } = e.latlng

      if (marker.current) {
        marker.current.setLatLng([lat, lng])
      } else {
        marker.current = L.marker([lat, lng], {
          draggable: true,
        }).addTo(leafletMap.current!)
      }

      // Get address for the selected location
      fetchAddress(lat, lng)
    })

    return () => {
      if (leafletMap.current) {
        leafletMap.current.remove()
        leafletMap.current = null
      }
    }
  }, [initialLatitude, initialLongitude])

  // Function to fetch address from coordinates using Nominatim (OpenStreetMap's geocoding service)
  const fetchAddress = async (lat: number, lng: number) => {
    setIsLoading(true)
    try {
      const response = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`,
        {
          headers: {
            "Accept-Language": "en",
          },
        },
      )

      if (!response.ok) {
        throw new Error("Failed to fetch address")
      }

      const data = await response.json()

      // Format the address
      let formattedAddress = ""
      if (data.display_name) {
        formattedAddress = data.display_name
      } else if (data.address) {
        const addr = data.address
        const parts = [addr.road, addr.suburb, addr.city || addr.town || addr.village, addr.state, addr.country].filter(
          Boolean,
        )
        formattedAddress = parts.join(", ")
      }

      setAddress(formattedAddress)

      // Pass coordinates and address to parent component
      onLocationSelect(lat, lng, formattedAddress)
    } catch (error) {
      console.error("Error fetching address:", error)
      setAddress("Location selected (Address lookup failed)")

      // Still pass coordinates even if address lookup fails
      if (marker.current) {
        const position = marker.current.getLatLng()
        onLocationSelect(position.lat, position.lng, "")
      }
    } finally {
      setIsLoading(false)
    }
  }

  // Function to get user's current location
  const getCurrentLocation = () => {
    if (navigator.geolocation) {
      setIsLoading(true)
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const { latitude, longitude } = position.coords

          // Update map view
          if (leafletMap.current) {
            leafletMap.current.setView([latitude, longitude], 16)
          }

          // Update or create marker
          if (marker.current) {
            marker.current.setLatLng([latitude, longitude])
          } else if (leafletMap.current) {
            marker.current = L.marker([latitude, longitude], {
              draggable: true,
            }).addTo(leafletMap.current)
          }

          // Get address for the current location
          fetchAddress(latitude, longitude)
        },
        (error) => {
          console.error("Error getting current location:", error)
          setIsLoading(false)
          alert("Unable to retrieve your location. Please ensure location services are enabled.")
        },
      )
    } else {
      alert("Geolocation is not supported by your browser")
    }
  }

  return (
    <div className={`flex flex-col gap-2 ${className}`}>
      <div ref={mapRef} className="h-[300px] w-full rounded-md border border-gray-200"></div>

      <div className="flex flex-col gap-2">
        <div className="flex items-center gap-2">
          <Button
            type="button"
            variant="outline"
            onClick={getCurrentLocation}
            disabled={isLoading}
            className="flex items-center gap-2"
          >
            <MapPin className="h-4 w-4" />
            {isLoading ? "Getting location..." : "Use my current location"}
          </Button>
        </div>

        {address && (
          <div className="text-sm text-muted-foreground mt-1">
            <strong>Selected address:</strong> {address}
          </div>
        )}

        <div className="text-xs text-muted-foreground">
          Click on the map to select a location or drag the marker to adjust.
        </div>
      </div>
    </div>
  )
}
