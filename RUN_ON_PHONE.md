# REPENT† Android — physical-phone setup

This code is a development build for Firebase project `repent-48fda`. It has
not yet been compiled on the author's machine or tested on a physical phone.

## 1. Prepare website images on XAMPP

1. Extract the companion `Repent_Website-main` folder to `C:\xampp\htdocs\`.
2. Start **Apache** using the XAMPP Control Panel.
3. From your laptop, open `http://localhost/Repent_Website-main/pages/shop.php`.
4. Find your laptop's IPv4 address with `ipconfig` (Windows), e.g. `192.168.1.20`.
5. In `app/build.gradle.kts`, set `BASE_URL` to
   `http://192.168.1.20/Repent_Website-main/` using **your** actual address.
6. Connect phone and laptop to the same Wi-Fi. Permit Apache through Windows
   Firewall on the private network. Check the same URL on your phone using the
   laptop IPv4 instead of `localhost`.

Firebase Auth and Firestore communicate directly with Google's services;
XAMPP is needed only for website hosting/product images during this demo.

## 2. Open in Android Studio

1. Extract the Android project.
2. Open the folder containing `settings.gradle.kts` in Android Studio.
3. Use Android Studio with Java 17, Android SDK 34 and Gradle 8.7.
4. This source archive was missing `gradlew`, `gradlew.bat` and
   `gradle/wrapper/gradle-wrapper.jar`. If Studio does not sync, generate the
   Gradle wrapper on your PC using installed Gradle 8.7:
   `gradle wrapper --gradle-version 8.7`.
5. Sync Gradle; check that `app/google-services.json` is present.
6. Build > Make Project; then run `testDebugUnitTest` and `assembleDebug`.

## 3. Google Sign-In certificate

Before using Google login, open the Gradle task `signingReport` in Android
Studio (or run `gradle signingReport`) to find SHA-1 and SHA-256 for the **debug**
certificate. Add both under Firebase Console > Project settings > Your apps >
REPENT Android. Download the **updated** `google-services.json` and replace the
file in `app/`. Then rebuild. Without this, the Google button displays a setup
warning instead of working. Email/password can be tested independently.

## 4. Firestore rules and sample data

Deploy `firestore.rules` in Firebase Console > Firestore > Rules after review.
Run the companion website project's `scripts/seed-firestore.mjs` using
authenticated developer credentials, or manually create the collection data.
Do not leave Firestore in public test mode. No app can display products until
the product collection has been populated.

## 5. Install and demonstrate

1. Enable Developer options and USB debugging on your Android phone.
2. Connect by USB and approve the computer's RSA debugging prompt.
3. Select the phone in Android Studio and Run. Alternatively, install the
   debug APK from `app/build/outputs/apk/debug/` after a successful build.
4. Test register > login > shop > cart > pending order > profile settings,
   incorrect input messages, Help, prayer and logout.
5. Record high-quality evidence of the physical device and Firebase Console.

## Known limitations before submission

- No real payments or stock reservation. Orders are **unverified demo requests**.
- Firebase rules must be deployed; checkout data must be reviewed before
  fulfilment. A production ecommerce system needs a trusted checkout server.
- The existing UI still shows product images from a local XAMPP computer, so
  image loading requires the same LAN unless images are deployed publicly.
- Google SSO remains unverified until SHA-1/256 and an updated config are used.
- Local Android build and unit tests have not been run in this environment.
- The assignment's relational DBMS wording conflicts with Firestore's document
  model; obtain lecturer acceptance or retain a relational alternative.
