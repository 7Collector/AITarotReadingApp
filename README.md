# AI Tarot Reading App

## Overview
The AI Tarot Reading App is a mobile application that provides tarot readings powered by AI. Users can interact with the AI via chat to receive tarot card interpretations and insights.
[Download APK](https://raw.githubusercontent.com/7Collector/AITarotReadingApp/refs/heads/main/AITarotReadingApp.apk)

## Features
- AI-powered tarot reading
- Interactive chat interface
- Text-to-speech (TTS) and speech-to-text (STT) support
- Secure API key storage using Gradle properties


## Project Setup

### **1. Prerequisites**
- Android Studio (latest stable version)
- Kotlin & Jetpack Compose
- Google Gemini API Key
- Internet connection

### **2. Clone the Repository**
```sh
 git clone https://github.com/your-repo/ai-tarot-reading-app.git
 cd ai-tarot-reading-app
```

### **3. API Key Configuration**

The app requires a Gemini API Key for AI-generated tarot readings. This key should be stored securely using Gradle Properties and accessed via the Secret Gradle Plugin.

#### **Steps to Configure the API Key**

1. Open `gradle.properties` (located in the root of your project).
2. Add the following line:
    ```properties
    geminiApiKey=your_api_key_here
    ```
3. **DO NOT** commit `gradle.properties` to version control. Add it to `.gitignore`:
    ```sh
    echo 'gradle.properties' >> .gitignore
    ```

#### **Step 3: Access API Key Securely in Code**
Inside your `ChatViewModel` or network request class:
```kotlin
val apiKey = BuildConfig.GEMINI_API_KEY
```
Make sure your `gradle.properties` key is mapped in `build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("geminiApiKey")}"\")
    }
}
```

### **4. Build and Run the Project**

#### **Using Gradle**
```sh
./gradlew build
```

#### **Using Android Studio**
1. Open the project in Android Studio.
2. Select your target device.
3. Click **Run ▶️** to launch the app.

---

## Troubleshooting
- If you face **API key not found** errors, ensure:
  - You correctly added `geminiApiKey` to `gradle.properties`.
  - The **Secret Gradle Plugin** is applied and synced.
- If the app crashes on startup, check **Logcat** for missing dependencies.

---
