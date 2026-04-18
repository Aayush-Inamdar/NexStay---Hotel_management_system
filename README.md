# NexStay — Hotel Management System

> A JavaFX-based Hotel Management System featuring smart room recommendation, billing management, and persistent file storage, built with Maven.

---

## Features

### Core
- **Dashboard** — Live stats: total rooms, available, occupied, and total revenue
- **Room Management** — Add and view rooms with type, pricing, and availability
- **Smart Book** — Intelligent room recommendation based on budget and number of guests
- **Billing** — Auto-generated bills at checkout with full receipt popup
- **Customer Management** — View all guests, select and checkout with one click

### Technical
- Persistent data storage using Java Serialization (`.dat` files)
- Full input validation — name (letters only), contact (10 digits), date logic (checkout after checkin)
- DatePicker for check-in and check-out selection
- Duplicate room number prevention
- Dashboard refreshes live on every visit
- Bill receipt popup on every checkout

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 25 | Core language |
| JavaFX 21 | GUI framework |
| Maven 3.9+ | Build and dependency management |
| FXML + Scene Builder | UI layout definition |
| Java Serialization | File-based data persistence |
| CSS | Custom theming |

---

## Project Structure

```
NexStay/
├── pom.xml
└── src/
    └── main/
        ├── java/com/nexstay/
        │   ├── App.java
        │   ├── model/
        │   │   ├── Room.java
        │   │   ├── Customer.java
        │   │   └── Billing.java
        │   ├── util/
        │   │   └── DataStore.java
        │   └── controller/
        │       ├── MainController.java
        │       ├── DashboardController.java
        │       ├── RoomsController.java
        │       ├── SmartBookController.java
        │       ├── BillingController.java
        │       └── CustomersController.java
        └── resources/com/nexstay/
            ├── MainLayout.fxml
            ├── Dashboard.fxml
            ├── Rooms.fxml
            ├── SmartBook.fxml
            ├── Billing.fxml
            ├── Customers.fxml
            └── styles/
                └── style.css
```

---

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.9+

### Run the App

```bash
git clone https://github.com/yourusername/NexStay.git
cd NexStay
mvn javafx:run
```

That's it. Maven handles all JavaFX dependencies automatically — no manual installation needed.

---

## How It Works

### Smart Book
The standout feature. Enter your budget and number of guests — NexStay filters all available rooms where price is within budget and capacity meets your requirement, then recommends the best value room. One click takes you straight to booking.

### Data Persistence
All data (rooms, customers, billing records) is saved to `.dat` files using Java Object Serialization. Data persists between sessions without any database.

### Checkout Flow
1. Select a customer from the Customers table
2. Click Checkout Selected Customer
3. System calculates nights stayed and total bill automatically
4. A styled receipt popup appears
5. Billing record is saved, room is marked available, customer is removed

---

## OOP Concepts Demonstrated

| Concept | Where Used |
|---|---|
| Encapsulation | Room, Customer, Billing model classes with private fields and getters/setters |
| Abstraction | Controller classes abstract UI logic from data logic |
| Generics | `ArrayList<Room>`, `ArrayList<Customer>`, `ArrayList<Billing>` |
| Serialization | All model classes implement `Serializable` |
| Collections | ArrayList, Iterator used throughout DataStore |
| Multithreading | JavaFX Application Thread model |
| JavaFX Event Handling | `setOnAction()` on all interactive controls |

---

## Screens

| Screen | Description |
|---|---|
| Dashboard | Live occupancy and revenue overview |
| Rooms | Add and manage hotel rooms |
| Smart Book | Budget-based room recommendation and booking |
| Billing | Complete billing history and total revenue |
| Customers | Guest records and checkout management |

---

## Color Theme

| Element | Color |
|---|---|
| Sidebar | Burgundy `#6b1a1a` |
| Accents & Buttons | Gold `#c9a84c` |
| Main Background | White `#ffffff` |
| Table Headers | Burgundy `#6b1a1a` |

---

## Academic Context

Built as the Week 10 capstone project for the Object-Oriented Software Development Lab (OSDL), covering concepts from all 9 preceding weeks, including OOP, wrapper classes, multithreading, I/O streams, serialization, generics, collections, and JavaFX.

---

## License

This project is for academic purposes.
