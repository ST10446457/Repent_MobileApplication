# REPENT Android tablet image and shop fixes

Changes made to this copy of the project:

- The ten product image files referenced by the website's Firebase seed data are bundled in `app/src/main/res/drawable-nodpi/`.
- `productImageModel` in `ui/components/Common.kt` maps those product filenames to bundled Android drawables, with URL fallback for other images.
- Home, cart, and product details can display the bundled pictures without connecting to a development XAMPP server.
- Shop displays product cards with pictures and filters matching the existing Firestore categories: `tshirts`, `hoodies`, and `caps`.
- The devotional screen shows a useful empty state if Firestore has no devotional documents.

To build:

1. Unzip this folder and open `Repent_MobileApplication-main` in Android Studio.
2. Keep the existing Firebase project configuration in `app/google-services.json` and use JDK 21 for Gradle 8.7.
3. Sync Gradle, then Build > Build APK(s), or run `:app:assembleDebug`.
4. Find the resulting APK at `app/build/outputs/apk/debug/app-debug.apk`.

Notes:

- New product and devotional records still need to exist in the live Firebase project; changing Android code does not populate Cloud Firestore.
- This package preserves the uploaded Gradle and Firebase integration. Builds and tablet behavior must be verified on the developer's Android Studio installation; the build toolchain was not available in this environment.
- The original XAMPP `BASE_URL` remains as fallback for any extra images not bundled in this version.
