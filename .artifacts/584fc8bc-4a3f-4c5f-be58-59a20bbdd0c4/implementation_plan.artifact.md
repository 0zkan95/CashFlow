# Logic Implementation Plan: Global Navigation & Routing

Refactor the app's navigation and state logic to follow the "Primary Route Hierarchy" and "Global Navigation Graph" as specified in the design documents.

## Proposed Changes

### 1. Dependency Management
- **Modify** `gradle/libs.versions.toml`: Add Jetpack Navigation Compose dependency.
- **Modify** `app/build.gradle.kts`: Include the new navigation dependency.

### 2. Navigation Architecture
- **NEW** `navigation/Routes.kt`: Define all routes as a sealed class (e.g., `OnboardingStep1`, `Home`, `Analytics`, `AppLock`, etc.).
- **Modify** `MainActivity.kt`:
    - Replace the manual `currentScreen` state with a `NavController`.
    - Implement a `NavHost` with a `startDestination` logic based on `hasCompletedOnboarding`.
    - Set up nested navigation graphs for "Onboarding" and "MainApp".

### 3. Screen Refactoring & Logic Wiring
- **Modify** `OnboardingScreen.kt`: Update to use `NavController` for transitioning between steps 1, 2, and 3.
- **Modify** `DashboardScreen.kt`, `AnalyticsScreen.kt`, etc.: Update their `onNavigate` callbacks to work with the new route structure.
- **Implement** the "Security Gate" interceptor logic: Ensure the app checks for lock status on cold starts and navigation.

### 4. Component Interaction
- **Modify** `AppDrawerContent`: Update drawer items to trigger `navController.navigate`.
- **Modify** `DashboardBottomNavigation`: Sync the selected item with the current backstack entry.

## Verification Plan

### Automated Tests
- N/A (Manual UI verification)

### Manual Verification
- **Cold Start**: Verify the app starts on `OnboardingStep1` if not completed, or `AppLock` if security is enabled.
- **Onboarding Flow**: Verify smooth transitions between steps and correct "Initialize" logic on completion.
- **Main Navigation**: Verify all bottom bar and drawer items navigate to the correct routes.
- **Back Navigation**: Verify that the back button behaves correctly in sub-pages like `TransactionDetail`.
