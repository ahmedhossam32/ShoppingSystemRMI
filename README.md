# 🛒 Shopping System using Java RMI

## 📌 Project Overview
This project is a **distributed Shopping System** implemented using **Java RMI** and **MongoDB**.  
It follows a clean **Client–Server architecture** and strictly applies the **MVC (Model–View–Controller)** pattern.

The system supports multiple user roles (**Customer, Admin, Delivery Staff**) and demonstrates the practical use of several **software design patterns** in a real-world distributed application.

---

## 🎯 Objectives
- Build a scalable distributed system using Java RMI
- Apply object-oriented principles and design patterns
- Separate concerns using MVC architecture
- Implement role-based functionality
- Persist data using MongoDB
- Create a desktop GUI using Java Swing

---

## 👥 User Roles & Features

### 👤 Customer
- Register and login
- View available products
- Add and remove products from cart
- Place orders
- Track order status
- Receive order status notifications using the Observer Pattern

### 🧑‍💼 Admin
- Login as administrator
- Add new products
- Update existing products
- Delete products
- Manage product inventory

### 🚚 Delivery Staff
- Login as delivery staff
- View assigned orders
- Receive order updates
- Handle the order delivery lifecycle

---

## 🏗️ System Architecture
The system is divided into two independent applications.

### 🖥️ ShoppingServer
- Hosts all business logic
- Handles database operations
- Implements RMI services
- Manages order lifecycle and notifications

### 💻 ShoppingClient
- Contains GUI only using Java Swing
- Sends requests to the server via Java RMI
- Contains no business logic or database code

---

## 🧠 Design Patterns Used

| Pattern  | Description |
|--------|------------|
| MVC | Separates UI, business logic, and data |
| Observer | Notifies customers and delivery staff of order updates |
| State | Manages order lifecycle (Pending, Confirmed, Shipped, Delivered) |
| Strategy | Handles multiple payment methods |
| Facade | Simplifies product viewing functionality |

---

## 🛠️ Technologies Used
- Java  
- Java RMI  
- Java Swing  
- MongoDB  
- Gson  
- MVC Architecture  
- Object-Oriented Programming  

---

## 📂 Project Structure

### Root Project: ShoppingSystemRMI

- ShoppingServer  
  - Database  
    - DB.java  
  - DesignPatterns  
    - Observer  
    - State  
    - Strategy  
    - Facade  
  - Mainclasses  
    - User.java  
    - Customer.java  
    - Admin.java  
    - DeliveryStaff.java  
    - Product.java  
    - Order.java  
    - Cart.java  
    - CartItem.java  
  - rmi  
    - UserInterface.java  
    - ProductInterface.java  
    - AdminProductInterface.java  
    - OrderInterface.java  
    - DeliverystaffInterface.java  
    - PaymentInterface.java  
  - rmi_implementations  
    - UserService.java  
    - ProductService.java  
    - AdminService.java  
    - OrderService.java  
    - DeliverystaffService.java  
    - PaymentService.java  
  - shoppingserver  
    - ShoppingServer.java  

- ShoppingClient  
  - gui  
    - LoginPage.java  
    - CustomerPage.java  
    - AdminPage.java  
    - DeliveryPersonPage.java  
  - controllers  
    - LoginPageController.java  
    - CustomerController.java  
    - AdminController.java  
    - DeliveryStaffController.java  
  - rmi  
    - Shared RMI interfaces  
  - Mainclasses  
    - Serializable DTO classes  
  - shoppingclient  
    - ShoppingClient.java  

- README.md  

---

## 🔌 RMI Services

| Service | Responsibility |
|-------|---------------|
| UserInterface | User registration, login, and profile management |
| ProductInterface | Retrieve product data |
| AdminProductInterface | Admin product management |
| OrderInterface | Order placement, tracking, and assignment |
| DeliverystaffInterface | Delivery staff operations |
| PaymentInterface | Payment processing |

---

## 🗄️ Database
- MongoDB is used as the persistence layer
- Collections used:
  - User
  - Product
  - Order
  - Admin
  - DeliveryStaff
  - Payment
- Gson is used for object serialization and deserialization

---

## 🔔 Observer Flow (Order Notifications)
1. Customer places an order
2. Order attaches the customer as an observer
3. Order status changes on the server
4. Observers are notified automatically
5. Notifications are displayed in the client GUI

---

## 💳 Payment Flow
1. Client selects a payment method
2. Server sets the corresponding payment strategy
3. Payment is processed
4. Result is returned to the client

---

## ▶️ How to Run the Project

### Step 1: Start MongoDB
- Ensure MongoDB is running on `localhost:27017`

### Step 2: Run RMI Server
- Run `ShoppingServer.main()`

### Step 3: Run Client
- Run `ShoppingClient.main()`

---

## 📌 Important Rules Followed
- No database access on the client side
- No business logic inside GUI classes
- Controllers communicate with the server using RMI only
- Domain models are Serializable
- Design patterns are implemented on the server side

---

## ✅ Project Status
- Fully implemented
- Stable and tested
- Ready for final submission

---

## 👨‍💻 Author
**Ahmed Hossam**

---

## 📜 License
This project is developed for educational purposes only.
