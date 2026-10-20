---
sessionId: session-260805-232725-kvwt
---

# Answer & Requirements

## Where do `CLIENT_ID` and `REDIRECT_URI` come from?

Both values are configured in the **Apple Developer Portal** (developer.apple.com) under your team's account, and then coordinated with the backend team. They are currently placeholders in `AppleSignInHelper.kt`:

```kotlin
const val CLIENT_ID = "com.example.tenee.android.service"   // Apple Services ID
const val REDIRECT_URI = "https://api.staging.tenee.io/users/apple_callback" // backend callback
```

- **`CLIENT_ID`** = the identifier of an Apple **Services ID** (NOT the app's Bundle ID). This is what Apple calls the OAuth client for *web / Android* Sign in with Apple flows.
- **`REDIRECT_URI`** = the exact **Return URL** you register on that Services ID. It must point at the **backend endpoint** that receives Apple's `form_post` (here `/users/apple_callback`). The backend then deep-links back into the app via `tenee://auth/callback`.

> Key point: because this Android flow uses the *web* OAuth flow (Chrome Custom Tab → `appleid.apple.com/auth/authorize`), you must use a **Services ID**, not the app's Bundle ID, and the redirect URI must be an **HTTPS URL you own** (the backend), never the `tenee://` scheme. Apple rejects custom-scheme redirect URIs.

### Goal
Produce clear, project-specific documentation so any developer can obtain these two values and plug them into the app + backend.

### Scope
**In scope**
- Step-by-step guide to create/find the Services ID (`CLIENT_ID`).
- Step-by-step guide to determine and register the `REDIRECT_URI`.
- Links to official Apple documentation.
- Mapping the values back into `AppleSignInHelper.kt`.

**Out of scope**
- Backend implementation of `/users/apple_callback`.
- Changing the auth flow itself.

# How to get the values

## Prerequisites
- Membership in the **Apple Developer Program** (paid) for the Teene team.
- Access to https://developer.apple.com/account with **Admin** or **App Manager** role.
- Coordination with the backend owner for the callback URL.

## Part A — Get `CLIENT_ID` (Apple Services ID)
1. Sign in at https://developer.apple.com/account → **Certificates, Identifiers & Profiles**.
2. Go to **Identifiers** (left menu).
3. **App ID first (one-time):** ensure an **App ID** exists for the iOS app with **Sign In with Apple** capability enabled. This is the "primary App ID" the Services ID will group under.
4. Create the Services ID:
   - Click **+** → select **Services IDs** → Continue.
   - **Description:** e.g. `Teene Android Sign In`.
   - **Identifier:** a reverse-DNS string, e.g. `com.example.tenee.android.service` (this string is the `CLIENT_ID`).
   - Register it.
5. Open the newly created Services ID → tick **Sign In with Apple** → **Configure**:
   - **Primary App ID:** select the App ID from step 3.
   - **Domains and Subdomains:** add the backend domain, e.g. `api.staging.tenee.io`.
   - **Return URLs:** add the exact backend callback (see Part B).
   - Save.
6. Also create a **Sign in with Apple Key** (Keys section, **+** → enable Sign in with Apple) and generate the **Key ID** + download the `.p8` — the **backend** needs these (plus **Team ID** and the Services ID) to verify tokens. Share them securely with the backend team.

➡️ The **Identifier** string from step 4 is your `CLIENT_ID`.

## Part B — Get `REDIRECT_URI`
- The redirect URI is **owned by the backend**, not the app. Ask the backend owner for the exact callback URL per environment.
- Convention in this project: `https://<backend-host>/users/apple_callback`.
  - Staging: `https://api.staging.tenee.io/users/apple_callback`
  - Production: `https://<prod-host>/users/apple_callback`
- Requirements Apple enforces:
  - Must be **HTTPS**.
  - Must **exactly match** (scheme, host, path, no trailing differences) a **Return URL** registered on the Services ID in Part A step 5.
  - The domain must be listed under **Domains and Subdomains**.
- After the backend receives Apple's `form_post`, it redirects into the app via `tenee://auth/callback?...` (already registered in `AndroidManifest.xml`). This deep link is separate from `REDIRECT_URI` and is not configured in Apple.

## Part C — Plug the values in
- Update `CLIENT_ID` and `REDIRECT_URI` in `app/src/main/java/com/example/tenee/authentication/AppleSignInHelper.kt` and remove the `TODO`.
- Ensure the same Services ID + Return URL pair is configured on the backend so token verification succeeds.

## Official documentation
- Sign in with Apple overview: https://developer.apple.com/sign-in-with-apple/
- Configuring your environment (Services ID, keys, return URLs): https://developer.apple.com/help/account/configure-app-capabilities/configure-sign-in-with-apple-for-the-web/
- Request an authorization (web) — params like `response_type`, `response_mode`, `scope`: https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_js/configuring_your_webpage_for_sign_in_with_apple
- Generate & validate tokens (backend): https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens

# Delivery Steps

###   Step 1: Add Apple Sign-In credentials setup documentation
A developer-facing markdown doc explains how to obtain and configure the Apple `CLIENT_ID` and `REDIRECT_URI` for this project.

- Create a doc file (e.g. `docs/apple-sign-in-setup.md`) in the repo.
- Document Part A (create/find the Apple **Services ID** → `CLIENT_ID`), including the App ID prerequisite and the Sign in with Apple Key (`.p8`, Key ID, Team ID) that the backend needs.
- Document Part B (determine/register the backend `REDIRECT_URI` per environment, HTTPS + exact-match Return URL requirements).
- Explain the relationship between `REDIRECT_URI` (Apple → backend) and the `tenee://auth/callback` deep link (backend → app) as wired in `AndroidManifest.xml` and `MainActivity.kt`.
- Include the official Apple documentation links.

###   Step 2: Wire the obtained values into AppleSignInHelper
`AppleSignInHelper.kt` uses the real Services ID and backend callback instead of placeholders.

- Replace the placeholder `CLIENT_ID` and `REDIRECT_URI` constants in `app/src/main/java/com/example/tenee/authentication/AppleSignInHelper.kt` with the confirmed values.
- Remove the `// TODO: Replace with actual IDs from the Backend` comment once populated.
- Cross-check that the `REDIRECT_URI` matches a registered Return URL on the Services ID and the backend endpoint, and note per-environment (staging/prod) handling.