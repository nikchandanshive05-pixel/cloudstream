# CloudStream Nuvio Mobile UI Redesign Plan

## 1. Module Structure Summary

- **`:app`** (Android Application): Hosts the Android lifecycle, navigation graph (`mobile_navigation.xml`), `MainActivity`, fragments, legacy XML layouts (`res/layout/`), viewmodels, and Android-specific media/download integration.
- **`:shared`** (Kotlin Multiplatform): Houses Compose Multiplatform code (`commonMain` and `androidMain`), design system (`com.lagradost.cloudstream4.theme`), Mihon preference bridge, and cross-platform UI components.
- **`:library`** (Kotlin Multiplatform): Scraper framework, provider interfaces, extractors, models. (*Protected - strict no-modification rule*).
- **`:desktopApp`** (Desktop Runner): Desktop packaging and entrypoint.

---

## 2. Screen Inventory

| Screen Name | Current Implementation | ViewModel Class | Target Plan |
|---|---|---|---|
| **Home** | XML (`FragmentHomeBinding`, `HomeFragment.kt`) | `com.lagradost.cloudstream3.ui.home.HomeViewModel` | Embed Nuvio-styled Compose UI in `HomeFragment` via `ComposeView` observing `HomeViewModel` LiveData (`page`, `apiName`, `popup`). Features Nuvio hero carousel, horizontal content shelves, and provider selector chip bar. |
| **Search** | XML (`FragmentSearchBinding`, `SearchFragment.kt`) | `com.lagradost.cloudstream3.ui.search.SearchViewModel` | Embed Nuvio search Compose UI via `ComposeView` observing `SearchViewModel` (`searchResponse`, `currentSearch`, `currentHistory`, `searchSuggestions`). Features sleek Nuvio search bar, filter chips, and responsive poster grid. |
| **Library** | XML (`FragmentLibraryBinding`, `LibraryFragment.kt`) | `com.lagradost.cloudstream3.ui.library.LibraryViewModel` | Embed Nuvio library Compose UI via `ComposeView` observing `LibraryViewModel` (`pages`, `currentPage`, `currentSortingMethod`). Features category tabs, sort pill, and watch-progress overlay poster cards. |
| **Result / Details** | XML (`FragmentResultPhoneBinding`, `ResultFragmentPhone.kt` via `ResultTrailerPlayer.kt`) | `com.lagradost.cloudstream3.ui.result.ResultViewModel2` | Embed Nuvio cinematic details screen via `ComposeView` in `ResultFragmentPhone` observing `ResultViewModel2` (`page`, `episodes`, `resumeWatching`, `watchStatus`). Features backdrop hero with gradient fade, action buttons, season/dub selectors, and episode cards. |
| **Settings** | Compose (`SettingsFragment2.kt`, `SettingsFragmentScreen.kt`) | AppSettings & Mihon preference store | Restyle existing Compose settings with `NuvioTheme`, Nuvio elevated card surfaces (`#1A1A1A`), gold/cyan accent indicators, and refined typography. |

---

## 3. Nuvio Design Tokens

Extracted directly from NuvioMobile (`composeApp/src/commonMain/kotlin/com/nuvio/app/core/ui`):

### 3.1 Colors
- **Background**: `#0D0D0D` (Pure deep black/grey)
- **Background Elevated / Surface**: `#1A1A1A`
- **Background Card**: `#242424` / `#1F1F1F`
- **Primary / Accent (Default Gold)**:
  - Secondary / Accent: `#E8A91C` (Gold)
  - Secondary Variant: `#9A6200`
  - Accent Gradient: `[#8A5700, #E8A91C, #FFF1A8, #FFD45C, #9A6200]`
  - Focus Ring / Highlight: `#FFD45C`
  - Focus Background: `#3D2D1A`
- **Arctic Blue Accent (Alternative)**:
  - Secondary: `#3185F5`
  - Accent Gradient: `[#4DE3FF, #3185F5, #4D55E8]`
- **Text & Content**:
  - Text Primary: `#FFFFFF`
  - Text Secondary: `#AAB2BE` (Graphite muted)
  - Text Tertiary: `#687381`
  - Rating Badge Background: `#FFD45C` / `#E8A91C` with `#111111` bold text
  - Overlay Gradients: Vertical linear gradient `[Transparent, #0D0D0D]`

### 3.2 Typography & Spacing
- **Font Scale**: Clean system sans-serif / Inter
  - Hero Header: 24sp - 28sp Bold
  - Shelf Section Header: 18sp - 20sp SemiBold
  - Card Title: 13sp - 14sp Medium
  - Badges & Microtext: 11sp - 12sp SemiBold
- **Corner Radii**:
  - Poster Cards: `12.dp`
  - Chips & Pills: `20.dp` / CircleShape
  - Buttons: `12.dp`
  - Modals & Sheets: `24.dp` top corners

---

## 4. Phase-by-Phase Plan

1. **Phase 1 — CI First**:
   - Create `.github/workflows/build-debug.yml` targeting `nuvio-ui` branch.
   - Add `applicationIdSuffix ".nuvio"` to `debug` buildType in `app/build.gradle.kts`.
   - Verify CI run reaches green before any UI code changes.

2. **Phase 2 — Design System**:
   - Implement `NuvioTheme.kt`, `NuvioPosterCard.kt`, `NuvioHeroBanner.kt`, `NuvioContentRow.kt`, `NuvioChip.kt`, `NuvioBottomBar.kt`, and `NuvioSectionHeader.kt` in `shared/src/commonMain/kotlin/com/lagradost/cloudstream4/compose/`.
   - Verify design tokens match NuvioMobile.

3. **Phase 3 — Screens Implementation**:
   - **Home**: Create `NuvioHomeScreen.kt`, bridge into `HomeFragment.kt`.
   - **Search**: Create `NuvioSearchScreen.kt`, bridge into `SearchFragment.kt`.
   - **Library**: Create `NuvioLibraryScreen.kt`, bridge into `LibraryFragment.kt`.
   - **Result / Detail**: Create `NuvioDetailScreen.kt`, bridge into `ResultFragmentPhone.kt`.
   - **Settings**: Restyle `SettingsFragmentScreen.kt` with `NuvioTheme`.

4. **Phase 4 — Wrap-up & QA**:
   - Validate GitHub Actions release artifact generation.
   - Complete manual verification checklist.
   - Update README with GPL-3.0 attribution for NuvioMedia.

---

## 5. CI Strategy
- Workflow trigger: Push to `nuvio-ui` branch.
- Gradle Task: `./gradlew :app:assemblePrereleaseDebug` (or `:app:assembleDebug`).
- Artifact Output: `app/build/outputs/apk/prerelease/debug/*.apk`.
- Release Automation: Publish/update GitHub Release `nuvio-ui-latest` with the built APK.

---

## 6. Blocked Section
### Current CI fixes (2026-10-22)
- Removed the conflicting `androidx.compose.ui.draw.background` import from `NuvioHomeScreen.kt`; retained `androidx.compose.foundation.background`.
- Fixed the in-progress Home Compose integration: removed invalid/duplicate imports, imported `Modifier.fillMaxSize`, safely accessed the successful page resource for expand actions, removed unsupported manual `ComposeView.recompose()` calls, and made the theme-name lookup non-null.
- The latest available CI log reported these Home integration compilation errors. Local Gradle compilation could not start because Java/JAVA_HOME is not configured. Push these fixes and let GitHub Actions compile them.
