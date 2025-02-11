# 🏢 SariSmart Backend

Welcome to the **SariSmart** backend! This directory contains the backend configuration, database setup, and server-side logic for the SariSmart platform, utilizing **Supabase** for authentication, database management, and cloud storage.

## 📁 Directory Structure

```
/backend
├── migrations/        # Database migration files
├── functions/         # Supabase Edge functions
├── src/
│   ├── models/       # Database schema models
│   ├── controllers/  # API route logic
│   ├── services/     # Business logic and utilities
│   ├── routes/       # Express API routes (if applicable)
├── .env.example      # Environment variable template
├── supabase/         # Supabase configuration files
├── package.json      # Dependencies and scripts
└── README.md         # Backend documentation
```

## 🚀 Features

- 🔑 **Authentication** – Secure user authentication with Supabase Auth.
- 📦 **Inventory Management** – Database models for tracking store inventory.
- 📊 **Analytics & Reports** – Backend logic to generate sales and inventory reports.
- 🔄 **API Services** – RESTful API endpoints for frontend and mobile integration.
- ☁️ **Cloud Storage** – Image and file storage via Supabase Storage.

## 🛠️ Setup & Installation

> **Prerequisites:**
>
> - Node.js & npm installed
> - Supabase account & project set up
> - PostgreSQL database initialized

### 1️⃣ Clone the Repository

```sh
git clone https://github.com/your-repository/sarismart.git
cd sarismart/backend
```

### 2️⃣ Install Dependencies

```sh
npm install
```

### 3️⃣ Configure Environment Variables

```sh
cp .env.example .env
```

- Fill in the required Supabase credentials (Database URL, API keys, etc.)

### 4️⃣ Run Database Migrations

```sh
npm run migrate
```

### 5️⃣ Start the Development Server

```sh
npm run dev
```

## 🔌 API Endpoints

| Method | Endpoint       | Description               |
| ------ | -------------- | ------------------------- |
| `POST` | `/auth/signup` | Register a new user       |
| `POST` | `/auth/login`  | User authentication       |
| `GET`  | `/inventory`   | Fetch all inventory items |
| `POST` | `/inventory`   | Add new inventory item    |
| `GET`  | `/sales`       | Retrieve sales reports    |

> Full API documentation available in the `/docs` directory.

## 🛡️ Security & Best Practices

- Use environment variables to store sensitive credentials.
- Validate all user inputs to prevent SQL injection.
- Implement authentication & role-based access control.
- Regularly update dependencies for security patches.

## 📌 Contributing

We welcome contributions! Feel free to open issues, submit pull requests, or reach out to the team.

## 👨‍💻 Maintainers

| Name                      | GitHub Profile                                        |
| ------------------------- | ----------------------------------------------------- |
| **John Kenny C. Quijote** | [mnemosyneiscool](https://github.com/mnemosyneiscool) |

---

**SariSmart** – Empowering small businesses with smart technology! 🚀
