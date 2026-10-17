# AerioTV Blue — build and validation

## Deliverables

- Installable APK: `AerioTV-Blue-0.5.11-blue.1.apk` (only the APK is needed to install).
- Modified source: `AerioTV-Blue-source.zip`; upstream-relative changes: `blue-edition.patch`.
- Name: **AerioTV Blue**; package: `com.aeriotv.blue`.
- Version: `0.5.11-blue.1`; version code: `6701`.
- Minimum Android: 8.0 (API 26); target SDK: 36.
- Native CPU types: arm64-v8a, armeabi-v7a, x86_64.
- APK bytes: 12192328.
- APK SHA-256: `0434c80bfa5bc0cd055a03c4dce2ab2fb1a0b5ba0b42b65142f35eac7223210f`.

The separate package, provider authorities and deep-link scheme allow this
version to coexist with the original. Its data is separate: add/import your
playlists into AerioTV Blue. The upstream self-updater is omitted.

## Confirmed checks

The final source built successfully using JDK 21, Gradle 9.5.0, Android Gradle
Plugin 9.3.3, SDK platform 36 and build tools 36.0.0. The final Gradle invocation
completed successfully, including `assemblePlayRelease`, the selected unit
tests, and `lintVitalPlayRelease`.

| Suite | Tests | Failures | Errors | Skipped |
| --- | ---: | ---: | ---: | ---: |
| GuideGridStateTest | 13 | 0 | 0 | 0 |
| ThemeContrastTest | 6 | 0 | 0 | 0 |

The six theme tests cover custom accents, all eight presets, light/dark and
True Black combinations, TV/phone secondary text settings, blue gradient
stops, category/focus fills, badge ink and alpha compositing. The thirteen
existing guide-state tests also passed. These are JVM tests, not rendered UI
or end-to-end playback tests. Other repository suites were not run.

The release APK passed archive integrity, manifest/identity/ABI checks,
16 KB ZIP-alignment verification and Android APK signature verification.
APK Signature Scheme v2 and v3 both verify. The personal certificate SHA-256 is
`c66eabc3339a28adcfca2e3003ecdb679812def71f59e6068a20d0c9d1ca389e`.
No upstream signing key was used. Private signing material is excluded from
the source archive.

All 402 recorded build-input hashes match the corresponding source files in
the archive. The original uploaded APK remains unchanged. Upstream source
revision: `7cb2ccb0ca7456db1f55c2b25290ba127df939c6` (`v0.5.11`).

## Installation and runtime limits

The signed APK was transferred to an Android 15/API 35 x86_64 emulator.
The initial ADB install command timed out after 60 seconds, but a subsequent
`pm path com.aeriotv.blue` returned its installed `base.apk` path, confirming
that installation completed in the background.

Launch validation did not complete. `am start` returned
`cmd: Can't find service: activity`; the emulator's crash buffer reported
`DeadSystemException` in `com.android.systemui` and
`com.android.permissioncontroller`. The Android framework was restarting
before this app could be launched. This cloud machine has no KVM acceleration
and the software emulator was slow and unstable. There is no successful
rendered-screen, remote-control, provider-stream or playback check to report.
Those checks still need a physical phone/TV or a working accelerated emulator.
The observed framework failures do not establish an application defect.

## Optional integrations

The actual release bindings `GOOGLE_DRIVE_WEB_CLIENT_ID` and
`CAST_RECEIVER_APP_ID` were checked and are empty. Google Drive sign-in/sync
and Google Cast require personal app registrations configured for this package
and signing certificate. No credentials from the uploaded APK were copied.
See `BLUE_EDITION.md` in the source for build and licensing details.
