# Kanhaiya Jewellers – Credit Manager

<img src="app/src/main/res/drawable/ic_app_logo.xml" width="80" />

A premium Android credit management app for jewellery shops. Track customer credit, record transactions, and manage payments — all offline with a stunning black + gold luxury UI.

---

## 📋 Tech Stack

| Layer      | Technology                      |
|------------|---------------------------------|
| Language   | Kotlin                          |
| Architecture | MVVM + Clean Architecture     |
| Database   | Room (SQLite, offline-first)    |
| UI         | Material 3, ViewBinding        |
| Navigation | Navigation Component + Safe Args|
| Async      | Kotlin Coroutines + LiveData    |
| Min SDK    | 26 (Android 8.0)               |

---

## 🚀 Setup & Run Instructions

### Prerequisites
- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK 17**
- **Android SDK** with API 34 installed

### Steps

1. **Open in Android Studio**
   ```
   File → Open → Select /kanhaiyajwellers folder
   ```

2. **Sync Gradle**
   Android Studio will automatically prompt to sync. Accept it, or run:
   ```
   File → Sync Project with Gradle Files
   ```

3. **Generate Gradle Wrapper** (first-time only, if gradlew is missing)
   In Android Studio Terminal:
   ```bash
   gradle wrapper --gradle-version 8.6
   chmod +x gradlew
   ```

4. **Run the App**
   - Select an emulator (Pixel 6, API 33+) or connect a physical device
   - Click ▶ Run, or use:
   ```bash
   ./gradlew installDebug
   ```

5. **Build Release APK**
   ```bash
   ./gradlew assembleRelease
   # Output: app/build/outputs/apk/release/app-release-unsigned.apk
   ```

---

## 📁 Project Structure

```
kanhaiyajwellers/
├── app/src/main/
│   ├── java/com/kanhaiyajewellers/creditmanager/
│   │   ├── KanhaiyaJewellersApp.kt          # Application class
│   │   ├── data/
│   │   │   ├── db/
│   │   │   │   ├── AppDatabase.kt           # Room singleton
│   │   │   │   ├── dao/
│   │   │   │   │   ├── CustomerDao.kt
│   │   │   │   │   └── TransactionDao.kt
│   │   │   │   └── entity/
│   │   │   │       ├── Customer.kt
│   │   │   │       └── Transaction.kt
│   │   │   └── model/
│   │   │       └── TransactionWithCustomer.kt
│   │   └── ui/
│   │       ├── ViewModelFactory.kt          # Unified factory
│   │       ├── splash/SplashActivity.kt
│   │       ├── main/MainActivity.kt
│   │       ├── dashboard/
│   │       │   ├── DashboardFragment.kt
│   │       │   ├── DashboardViewModel.kt
│   │       │   └── TransactionAdapter.kt
│   │       ├── search/
│   │       │   ├── SearchFragment.kt
│   │       │   └── SearchViewModel.kt
│   │       ├── addtransaction/
│   │       │   ├── AddTransactionFragment.kt
│   │       │   └── AddTransactionViewModel.kt
│   │       ├── transactiondetail/
│   │       │   ├── TransactionDetailFragment.kt
│   │       │   └── TransactionDetailViewModel.kt
│   │       └── addpayment/
│   │           └── AddPaymentBottomSheet.kt
│   └── res/
│       ├── anim/          # Slide transition animations
│       ├── color/         # Color state lists (nav selector)
│       ├── drawable/      # Drawables + vector icons
│       ├── layout/        # All screen layouts
│       ├── menu/          # Bottom nav menu
│       ├── navigation/    # nav_graph.xml
│       └── values/        # colors, strings, themes, dimens
```

---

## 🎨 Design System

| Token                | Value     |
|----------------------|-----------|
| Background Primary   | `#0B0B0B` |
| Background Secondary | `#121212` |
| Card                 | `#1A1A1A` |
| Gold Primary         | `#D4AF37` |
| Gold Accent          | `#E6C76A` |
| Gold Dark            | `#A67C00` |
| Text Primary         | `#FFFFFF` |
| Text Secondary       | `#BFBFBF` |
| Pending (Red)        | `#FF4D4D` |
| Completed (Green)    | `#4CAF50` |

---

## 📱 Screens

| Screen               | Description                                              |
|----------------------|----------------------------------------------------------|
| Splash               | KJ monogram + app name for 2.5s                         |
| Dashboard            | 3 stat cards, recent transactions list, gold FAB         |
| Search               | Instant phone/name search with live results             |
| Add Transaction      | Form with real-time remaining = total − paid            |
| Transaction Detail   | Full breakdown with status badge + Add Payment           |
| Add Payment          | Bottom sheet to record partial payments                  |

---

## 🧠 Data Model

```
customers
  id          INTEGER PK
  name        TEXT
  phone       TEXT UNIQUE

transactions
  id              INTEGER PK
  customer_id     INTEGER FK → customers.id (CASCADE)
  item_name       TEXT
  total_amount    REAL
  paid_amount     REAL
  remaining_amount REAL
  status          TEXT ('PENDING' | 'COMPLETED')
  created_at      INTEGER (Unix ms)
```

---

## ✅ Business Rules

- **Auto-status**: `remaining > 0` → PENDING, `remaining == 0` → COMPLETED
- **Deduplication**: Same phone → reuses existing Customer record
- **Offline-first**: All data stored locally in Room (no internet required)
- **Cascade delete**: Deleting a customer removes all their transactions

---

## 🔧 Optional Enhancements (Future)

- [ ] Export transactions as PDF/CSV
- [ ] WhatsApp payment reminder integration
- [ ] Customer-wise transaction history screen
- [ ] Date range filters on Dashboard
- [ ] Firebase Firestore sync for cloud backup
