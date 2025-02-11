# 📱 SariSmart Mobile

Welcome to the **SariSmart** mobile directory! This directory contains the Android Kotlin-based mobile application, designed to provide an optimized experience for managing Sari-sari store operations on the go.

## 📁 Directory Structure

```
/mobile
├── app/                 # Main application source code
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sarismart/  # Kotlin source files
│   │   │   ├── res/                 # UI layouts and resources
├── gradle/              # Gradle build configurations
├── assets/              # Additional resources and data
├── .env.example         # Environment variable template
├── build.gradle         # Project dependencies and settings
├── AndroidManifest.xml  # Application configuration
└── README.md            # Mobile documentation
```

## 🚀 Features

- 📦 **Inventory Tracking** – Manage stock levels directly from your phone.
- 💰 **Sales Processing** – Record transactions and track revenue.
- 📸 **Snap & Checkout** – Use image recognition for fast product entry.
- 📊 **Real-time Analytics** – Get instant insights into sales trends.
- 🌐 **Supabase Integration** – Cloud-sync data for seamless management.

## 🛠️ Setup & Installation

> **Prerequisites:**
>
> - Android Studio installed
> - Supabase account & API setup
> - Kotlin SDK installed

### 1️⃣ Clone the Repository

```sh
git clone https://github.com/your-repository/sarismart.git
cd sarismart/mobile
```

### 2️⃣ Configure Environment Variables

```sh
cp .env.example .env
```

- Add Supabase API keys and necessary credentials.

### 3️⃣ Build the Project

```sh
./gradlew build
```

### 4️⃣ Run the Application

```sh
./gradlew installDebug
```

- Open the app on an emulator or physical device.

## 🔌 API Endpoints Used

| Method | Endpoint     | Description               |
| ------ | ------------ | ------------------------- |
| `GET`  | `/inventory` | Fetch all inventory items |
| `POST` | `/inventory` | Add new inventory item    |
| `GET`  | `/sales`     | Retrieve sales reports    |
| `POST` | `/sales`     | Register a new sale       |

> API details are documented in the `/docs` directory.

## 🛡️ Security & Best Practices

- Store sensitive credentials in environment variables.
- Follow Android security guidelines to protect user data.
- Implement proper input validation and error handling.
- Regularly update dependencies to patch security vulnerabilities.

## 📌 Contributing

We welcome contributions! Feel free to open issues, submit pull requests, or reach out to the team.

## 👨‍💻 Maintainers

| Name                      | GitHub Profile                          |
| ------------------------- | --------------------------------------- |
| **Nicolo Ryne A. Porter** | [nicoryne](https://github.com/nicoryne) |

---

**SariSmart** – Bringing smart store management to your fingertips! 📱🚀
