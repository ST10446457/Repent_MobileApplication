# REPENT† — Android Application

Native **Kotlin / Jetpack Compose** client for the REPENT† clothing and faith-content project. This Firebase-backed source is based on the `chore/repent-ci-and-source-refresh-20261028` branch, with PayFast sandbox work added on this feature branch.

## Current technology
- Android SDK 34, minimum SDK 24; Kotlin, Compose, Gradle
- Firebase Authentication with verified email/password accounts; Google sign-in depends on Firebase configuration
- Cloud Firestore in shared project `repent-48fda`
- Firestore products, devotionals, profiles, order requests, prayer requests and newsletter
- GitHub Actions build workflow and JUnit registration/delivery validation tests
- **PayFast sandbox card checkout integration (not deployed or live-tested)**

## Run in Android Studio
1. Download this branch's ZIP or clone the repository.
2. Open the repository root in Android Studio; use a compatible **JDK 21**, then sync Gradle.
3. Configure the Firebase Android app in project `repent-48fda` and use the appropriate `google-services.json` from your own Firebase project.
4. Check the HTTPS public payment function URL in `app/build.gradle.kts` as `PAYFAST_API_BASE_URL`.
5. Run on a physical device or emulator; use a verified Firebase email/password account to test.
6. Build using `./gradlew testDebugUnitTest assembleDebug` (Windows: `gradlew.bat testDebugUnitTest assembleDebug`).
7. Generated debug APK (after successful build): `app/build/outputs/apk/debug/app-debug.apk`.

## PayFast sandbox card checkout
The shared backend **lives in the website repository** at `payments/functions/index.js`. Its HTTPS endpoint should be deployed to Firebase Cloud Functions only after reviewing the ITN security checks and Firestore rules.

In checkout:
- **EFT / bank transfer**: an order request is created, **no electronic payment is processed**.
- **Card**: app obtains a Firebase ID token and calls `POST /create` on the shared backend.
- Backend derives product prices from Firestore, creates a pending order and gives the app a short-lived PayFast-hosted checkout URL.
- Android launches the checkout in the system browser (no card details collected by the app).
- Server-validated PayFast notification sets `paymentStatus=paid` (not the browser return).
- Orders screen displays the payment status for PayFast orders when available.

The sandbox uses simulated funds. Do not use real merchant keys or real bank cards. The backend is **not yet deployed**, so card payment will not function until setup is completed.

## Important files
- `app/src/main/java/com/repent/app/ui/screens/checkout/CheckoutScreen.kt` — checkout screen
- `app/src/main/java/com/repent/app/ui/screens/checkout/CheckoutViewModel.kt` — order and PayFast flows
- `app/src/main/java/com/repent/app/data/repository/PayfastRepository.kt` — authenticated API client
- `app/src/main/java/com/repent/app/data/repository/OrderRepository.kt` — Firebase order request and history
- `app/src/main/java/com/repent/app/ui/screens/orders/OrdersScreen.kt` — order history/payment status
- `app/src/main/java/com/repent/app/data/firebase/AccountValidation.kt` — email/registration rules
- `app/src/test/java/com/repent/app/AccountValidationTest.kt` — local JUnit validation tests

## CI, tests, and caveats
The Android GitHub Actions pipeline runs Gradle tests/builds where configured. A green CI run confirms only its configured checks. It does **not** prove that PayFast transactions or real devices work.

An administrator must receive a Firebase **custom claim** (not a writable Firestore `role` field). Android admin repository supports reading orders and updating order status, while admin screen functionality is not fully wired for end-to-end fulfilment.

Do not run the older PHP/MySQL README's `UPDATE users SET role='admin'` instruction: it does not grant access to the Firebase-backed app.

For initial migration details see [FIREBASE_MIGRATION.md](FIREBASE_MIGRATION.md) and [RUN_ON_PHONE.md](RUN_ON_PHONE.md). Check these older documents against this README before relying on their legacy PHP API instructions.

## Known limitations
- PayFast function deployment, actual ITN validation and live sandbox transaction verification remain incomplete.
- Firestore prices and website catalogue must agree; no inventory reservation or fulfilment automation has been implemented.
- Google sign-in requires correct SHA fingerprints and Firebase Android configuration.
- Never add merchant passphrases or server credentials to the APK or GitHub.

**Status:** Prototype checkout, not production-ready payment processing.
