import Image from "next/image"
import Link from "next/link"
import { ArrowRight } from "lucide-react"

import { Button } from "@/components/ui/button"

export default function Home() {
  return (
    <>
      <section className="relative overflow-hidden bg-gradient-to-b from-white to-gray-50 py-12 sm:py-16 md:py-20">
        <div className="container relative z-10 px-4 md:px-6">
          <div className="grid gap-8 lg:grid-cols-2 lg:gap-12">
            <div className="flex flex-col justify-center space-y-4 sm:space-y-6">
              <div>
                <div className="inline-block rounded-full bg-[#40E0D0]/20 px-3 py-1 text-sm font-medium text-[#005F6B]">
                  Innovative Solutions
                </div>
              </div>
              <h1 className="text-3xl font-bold tracking-tight text-gray-900 sm:text-4xl md:text-5xl lg:text-6xl">
                Smart Technology for a Better Future
              </h1>
              <p className="max-w-lg text-base text-gray-600 sm:text-lg">
                Empowering businesses with intelligent solutions that drive growth, efficiency, and innovation in
                today's digital landscape.
              </p>
              <div className="flex flex-col gap-3 sm:flex-row">
                <Button asChild size="lg" className="bg-[#008080] text-white hover:bg-[#005F6B]">
                  <Link href="/about">
                    Learn More
                    <ArrowRight className="ml-2 h-4 w-4" />
                  </Link>
                </Button>
                <Button
                  asChild
                  variant="outline"
                  size="lg"
                  className="border-[#008080] text-[#008080] hover:bg-[#40E0D0]/10 hover:text-[#005F6B]"
                >
                  <Link href="/contact">Contact Sales</Link>
                </Button>
              </div>
            </div>
            <div className="flex items-center justify-center pt-8 lg:pt-0">
              <div className="relative h-[250px] w-full max-w-[500px] overflow-hidden rounded-2xl shadow-2xl sm:h-[300px] md:h-[350px] lg:h-[400px]">
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
            className="absolute left-[max(50%,25rem)] top-0 h-[64rem] w-[128rem] -translate-x-1/2 stroke-[#40E0D0]/20 [mask-image:radial-gradient(64rem_64rem_at_top,white,transparent)]"
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
            <svg x="50%" y="-1" className="overflow-visible fill-[#40E0D0]/5">
              <path
                d="M-100.5 0h201v201h-201Z M699.5 0h201v201h-201Z M499.5 400h201v201h-201Z M-300.5 600h201v201h-201Z"
                strokeWidth="0"
              />
            </svg>
            <rect width="100%" height="100%" strokeWidth="0" fill="url(#e813992c-7d03-4cc4-a2bd-151760b470a0)" />
          </svg>
        </div>
      </section>
    </>
  )
}
