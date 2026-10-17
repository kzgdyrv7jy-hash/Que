# AerioTV Blue

Personal version of AerioTV 0.5.11, based on upstream commit
`7cb2ccb0ca7456db1f55c2b25290ba127df939c6` (tag `v0.5.11`).
Upstream: https://github.com/jonzey231/AerioTV-Android

## Changes

- Royal Blue is the default theme: a blue gradient inspired by the supplied
  reference, navy cards, and pale blue accents. It has a light rendition and
  supports the existing Dark / Light / System and True Black preferences.
- Buttons choose black or white foregrounds from the actual accent fill.
  White custom accents no longer produce white button labels in light mode.
- Secondary and small text receive a minimum contrast adjustment rather than
  disappearing into their surfaces.
- Guide text and focus outlines account for each rendered category/focus fill.
  Text caches invalidate when the palette changes. Badges use readable ink.
- Focused guide controls, search results, and shared TV controls follow the
  resolved light/dark palette.
- Separate package `com.aeriotv.blue`, launcher label `AerioTV Blue`, and
  `aeriotvblue://` deep links allow installation alongside the original.

## Build

Use JDK 21, Android SDK platform 36 and build tools 36.0.0. Gradle 9.5.0 is
pinned by the wrapper. The bundled FFmpeg AAR is unchanged.

```sh
./gradlew :app:testPlayDebugUnitTest \
  --tests com.aeriotv.android.ui.theme.ThemeContrastTest \
  --tests com.aeriotv.android.feature.livetv.grid.GuideGridStateTest \
  :app:assemblePlayRelease
```

The `play` variant is used because it omits the upstream self-updater. Personal
builds must not try to install updates signed by the upstream app's key. This
APK is for direct installation; it has not been uploaded to Google Play.

Release APKs are unsigned unless your own `keystore.properties` is configured
as described by the upstream build. Alternatively, align the unsigned APK and
sign it using Android SDK `zipalign` and `apksigner`. Keep your private signing
key outside source control and reuse it for future updates to this app.

The supplied personal APK uses its own signing identity. Its app data is
separate from the original; add or import your playlists in the new app.

## Optional Google integrations

The source defaults to empty Google Drive OAuth and Cast application IDs.
Drive sign-in and Google Cast therefore require your own app registrations,
configured for the personal package and signing certificate. No credentials
or registration IDs from the original APK are reused. Playlist, guide and
local playback code remains available without those integrations.

## Licence and source

The original licence, exception, third-party notices, and FFmpeg build
instructions are retained. Modifications were made for the Blue edition in
October 2026 and are supplied under the project's GPL-3.0-or-later licence.
See `LICENSE`, `LICENSE-EXCEPTIONS.md`, and `THIRD_PARTY_LICENSES.md`.

This source does not include private signing keys, credentials, build caches,
or the user's playlists. See the accompanying validation report for the
checks actually executed and their limits.
