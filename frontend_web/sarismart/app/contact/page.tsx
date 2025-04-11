"use client"

import type React from "react"

import { useState } from "react"
import Link from "next/link"
import { ArrowRight, Mail, MapPin, Phone } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

export default function ContactPage() {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    company: "",
    subject: "",
    message: "",
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSelectChange = (value: string) => {
    setFormData((prev) => ({ ...prev, subject: value }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    // In a real application, you would handle form submission here
    console.log("Form submitted:", formData)
    alert("Thank you for your message! We'll get back to you soon.")
    setFormData({
      name: "",
      email: "",
      company: "",
      subject: "",
      message: "",
    })
  }

  return (
    <>
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-b from-white to-gray-50 py-12 sm:py-16 md:py-20">
        <div className="container relative z-10 px-4 md:px-6">
          <div className="mx-auto max-w-3xl text-center">
            <h1 className="text-3xl font-bold tracking-tight text-gray-900 sm:text-4xl md:text-5xl">Get in Touch</h1>
            <p className="mt-4 text-lg text-gray-600">
              Have questions or ready to start your journey with SariSmart? We're here to help.
            </p>
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

      {/* Contact Form Section */}
      <section className="py-12 md:py-16 lg:py-20">
        <div className="container px-4 md:px-6">
          <div className="grid gap-10 lg:grid-cols-2">
            {/* Contact Information */}
            <div className="flex flex-col justify-center space-y-8">
              <div>
                <div className="inline-block rounded-full bg-[#40E0D0]/20 px-3 py-1 text-sm font-medium text-[#005F6B]">
                  Contact Information
                </div>
                <h2 className="mt-4 text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl">
                  We'd Love to Hear From You
                </h2>
                <p className="mt-4 text-gray-600">
                  Whether you have a question about our products, pricing, or just want to say hello, we're ready to
                  answer all your questions.
                </p>
              </div>

              <div className="space-y-6">
                <div className="flex items-start">
                  <MapPin className="mr-4 h-6 w-6 text-[#008080]" />
                  <div>
                    <h3 className="font-medium">Our Office</h3>
                    <p className="mt-1 text-gray-600">
                      123 Tech Park Avenue
                      <br />
                      San Francisco, CA 94107
                      <br />
                      United States
                    </p>
                  </div>
                </div>

                <div className="flex items-start">
                  <Phone className="mr-4 h-6 w-6 text-[#008080]" />
                  <div>
                    <h3 className="font-medium">Phone</h3>
                    <p className="mt-1 text-gray-600">+1 (555) 123-4567</p>
                    <p className="text-sm text-gray-500">Monday-Friday, 9AM-6PM PST</p>
                  </div>
                </div>

                <div className="flex items-start">
                  <Mail className="mr-4 h-6 w-6 text-[#008080]" />
                  <div>
                    <h3 className="font-medium">Email</h3>
                    <p className="mt-1 text-gray-600">info@sarismart.com</p>
                    <p className="text-sm text-gray-500">We'll respond as soon as possible</p>
                  </div>
                </div>
              </div>

              <div className="rounded-xl bg-[#40E0D0]/10 p-6">
                <h3 className="font-medium text-[#005F6B]">Looking for Support?</h3>
                <p className="mt-2 text-gray-600">
                  Our dedicated support team is available to help with technical issues and questions.
                </p>
                <Button asChild variant="link" className="mt-2 p-0 text-[#008080]">
                  <Link href="/support">
                    Visit our Support Center
                    <ArrowRight className="ml-1 h-4 w-4" />
                  </Link>
                </Button>
              </div>
            </div>

            {/* Contact Form */}
            <div className="rounded-xl border bg-white p-6 shadow-sm md:p-8">
              <h3 className="text-xl font-bold text-gray-900">Send Us a Message</h3>
              <p className="mt-2 text-gray-600">
                Fill out the form below and we'll get back to you as soon as possible.
              </p>

              <form onSubmit={handleSubmit} className="mt-6 space-y-6">
                <div className="grid gap-4 sm:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="name">Full Name</Label>
                    <Input
                      id="name"
                      name="name"
                      placeholder="John Doe"
                      required
                      value={formData.name}
                      onChange={handleChange}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="email">Email</Label>
                    <Input
                      id="email"
                      name="email"
                      type="email"
                      placeholder="john@example.com"
                      required
                      value={formData.email}
                      onChange={handleChange}
                    />
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="company">Company (Optional)</Label>
                  <Input
                    id="company"
                    name="company"
                    placeholder="Your Company"
                    value={formData.company}
                    onChange={handleChange}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="subject">Subject</Label>
                  <Select onValueChange={handleSelectChange} value={formData.subject}>
                    <SelectTrigger id="subject">
                      <SelectValue placeholder="Select a subject" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="general">General Inquiry</SelectItem>
                      <SelectItem value="sales">Sales Question</SelectItem>
                      <SelectItem value="support">Technical Support</SelectItem>
                      <SelectItem value="partnership">Partnership Opportunity</SelectItem>
                      <SelectItem value="other">Other</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="message">Message</Label>
                  <Textarea
                    id="message"
                    name="message"
                    placeholder="How can we help you?"
                    rows={5}
                    required
                    value={formData.message}
                    onChange={handleChange}
                  />
                </div>

                <Button type="submit" className="w-full bg-[#008080] text-white hover:bg-[#005F6B]">
                  Send Message
                </Button>
              </form>
            </div>
          </div>
        </div>
      </section>

      {/* Map Section */}
      <section className="bg-gray-50 py-12 md:py-16">
        <div className="container px-4 md:px-6">
          <div className="mx-auto max-w-3xl text-center">
            <h2 className="text-2xl font-bold tracking-tight text-gray-900">Visit Our Office</h2>
            <p className="mt-2 text-gray-600">We're located in the heart of San Francisco's tech district.</p>
          </div>

          <div className="mt-8 overflow-hidden rounded-xl border shadow-sm">
            <div className="aspect-video w-full bg-gray-200">
              {/* In a real application, you would embed a map here */}
              <div className="flex h-full items-center justify-center">
                <p className="text-gray-500">Map Placeholder</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  )
}
