# 🏠 Shelf Sense

### Smart Home Inventory & Expiry Tracker

Shelf Sense is an Android application designed to help users manage household inventory, track item quantities, monitor expiry dates, and receive notifications for important inventory events.

The application provides a simple dashboard where users can quickly see their inventory status, expiring items, expired items, and low-stock items.

---

## 📱 Project Overview

Managing household items manually can make it easy to forget expiry dates or run out of important products.

**Shelf Sense** provides a centralized solution for:

- Managing household inventory
- Tracking item quantities
- Recording purchase and expiry dates
- Monitoring upcoming expiry dates
- Identifying expired products
- Detecting low-stock items
- Searching and filtering inventory
- Sorting inventory items
- Viewing expiry dates on a calendar
- Receiving expiry and low-stock notifications

---

## ✨ Features

### 📊 Dashboard

The dashboard provides a quick overview of the inventory.

It displays:

- Total inventory items
- Items expiring within 7 days
- Low-stock items
- Expired items
- Upcoming expiry items
- Expiry calendar

---

### 📦 Inventory Management

Users can add household items with details such as:

- Item name
- Category
- Quantity
- Unit
- Purchase date
- Expiry date
- Notes

Supported categories include:

- Food/Grocery
- Personal Care
- Medicine
- Cleaning
- Other

Supported units include:

- Piece
- Packet
- Bottle
- Box
- Kg
- Gram
- Liter
- ml
- Dozen
- Other

---

### 🔍 Search & Filter

The inventory can be searched using the item name.

Users can also filter items by category:

- All
- Food/Grocery
- Personal Care
- Medicine
- Cleaning
- Other

---

### ↕️ Sorting

Inventory items can be sorted by:

- Name A-Z
- Name Z-A
- Expiry Soonest
- Expiry Latest
- Quantity Low-High
- Quantity High-Low

---

### ⏰ Expiry Tracking

Shelf Sense automatically calculates the expiry status of each item.

Items can be identified as:

- Expired
- Expires Today
- Expires Tomorrow
- Expires within 7 days
- Upcoming

---

### 📅 Expiry Calendar

The application includes a custom expiry calendar.

The calendar provides:

- Current month display
- Previous/next month navigation
- Expiry indicators
- Different indicators for expired and upcoming items
- Date selection
- Items expiring on a selected date

---

### 🔔 Notifications

Shelf Sense uses **WorkManager** to periodically check inventory.

Notifications can be generated for:

- Expired items
- Items expiring within 7 days
- Low-stock items

The application also requests notification permission on supported Android versions.

---

### ✏️ Edit & Delete

Users can open any inventory item to view its complete details.

From the details screen, users can:

- Edit item information
- Change quantity
- Change unit
- Change category
- Change purchase date
- Change expiry date
- Update notes
- Delete the item

---

## 🛠️ Technologies Used

| Technology | Usage |
|---|---|
| Kotlin | Application development |
| XML | User interface |
| Android Studio | Development environment |
| RecyclerView | Displaying inventory lists |
| SharedPreferences | Local data storage |
| JSON | Storing item data |
| WorkManager | Background notification scheduling |
| DatePickerDialog | Selecting dates |
| Custom View | Expiry calendar |
| Material/Android UI components | Interface elements |

---

## 🏗️ Project Architecture

Shelf Sense uses a simple Android architecture based on Activities, RecyclerView, and local storage.

```text
Shelf Sense
│
├── MainActivity
│   └── Dashboard
│
├── InventoryActivity
│   └── Inventory / Search / Filter / Sort
│
├── AddItemActivity
│   └── Add new item
│
├── ItemDetailsActivity
│   └── View item details
│
├── EditItemActivity
│   └── Edit existing item
│
├── ExpiringItemsActivity
│   └── Expiring items
│
├── ItemAdapter
│   └── RecyclerView adapter
│
├── StorageManager
│   └── SharedPreferences + JSON
│
├── ExpiryUtils
│   └── Expiry calculations
│
├── ExpiryCalendarView
│   └── Custom expiry calendar
│
└── ExpiryNotificationWorker
    └── Background notifications
