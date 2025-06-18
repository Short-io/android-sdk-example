
# 📱 ShortIOApp – Android Sample Project for ShortIOSDK

**ShortIOApp** is a sample Android project that demonstrates how to integrate and use the [ShortIOSDK](https://github.com/Short-io/android-sdk.git) for generating short links and handling deep links using [Short.io](https://short.io/).

This project helps developers understand how to:

- Set up and use `ShortIOSDK`
- Generate short URLs with customizable parameters
- Integrate and handle Deep Links

## 📦 Requirements

- MinSdk of Android project should be greater than or equal to 21
- A valid [Short.io](https://short.io/) account

## 🚀 Getting Started

### Clone the Repository

```bash
git clone https://github.com/Short-io/android-sdk-example
```

### Open the Project

Open Android Studio, and open the `android-sdk-example` folder in Android Studio.

## 🛠 Setup Instructions

### 1. Add Your API Key

Open the Main Activity file and replace the placeholder with your Short.io Public API Key:

```bash
val apiKey = "your_api_key"
```

### 🔗 Need help finding your API key?

Follow this guide in the [ShortIOSDK README](https://github.com/Short-io/android-sdk#:~:text=Step%201%3A%20Get,your%20API%20key.).

### 2. Add Your Required Parameters

In your MainActivity file replace the placeholder with your Short.io domain and originalURL:

```kotlin
val params = ShortIOParameters(
    domain = "your_domain", // e.g., example.short.gy
    originalURL = "https://{your_domain}" // The destination URL
)
``` 

## 💡 How It Works

The app demonstrates:

### ✅ Generating Short Links

Using your domain and original URL, you can generate a short link like this:

```kotlin
val apiKey = "your_api_key"

val params = ShortIOParameters(
    domain = "your_domain", // e.g., example.short.gy
    originalURL = "https://{your_domain}" // The destination URL
)

when (val result = ShortioSdk.shortenUrl(apiKey, params)) {
    is ShortIOResult.Success -> {
        val shortUrl = result.data.shortURL
        Log.d("ShortIO", "Shortened URL: $shortUrl")
    }
    is ShortIOResult.Error -> {
        val error = result.data
        Log.e("ShortIO", "Error:- ${error.message}")
    }
}
```
## 🤖 Deep Linking Setup
To handle deep links via Short.io on Android, you'll need to set up Android App Links properly using your domain's Digital Asset Links and intent filters.

### 🔧 Step 1: Configure Intent Filter in AndroidManifest.xml

1. Open your Android project.

2. Navigate to android/app/src/main/AndroidManifest.xml.

3. Inside your MainActivity, add an intent filter to handle app links:
```
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:launchMode="singleTask">
    
    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        
        <data
            android:scheme="https"
            android:host="yourshortdomain.short.gy" />
    </intent-filter>
</activity>
```
✅ Tip: Replace yourshortdomain.short.gy with your actual Short.io domain.

### 🛡️ Step 2: Configure Asset Links on Short.io

1. Go to Short.io.

2. Navigate to Domain Settings > Deep links for your selected domain.

3. In the Android Package Name field, enter your app's package name (e.g., com.example.app).

4. In the SHA-256 Certificate Fingerprint field, enter your release key’s SHA-256 fingerprint.
```
// Example Package:
com.example.app

// Example SHA-256:
A1:B2:C3:D4:E5:F6:...:Z9
```
You can retrieve the fingerprint using the following command:

```
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```
📌 Note: Use the SHA-256 of your release keystore for production builds.

5. Click Save to update the Digital Asset Links.

### 🚦 Step 3: Enable the Link in "Open by Default"

1. Build and install your app on the device.

2. Go to **App Settings > Open by Default**.

3. Tap on **“Add link”** under the **Open by Default** section.

4. Add your URL and make sure to enable the checkbox for your link.

### 🔗 Step 4: Open the App Using a Deep Link

1. Open a Notes, Email or messaging app on your device.

2. Tap a deep link (e.g., https://yourdomain.com/your-path).

3. If configured properly, your app will appear as an option to handle the link, or it will directly open the app.

### 🧭 Step 5: Handle Incoming URLs with onNewIntent() Method

1. Open your main activity file (e.g., MainActivity.kt).

2. Override the onNewIntent() method to receive new intents when the activity is already running:

```kotlin
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    val result = ShortioSdk.handleIntent(intent)
    Log.d("New Intent", "Host: ${result?.host}, Path: ${result?.path}")
}
```
3. In the same activity, you can also handle the initial intent inside the `onCreate()` method:

```kotlin
// Optional
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
        val result = ShortioSdk.handleIntent(intent)
    Log.d("New Intent", "Host: ${result?.host}, Path: ${result?.path}")
}
```

### 🤝 Contributing

If you'd like to contribute to the SDK or sample app, please fork the repository and submit a pull request.
