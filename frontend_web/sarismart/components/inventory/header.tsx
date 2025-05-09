  "use client"
  import Image from "next/image"
  import { ChevronDown, Menu, User } from "lucide-react"
  import { useUser } from "@/hooks/user-user"
  import { Button } from "@/components/ui/button"
  import { Skeleton } from "@/components/ui/skeleton"

  export default function InventoryHeader() {
    const { user, isLoading, error } = useUser()

    // Get first letter of first and last name for avatar fallback
    const getInitials = () => {
      if (!user) return ""
      return `${user.firstName?.[0] || ""}${user.lastName?.[0] || ""}`.toUpperCase()
    }

    // Get display name
    const getDisplayName = () => {
      if (!user) return ""
      if (user.firstName && user.lastName) {
        return `${user.firstName} ${user.lastName}`
      }
      return user.username || user.email || ""
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

        {/* Center Section */}
        <div className="absolute left-1/2 transform -translate-x-1/2">
          <h1 className="text-lg font-semibold text-[#008080]">SariSmart</h1>
        </div>

        {/* Right Section - Profile */}
        <div className="flex items-center gap-4 ml-auto">
          <div className="flex items-center gap-2">
            {isLoading ? (
              <>
                <Skeleton className="h-8 w-8 rounded-full" />
                <div className="hidden md:block">
                  <Skeleton className="h-4 w-24 mb-1" />
                  <Skeleton className="h-3 w-16" />
                </div>
              </>
            ) : error ? (
              <div className="relative h-8 w-8 overflow-hidden rounded-full bg-gray-200 flex items-center justify-center">
                <User className="h-5 w-5 text-gray-500" />
              </div>
            ) : (
              <>
                <div className="relative h-8 w-8 overflow-hidden rounded-full bg-[#008080] flex items-center justify-center text-white">
                  {user?.profileImage ? (
                    <Image
                      src={user.profileImage || "/placeholder.svg"}
                      alt={getDisplayName()}
                      width={32}
                      height={32}
                      className="object-cover"
                    />
                  ) : (
                    <span className="text-xs font-medium">{getInitials()}</span>
                  )}
                </div>
                <div className="hidden md:block">
                  <div className="text-sm font-medium">{getDisplayName()}</div>
                </div>
              </>
            )}
          </div>
        </div>
      </header>
    )
  }
