# SafeKeep

This project was developed as part of the Michal Sela Hackathon, where our team created **SafeKeep**, an Android application aimed at enhancing the personal safety of women during first dates with unfamiliar individuals.

## Project Overview

SafeKeep empowers users by providing safety features designed to offer reassurance and quick assistance if needed during social interactions.  
The app integrates security measures while maintaining user privacy and ease of use.

## Technologies Used

- **Android** (Kotlin)
- **Firebase** (Authentication, Realtime Database)
- **Google Maps API**
- **Push Notifications**
- **Location Services**
- **Gradle** for build automation

## Key Features

- **Emergency SOS Button**: Quickly send an alert to a trusted contact.
- **Live Location Sharing**: Share real-time location with selected contacts during a date.
- **Check-In System**: Periodic check-ins to ensure the user's well-being.
- **Contact Management**: Select trusted contacts for emergency alerts.
- **Secure Authentication**: Using Firebase Authentication for user login and registration.

## Project Structure

```
.
├── app/                  # Main Android application code
├── gradle/                # Gradle wrapper files
├── build.gradle.kts       # Project build configuration
├── settings.gradle.kts    # Project settings
├── secret.properties      # Secure keys and API configurations
```

## How to Build and Run

1. Clone the repository.
2. Open the project in Android Studio.
3. Set up Firebase credentials and Google Maps API key in `secret.properties`.
4. Sync Gradle and run the application on an Android emulator or physical device.

## Purpose

The purpose of SafeKeep is to provide an added layer of safety in sensitive social situations, helping users feel more secure and connected when meeting new people.
