# REPENT† — Android App

> **Firebase migration (October 2026):** This version uses Firebase
> Authentication and Cloud Firestore for the main customer workflows.
> Historical PHP API instructions below apply to the original app. Start
> with [RUN_ON_PHONE.md](RUN_ON_PHONE.md) and
> [FIREBASE_MIGRATION.md](FIREBASE_MIGRATION.md). Orders are unverified demo
> requests, not real payments. Build and phone testing are pending.

A native Android (Kotlin + Jetpack Compose) client for the REPENT† faith-based
clothing site, talking to a PHP REST API added to your existing project.

This project is delivered as **two pieces**:

1. `repent-api.zip` — drop this into your existing `Repent` PHP project (adds
   an `api/` folder + one migration SQL file). This is the backend the app talks to.
2. `repent-android.zip` (this project) — open in Android Studio and run.

---

## 1. Set up the API

1. Unzip `repent-api.zip` **into the root of your existing `Repent` project**,
   so you end up with `Repent/api/...` next to your existing `pages/`,
   `includes/`, etc. (It reuses your existing `includes/db.php`.)
2. Run `repent_api_migration.sql` against your `repent_db` database
   (phpMyAdmin, or `mysql -u root -p repent_db < repent_api_migration.sql`).
   This is additive/idempotent — safe to run even if some of it already exists.
3. Start your PHP server as usual, e.g. from the project root:
   ```
   php -S localhost:8000
   ```
4. Quick test in a browser or `curl`:
   ```
   curl http://localhost:8000/api/products.php
   ```
   You should get back JSON.

**Note:** two small pre-existing bugs were fixed along the way rather than
carried into the API: `pages/checkout.php` was writing to `orders` columns
(`total_amount`, `shipping_address`) that didn't exist in your schema — the
migration adds the real columns and the API uses the correct schema; and
`pages/admin.php` referenced an undefined `$user` variable.

---

## 2. Run the Android app

1. Open this folder (`repent-android/`) in **Android Studio** (Koala or newer).
   Let Gradle sync — it will download the wrapper jar automatically on first sync.
2. **Point the app at your API** — edit `app/build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/\"")
   ```
   - `10.0.2.2` is the special address the **Android Emulator** uses to reach
     `localhost` on your development machine. If you're running on a **real
     phone**, replace it with your computer's LAN IP (e.g. `http://192.168.1.20:8000/`)
     — the phone and computer need to be on the same Wi-Fi network.
   - Once you deploy the API to a real server (e.g. via your host's PHP
     hosting), just point `BASE_URL` at that instead, e.g.
     `https://yourdomain.com/`.
3. Run the app on an emulator or device (Run ▶ in Android Studio).

### Product images
The app loads product photos directly from your PHP server
(`BASE_URL/assets/images/<filename>`), the same images your website already
uses — nothing to duplicate or re-upload.

### Brand fonts (optional)
The app currently uses the system serif/sans-serif fonts as stand-ins for your
site's **Cormorant Garamond** (headings) and **DM Sans** (body) fonts, so it
builds and runs with zero extra setup. To use the real fonts:
1. Download the `.ttf` files for both families from Google Fonts.
2. Put them in `app/src/main/res/font/`.
3. In `ui/theme/Type.kt`, build real `FontFamily`s from them and swap out
   `DisplayFontFamily` / `BodyFontFamily`.

### Demo / test accounts
Use the **Register** screen in the app to create an account — it hits
`api/register.php` and logs you straight in. To test the **Admin Dashboard**,
either register normally and then run this once against your database:
```sql
UPDATE users SET role = 'admin' WHERE email = 'your@email.com';
```
The Admin tab then appears on the Profile screen for that account.

---

## What's implemented

**Screens:** Splash, Home, Shop (category filter), Product Detail, Cart,
Checkout, Order Confirmation, My Orders, Devotionals, Prayer Requests,
Newsletter, Profile/Login/Register, Admin Dashboard (stats, orders, prayer
requests, subscribers).

**Architecture:** MVVM, Retrofit + OkHttp for networking, Gson for JSON,
Jetpack DataStore for the auth token and the on-device cart (cart lives on
the phone and is only sent to the server at checkout — same idea as the
website's session-based cart, just without a server session), Coil for image
loading, Navigation Compose for screen routing.

**Not included / left for you:** push notifications, offline caching beyond
the cart, payment gateway integration (checkout currently just records the
order — `payment_method` is stored but no actual card/EFT processing happens),
and a proper dependency-injection framework (this uses a small hand-rolled
`RepentApp.kt` service locator instead of Hilt, which is plenty for a project
this size but worth knowing about).
