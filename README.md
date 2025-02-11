# 🏪 SariSmart 💡

**SariSmart** is the future of Sari-sari stores, providing innovative solutions to optimize store management, inventory tracking, and customer engagement.

## 📂 Repository Contents Overview

This repository contains the source code, documentation, and necessary resources for SariSmart. The key directories and files include:

- `/backend` - Supabase backend configuration and database setup.
- `/docs` - Documentation and guides.
- `/mobile` - Android Kotlin mobile development files.
- `/web` - Next.js web development files.
- `README.md` - Project overview and setup instructions.

## 👥 Members

| Full Name                    | GitHub Profile                                        | Role                   |
| ---------------------------- | ----------------------------------------------------- | ---------------------- |
| **Leones**, Michael Harry P. | [Saiiph](https://github.com/Saiiph)                   | Web Development        |
| **Porter**, Nicolo Ryne A.   | [nicoryne](https://github.com/nicoryne)               | Mobile Development     |
| **Quijote**, John Kenny C.   | [mnemosyneiscool](https://github.com/mnemosyneiscool) | Backend Administration |

## 🚀 Features

- 📦 **Inventory Management** - Easily track and manage store inventory.
- 💰 **Sales Tracking** - Monitor sales and revenue in real-time.
- 📸 **Snap & Checkout** – Streamline purchases with image recognition.
- 📊 **Analytics Dashboard** - Gain insights from sales and inventory data.
- 🌐 **Cloud Synchronization** - Sync data securely with Supabase backend.

## 🌍 Branching

We follow the Gitflow workflow:

- `main` - Stable production-ready branch.
- `dev` - Latest development updates.
- `feature/{feature-name}` - Feature branches for new functionalities.
- `hotfix/{issue-name}` - Quick fixes for critical bugs.
- `bugfix/{issue-name}` - Bug fixes and minor patches.
- `release/{version}` - Preparation branch for new releases.

## 🛠️ Installation Guide

> **Prerequisites:**
>
> - Node.js & npm installed
> - Supabase account and database setup
> - Git for version control
> - Android Studio (for mobile development)

### Steps:

1. Clone the repository:
   ```sh
   git clone https://github.com/your-repository/sarismart.git
   cd sarismart
   ```
2. Install dependencies for web:
   ```sh
   cd web
   npm install
   ```
3. Install dependencies for mobile:
   ```sh
   cd ../mobile
   ./gradlew build
   ```
4. Configure environment variables:
   ```sh
   cp .env.example .env
   ```
   - Update the `.env` file with necessary API keys and Supabase credentials.
5. Run the web development server:
   ```sh
   npm run dev
   ```
6. Open `http://localhost:3000` in your browser.

## ⚙️ Development Configurations

- **Frontend:** Next.js (React) with Tailwind CSS.
- **Mobile:** Kotlin (Android Native Development).
- **Backend:** Supabase (PostgreSQL, Auth, Storage).
- **State Management:** Redux / Context API.
- **Authentication:** Supabase Auth / JWT.

## 🐝 Contributing

We welcome contributions! Please give us a message if you'd like to contribute!
