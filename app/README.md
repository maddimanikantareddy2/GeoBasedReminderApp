# Geo-Based Reminder App 📍⏰

## Overview
The **Geo-Based Reminder App** is an Android application that allows users to create location-based reminders.  
Instead of triggering reminders based on time, the app notifies users when they **arrive at or leave a specific location**, ensuring important tasks are not missed.


---

## Features 🚀

- 📍 **Location-Based Reminders**
    - Set reminders by selecting a location on Google Maps
    - Choose trigger type: **Arrive** or **Leave**

- 🗺️ **Map Integration**
    - Select location using Google Maps
    - Visualize reminder radius on the map (100m – 500m)

- 🔔 **Smart Notifications**
    - Receive notifications when entering or exiting the selected location
    - Works even when the app is in the background

- 🏠 **Home Dashboard**
    - Overview of upcoming reminders
    - Location & permission status indicators
    - Quick actions to add or view reminders

- 📋 **Saved & History Screens**
    - View all saved reminders
    - Separate **Upcoming** and **Triggered (Past)** reminders

- 👤 **User Profile**
    - Displays user details (Name, Email, DOB, Place)
    - Logout functionality

---

## Tech Stack 🛠️

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** MVVM
- **Local Database:** Room
- **Maps & Location:**
    - Google Maps SDK
    - Google Play Services (Location & Geofencing)
- **Backend (User Data):** Firebase Realtime Database
- **Notifications:** Android Notification Manager

---

## Permissions Used 🔐

The app requires the following permissions:

- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`
- `ACCESS_BACKGROUND_LOCATION`
- `POST_NOTIFICATIONS` (Android 13+)
- `INTERNET`

These permissions are required to ensure accurate location tracking and reliable reminder notifications.

---

## App Screens 📱

- Home Screen
- Add Reminder Screen (Map-based selection)
- Saved Reminders Screen
- Reminder History (Upcoming & Past)
- Profile Screen
- About & Contact Screen

---

## How It Works ⚙️

1. User selects a location on the map
2. Defines radius and trigger type (Arrive / Leave)
3. Reminder is saved locally
4. A geofence is registered using Google Play Services
5. When the user enters or exits the location:
    - Geofence is triggered
    - Notification is shown
    - Reminder is marked as triggered


---

## Developer 👨‍💻

- **Student Number:** S3521330
- **Email:** maddimanikantareddy2@gmail.com

---
