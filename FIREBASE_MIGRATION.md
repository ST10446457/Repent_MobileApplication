# REPENT Firebase Android migration — developer handoff

Project: `repent-48fda` / Android package: `com.repent.app`.

## Implemented in this working source

- Added Firebase Google Services plugin, Auth and Firestore SDK dependencies.
- Added the user's `app/google-services.json` configuration.
- Wired `AuthRepository` to Firebase Auth, and added `FirebaseAccountService` with email/password signup/login, reset email,
  recent-sign-in password change, display name update, and sign-out.
- Wired products, devotionals, prayers, newsletters, draft orders and administrator
  read dashboards to Cloud Firestore.
- Added native Account Settings, password management, Help and Google
  Credential Manager sign-in button (requires SHA configuration).
- Added six JUnit account/delivery validation tests and GitHub Actions CI.
- Added initial Firestore security rules with a strict admin custom-claim
  check and protected user and order data.

## Not yet validated / known limitations

- Native Google Sign-In still needs SHA-1/256 registration
  and a refreshed Google services configuration.
- Cloud Firestore rules have NOT been deployed. They should be reviewed and
  tested against the Firestore Rules Emulator before use.
- Checkout only saves unverified pending order requests; a trusted backend is
  still required for actual order fulfillment, payment and stock reservation.
- Admin custom claims cannot be granted from the client. The companion
  website script can be run by an authorized developer.
- Legacy Retrofit API definitions remain in the source for compatibility, but
  are no longer used by the main Firebase-backed screen repositories.
- No Android build has been run: the source archive has no Gradle wrapper
  executable/JAR and this environment has no Gradle/Android SDK installed.
- See `RUN_ON_PHONE.md` for exact desktop setup and physical-device tests.

## Assignment warning

The supplied rubric explicitly requests a relational DBMS and ten records in
every table. Firestore is a document store. Confirm with the lecturer whether
it is acceptable, or use a relational service such as Firebase Data Connect.
