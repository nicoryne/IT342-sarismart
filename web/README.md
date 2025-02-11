# 🌐 SariSmart Web

Welcome to the **SariSmart** web directory! This directory contains the **Next.js** web application, designed to provide an intuitive and responsive interface for managing Sari-sari store operations efficiently.

## 📁 Directory Structure

```
/web
├── public/              # Static assets (images, icons, etc.)
├── src/
│   ├── components/      # Reusable UI components
│   ├── app/             # Next.js app (routes)
│   ├── hooks/           # Custom React hooks
│   ├── context/         # Context API for state management
│   ├── services/        # API services for backend communication
│   ├── styles/          # Global and component-specific styles
|   |── lib/             # Utility library
├── .env.example         # Environment variable template
├── package.json         # Dependencies and scripts
├── next.config.js       # Next.js configuration
├── tailwind.config.js   # Tailwind CSS configuration
├── README.md            # Web documentation
```

## 🚀 Features

- 📦 **Inventory Management** – Easily add, update, and track store inventory.
- 💰 **Sales Monitoring** – View and analyze sales data in real-time.
- 📸 **Snap & Checkout** – Streamline purchases with AI-powered image recognition.
- 📊 **Dashboard Analytics** – Get actionable insights from sales trends.
- 🌐 **Supabase Integration** – Secure cloud storage and authentication.

## 🛠️ Setup & Installation

> **Prerequisites:**
>
> - Node.js & npm installed
> - Supabase account & API setup
> - Tailwind CSS installed (optional but recommended)

### 1️⃣ Clone the Repository

```sh
git clone https://github.com/your-repository/sarismart.git
cd sarismart/web
```

### 2️⃣ Install Dependencies

```sh
npm install
```

### 3️⃣ Configure Environment Variables

```sh
cp .env.example .env
```

- Fill in the required Supabase credentials (API keys, database URL, etc.)

### 4️⃣ Run the Development Server

```sh
npm run dev
```

- Open `http://localhost:3000` in your browser.

## 🔌 API Endpoints Used

| Method | Endpoint     | Description               |
| ------ | ------------ | ------------------------- |
| `GET`  | `/inventory` | Fetch all inventory items |
| `POST` | `/inventory` | Add new inventory item    |
| `GET`  | `/sales`     | Retrieve sales reports    |
| `POST` | `/sales`     | Register a new sale       |

> API details are documented in the `/docs` directory.

## 🎨 UI & Styling

- **Framework:** Next.js (React-based SSR & SSG)
- **Styling:** Tailwind CSS for modern UI design
- **State Management:** React Context API / Redux (if needed)
- **Authentication:** Supabase Auth (JWT-based login system)

## 🛡️ Security & Best Practices

- Store sensitive credentials in environment variables.
- Sanitize and validate user inputs to prevent security vulnerabilities.
- Optimize images and assets for better performance.
- Regularly update dependencies to prevent security issues.

## 📌 Contributing

We welcome contributions! Feel free to open issues, submit pull requests, or reach out to the team.

## 👨‍💻 Maintainers

| Name                        | GitHub Profile                      |
| --------------------------- | ----------------------------------- |
| **Michael Harry P. Leones** | [Saiiph](https://github.com/Saiiph) |

---

**SariSmart** – Empowering small businesses with smart technology! 🚀🌍
