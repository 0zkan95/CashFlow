# Implementation Plan: Midnight Ledger Configuration

This plan covers the initial configuration of the Cash Book Neo app based on the "Midnight Ledger" design system specified in [DESIGN.md](file:///C:/Users/aytek/Downloads/DESIGN.md).

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/aytek/AndroidStudioProjects/CashBookNeo/gradle/libs.versions.toml)
- Add Google Fonts dependency for Compose.

#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/aytek/AndroidStudioProjects/CashBookNeo/app/build.gradle.kts)
- Add the Google Fonts dependency to the dependencies block.

### UI Theme

#### [MODIFY] [Color.kt](file:///C:/Users/aytek/AndroidStudioProjects/CashBookNeo/app/src/main/java/com/example/cashbookneo/ui/theme/Color.kt)
- Define the comprehensive "Midnight Ledger" color palette including surface, primary, secondary, tertiary, and functional accents (Cash In/Out).

#### [MODIFY] [Type.kt](file:///C:/Users/aytek/AndroidStudioProjects/CashBookNeo/app/src/main/java/com/example/cashbookneo/ui/theme/Type.kt)
- Configure typography using "Plus Jakarta Sans" for headlines and "Manrope" for body text via Google Fonts.
- Define custom text styles like `currencyDisplay` and `currencyLedger`.

#### [MODIFY] [Theme.kt](file:///C:/Users/aytek/AndroidStudioProjects/CashBookNeo/app/src/main/java/com/example/cashbookneo/ui/theme/Theme.kt)
- Update `DarkColorScheme` and `LightColorScheme` (though the design is OLED-dark focused) with the new colors.
- Set up the `MaterialTheme` to use the updated typography and colors.
- Disable dynamic color by default to maintain brand consistency as per the design document.

## Verification Plan

### Automated Tests
- Build the project to ensure dependencies are resolved correctly.
- Run `gradlew assembleDebug` to verify compilation.

### Manual Verification
- Render a Compose Preview of the `MainActivity` (or a theme preview) to verify that colors and fonts are applied correctly.
