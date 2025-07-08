# iStock Inventory Manager

A modern Android application built with Kotlin and Jetpack Compose for comprehensive inventory management and shopping list functionality.

## Features

### 📦 Inventory Management
- **User-titled Categories**: Organize items using a folder-file paradigm
- **Stock Tracking**: Monitor quantities with low stock alerts
- **Expiration/Warranty Dates**: Track important dates with notifications
- **Real-time Updates**: Instant updates across the application

### 🛒 Shopping List
- **Output Shopping Lists**: Generate and manage shopping lists
- **Priority System**: Organize items by priority (Low, Medium, High)
- **Completion Tracking**: Mark items as completed and manage completed items
- **Notes**: Add additional notes to shopping items

### 🔔 Notification System
- **Low Stock Alerts**: Get notified when items are running low
- **Expiration Notifications**: Alerts for items about to expire (7 days)
- **Warranty Notifications**: Alerts for warranties expiring soon (30 days)
- **Background Processing**: Uses WorkManager for reliable notifications

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **Dependency Injection**: Hilt/Dagger
- **Navigation**: Navigation Compose
- **Background Tasks**: WorkManager
- **Notifications**: Android Notification System

## Architecture Overview

```
┌─────────────────┐
│   UI Layer      │  ← Compose Screens & ViewModels
├─────────────────┤
│ Repository      │  ← Data abstraction layer
├─────────────────┤
│   Room DB       │  ← Local database (SQLite)
├─────────────────┤
│ Notifications   │  ← WorkManager & NotificationManager
└─────────────────┘
```

## Project Structure

```
com.istock.inventorymanager/
├── data/
│   ├── dao/              # Data Access Objects
│   ├── database/         # Room database setup
│   ├── model/           # Data models
│   └── repository/      # Repository implementations
├── di/                  # Dependency injection modules
├── notification/        # Notification system
├── ui/
│   ├── navigation/      # Navigation setup
│   ├── screen/         # Compose screens
│   ├── theme/          # App theming
│   └── viewmodel/      # ViewModels
└── MainActivity.kt     # Main application entry point
```

## Database Schema

### Categories
- ID (Primary Key)
- Name
- Description
- Created Date

### Inventory Items
- ID (Primary Key)
- Name
- Description
- Category ID (Foreign Key)
- Quantity
- Minimum Stock Level
- Expiration Date (Optional)
- Warranty Date (Optional)
- Price
- Created/Updated Dates

### Shopping List Items
- ID (Primary Key)
- Inventory Item ID (Optional Foreign Key)
- Name
- Quantity
- Priority (1=Low, 2=Medium, 3=High)
- Completion Status
- Notes
- Created Date

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Kotlin 1.9.22 or later
- Android SDK API 24 (Android 7.0) or higher
- Target SDK API 35

### Installation
1. Clone the repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Run the application

### Permissions
The app requires the following permissions:
- `POST_NOTIFICATIONS` - For displaying notifications
- `SCHEDULE_EXACT_ALARM` - For scheduling precise notifications
- `USE_EXACT_ALARM` - For alarm functionality

## Usage

### Managing Categories
1. Navigate to the Categories tab
2. Tap the "+" button to add a new category
3. Enter category name and optional description
4. Use edit/delete buttons to manage existing categories

### Managing Inventory
1. Navigate to the Inventory tab
2. Filter by category using the filter chips
3. Tap "+" to add new inventory items
4. Set quantities, expiration dates, and warranty information
5. Monitor low stock alerts and expiring items

### Managing Shopping Lists
1. Navigate to the Shopping List tab
2. Add items with quantity and priority
3. Check off completed items
4. Use "Clear Completed" to remove finished items

### Notifications
- The app automatically checks for low stock and expiring items
- Notifications are sent based on configured thresholds
- Tap notifications to open the app

## Development

### Building the Project
```bash
./gradlew assembleDebug
```

### Running Tests
```bash
./gradlew test
```

### Code Style
This project follows standard Kotlin coding conventions and uses:
- Detekt for static code analysis
- ktlint for formatting

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Future Enhancements

- [ ] Barcode scanning for easy item addition
- [ ] Data export/import functionality
- [ ] Multi-location inventory tracking
- [ ] Analytics and reporting
- [ ] Cloud synchronization
- [ ] Photo attachments for items
- [ ] Advanced search and filtering
- [ ] Bulk operations
