import Image from "next/image"
import Link from "next/link"
import { ArrowRight } from "lucide-react"

import { Button } from "@/components/ui/button"

export default function Home() {
  return (
    <div className="flex min-h-screen flex-col">
      <header className="sticky top-0 z-50 border-b bg-white/80 backdrop-blur-md">
        <div className="container flex h-16 items-center justify-between px-4 md:px-6">
          <Link href="/" className="flex items-center gap-2">
            <span className="text-2xl font-bold">
              <span className="text-emerald-600">Sari</span>
              <span>Smart</span>
            </span>
          </Link>
          <nav className="hidden md:flex md:gap-6 lg:gap-10">
            <Link href="/" className="text-sm font-medium text-emerald-600 transition-colors hover:text-emerald-700">
              Home
            </Link>
            <Link href="/about" className="text-sm font-medium text-gray-600 transition-colors hover:text-emerald-600">
              About Us
            </Link>
            <Link
              href="/contact"
              className="text-sm font-medium text-gray-600 transition-colors hover:text-emerald-600"
            >
              Contact Us
            </Link>
          </nav>
          <div className="flex items-center gap-2">
            <Link
              href="/register"
              className="hidden text-sm font-medium text-gray-600 transition-colors hover:text-emerald-600 md:block"
            >
              Register
            </Link>
            <Button asChild size="sm" className="bg-emerald-600 hover:bg-emerald-700">
              <Link href="/login">Log In</Link>
            </Button>
          </div>
        </div>
      </header>
      <main className="flex-1">
        <section className="relative overflow-hidden bg-gradient-to-b from-white to-gray-50 py-20">
          <div className="container relative z-10 px-4 md:px-6">
            <div className="grid gap-12 lg:grid-cols-2 lg:gap-8">
              <div className="flex flex-col justify-center space-y-6">
                <div>
                  <div className="inline-block rounded-full bg-emerald-100 px-3 py-1 text-sm font-medium text-emerald-800">
                    Innovative Solutions
                  </div>
                </div>
                <h1 className="text-4xl font-bold tracking-tight text-gray-900 sm:text-5xl md:text-6xl">
                  Smart Technology for a Better Future
                </h1>
                <p className="max-w-lg text-lg text-gray-600">
                  Empowering businesses with intelligent solutions that drive growth, efficiency, and innovation in
                  today's digital landscape.
                </p>
                <div className="flex flex-col gap-3 sm:flex-row">
                  <Button asChild size="lg" className="bg-emerald-600 hover:bg-emerald-700">
                    <Link href="/learn-more">
                      Learn More
                      <ArrowRight className="ml-2 h-4 w-4" />
                    </Link>
                  </Button>
                  <Button variant="outline" size="lg">
                    <Link href="/contact">Contact Sales</Link>
                  </Button>
                </div>
              </div>
              <div className="flex items-center justify-center">
                <div className="relative h-[400px] w-full max-w-[500px] overflow-hidden rounded-2xl shadow-2xl">
                  <Image
                    src="/placeholder.svg?height=800&width=800"
                    alt="SariSmart Technology"
                    fill
                    className="object-cover"
                    priority
                  />
                </div>
              </div>
            </div>
          </div>
          <div className="absolute inset-0 -z-10 overflow-hidden">
            <svg
              className="absolute left-[max(50%,25rem)] top-0 h-[64rem] w-[128rem] -translate-x-1/2 stroke-gray-200 [mask-image:radial-gradient(64rem_64rem_at_top,white,transparent)]"
              aria-hidden="true"
            >
              <defs>
                <pattern
                  id="e813992c-7d03-4cc4-a2bd-151760b470a0"
                  width="200"
                  height="200"
                  x="50%"
                  y="-1"
                  patternUnits="userSpaceOnUse"
                >
                  <path d="M100 200V.5M.5 .5H200" fill="none" />
                </pattern>
              </defs>
              <svg x="50%" y="-1" className="overflow-visible fill-gray-50">
                <path
                  d="M-100.5 0h201v201h-201Z M699.5 0h201v201h-201Z M499.5 400h201v201h-201Z M-300.5 600h201v201h-201Z"
                  strokeWidth="0"
                />
              </svg>
              <rect width="100%" height="100%" strokeWidth="0" fill="url(#e813992c-7d03-4cc4-a2bd-151760b470a0)" />
            </svg>
          </div>
        </section>
      </main>
    </div>
  )
}

