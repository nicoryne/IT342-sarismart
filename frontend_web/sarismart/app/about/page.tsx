import Image from "next/image"
import Link from "next/link"
import { ArrowRight, CheckCircle2 } from "lucide-react"

import { Button } from "@/components/ui/button"

export default function AboutPage() {
  return (
    <>
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-b from-white to-gray-50 py-12 sm:py-16 md:py-20">
        <div className="container relative z-10 px-4 md:px-6">
          <div className="mx-auto max-w-3xl text-center">
            <h1 className="text-3xl font-bold tracking-tight text-gray-900 sm:text-4xl md:text-5xl">
              About <span className="text-[#008080]">SariSmart</span>
            </h1>
            <p className="mt-4 text-lg text-gray-600">Pioneering intelligent solutions for businesses since 2015</p>
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

      {/* Our Story Section */}
      <section className="py-12 md:py-16 lg:py-20">
        <div className="container px-4 md:px-6">
          <div className="grid gap-8 lg:grid-cols-2 lg:gap-12">
            <div className="flex flex-col justify-center space-y-4">
              <div>
                <div className="inline-block rounded-full bg-[#40E0D0]/20 px-3 py-1 text-sm font-medium text-[#005F6B]">
                  Our Story
                </div>
              </div>
              <h2 className="text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl md:text-4xl">
                From Vision to Reality
              </h2>
              <p className="text-gray-600">
                Founded in 2015, SariSmart began with a simple mission: to make intelligent technology accessible to
                businesses of all sizes. What started as a small team of passionate innovators has grown into a global
                company serving clients across industries.
              </p>
              <p className="text-gray-600">
                Our journey has been defined by a commitment to excellence, innovation, and customer success. We believe
                that technology should empower businesses, not complicate them. This philosophy guides everything we do,
                from product development to customer support.
              </p>
            </div>
            <div className="flex items-center justify-center">
              <div className="relative h-[300px] w-full max-w-[500px] overflow-hidden rounded-2xl shadow-lg sm:h-[350px] md:h-[400px]">
                <Image
                  src="/placeholder.svg?height=800&width=800"
                  alt="SariSmart Office"
                  fill
                  className="object-cover"
                />
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Mission & Values Section */}
      <section className="bg-gray-50 py-12 md:py-16 lg:py-20">
        <div className="container px-4 md:px-6">
          <div className="mx-auto max-w-3xl text-center">
            <div className="inline-block rounded-full bg-[#40E0D0]/20 px-3 py-1 text-sm font-medium text-[#005F6B]">
              Mission & Values
            </div>
            <h2 className="mt-4 text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl md:text-4xl">
              What Drives Us Forward
            </h2>
            <p className="mt-4 text-gray-600">
              Our mission and values form the foundation of everything we do at SariSmart.
            </p>
          </div>

          <div className="mt-12 grid gap-8 md:grid-cols-2 lg:grid-cols-3">
            {/* Mission Card */}
            <div className="rounded-xl border bg-white p-6 shadow-sm">
              <h3 className="text-xl font-bold text-[#008080]">Our Mission</h3>
              <p className="mt-2 text-gray-600">
                To empower businesses with intelligent solutions that drive growth, efficiency, and innovation in
                today's digital landscape.
              </p>
            </div>

            {/* Vision Card */}
            <div className="rounded-xl border bg-white p-6 shadow-sm">
              <h3 className="text-xl font-bold text-[#008080]">Our Vision</h3>
              <p className="mt-2 text-gray-600">
                To be the global leader in smart business solutions, recognized for our innovation, reliability, and
                positive impact on our clients' success.
              </p>
            </div>

            {/* Values Card */}
            <div className="rounded-xl border bg-white p-6 shadow-sm">
              <h3 className="text-xl font-bold text-[#008080]">Our Values</h3>
              <ul className="mt-2 space-y-2 text-gray-600">
                <li className="flex items-start">
                  <CheckCircle2 className="mr-2 h-5 w-5 text-[#40E0D0]" />
                  <span>Innovation at our core</span>
                </li>
                <li className="flex items-start">
                  <CheckCircle2 className="mr-2 h-5 w-5 text-[#40E0D0]" />
                  <span>Customer success first</span>
                </li>
                <li className="flex items-start">
                  <CheckCircle2 className="mr-2 h-5 w-5 text-[#40E0D0]" />
                  <span>Integrity in all we do</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </section>

      {/* Team Section */}
      <section className="py-12 md:py-16 lg:py-20">
        <div className="container px-4 md:px-6">
          <div className="mx-auto max-w-3xl text-center">
            <div className="inline-block rounded-full bg-[#40E0D0]/20 px-3 py-1 text-sm font-medium text-[#005F6B]">
              Our Team
            </div>
            <h2 className="mt-4 text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl md:text-4xl">
              Meet the People Behind SariSmart
            </h2>
            <p className="mt-4 text-gray-600">
              Our diverse team of experts is passionate about creating solutions that make a difference.
            </p>
          </div>

          <div className="mt-12 grid gap-8 sm:grid-cols-2 lg:grid-cols-3">
            {/* Team Member 1 */}
            <div className="flex flex-col items-center text-center">
              <div className="relative h-40 w-40 overflow-hidden rounded-full">
                <Image src="/placeholder.svg?height=160&width=160" alt="Team Member" fill className="object-cover" />
              </div>
              <h3 className="mt-4 text-lg font-bold">Nicolo Porter</h3>
              <p className="text-[#008080]">Mobile Developer</p>
              <p className="mt-2 text-sm text-gray-600">
                With over 15 years of experience in technology leadership, Nicolo founded SariSmart with a vision to
                transform how businesses use technology.
              </p>
            </div>

            {/* Team Member 2 */}
            <div className="flex flex-col items-center text-center">
              <div className="relative h-40 w-40 overflow-hidden rounded-full">
                <Image src="/placeholder.svg?height=160&width=160" alt="Team Member" fill className="object-cover" />
              </div>
              <h3 className="mt-4 text-lg font-bold">Michael Leones</h3>
              <p className="text-[#008080]">Web Developer</p>
              <p className="mt-2 text-sm text-gray-600">
                Michael leads our technical strategy and innovation, bringing expertise from his background in AI and
                machine learning.
              </p>
            </div>

            {/* Team Member 3 */}
            <div className="flex flex-col items-center text-center">
              <div className="relative h-40 w-40 overflow-hidden rounded-full">
                <Image src="/placeholder.svg?height=160&width=160" alt="Team Member" fill className="object-cover" />
              </div>
              <h3 className="mt-4 text-lg font-bold">John Kenny Quijote</h3>
              <p className="text-[#008080]">Backend Developer</p>
              <p className="mt-2 text-sm text-gray-600">
                John Kenny ensures our clients get the most value from our solutions, with a focus on long-term partnerships
                and success.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="bg-[#008080] py-12 md:py-16">
        <div className="container px-4 md:px-6">
          <div className="mx-auto max-w-3xl text-center">
            <h2 className="text-2xl font-bold tracking-tight text-white sm:text-3xl md:text-4xl">
              Ready to Transform Your Business?
            </h2>
            <p className="mt-4 text-lg text-white/80">
              Join the thousands of businesses that trust SariSmart for their technology needs.
            </p>
            <div className="mt-8 flex flex-col items-center justify-center gap-4 sm:flex-row">
              <Button asChild size="lg" className="bg-white text-[#008080] hover:bg-gray-100">
                <Link href="/contact">
                  Contact Us
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Link>
              </Button>
              <Button asChild variant="outline" size="lg" className="border-white text-white hover:bg-white/10">
                <Link href="/login">Learn More</Link>
              </Button>
            </div>
          </div>
        </div>
      </section>
    </>
  )
}
