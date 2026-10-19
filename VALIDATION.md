# AerioTV Que 0.5.13 — validation

The app is rebuilt from the source for the exact supplied v0.5.13 release,
commit 8b2d935a307d99203a0fd91e44a83c9f2ae430fd. The downloaded original APK's
SHA-256 matches GitHub's published digest:
1ed8fe2ec0f943f2fcdc4fed9d02e3cfcde562cf4d675d6d2a2cf79bf50e80ca.

## Changes

Que Blue now uses the reference's fixed 104dp TV sidebar, separate Live TV and
Guide entries, blue gradient/circular decorations, category capsules, text-only
programme hero, large rounded guide cells, channel-list/video-preview split,
five-column poster grids, three-column recording grids, Appearance swatches,
and a centred blue Dispatcharr sign-in panel.
Actual titles, artwork, channels and categories come from the configured source.

Contrast fixes cover white custom accents, filled controls, text/outlined
buttons, focused filters, action menus, guide fills, channel groups, alphabet
navigation, search placeholders and dim text. Normal text is checked at 4.5:1;
focus boundaries at 3:1. Added pale-blue presets are selectable actual themes.

## Automated tests

37 JVM test suites: 280 discovered, 276 passed, 4 skipped, zero failures/errors.
All 7 ThemeContrastTest cases and all 13 GuideGridStateTest cases passed.

The upstream tests originally assumed `/opt/homebrew/bin/ffmpeg` and ffprobe.
Test-only discovery now uses PATH (or FFMPEG_BIN / FFPROBE_BIN overrides), with
Homebrew as a fallback. Host FFmpeg and ffprobe 7.1.5 were present and ran
successfully. Sixteen previously skipped media checks now pass, including
remux/probe, frame preservation and timeline/segment-continuity checks.

Four tests still skip through their original JUnit assumptions:

| Suite | Skipped | Reason |
| --- | ---: | --- |
| CastAudioFrameCensusTest | 1 | Author's private ESPNU stream capture absent |
| CastDemuxedRenditionTest | 1 | Author's private ESPNU stream capture absent |
| CastDemuxedRenditionTest | 2 | Upstream Chrome test setup/path unavailable |

No tests were manually excluded or assertions weakened. The Android APK includes
the FFmpeg JNI audio-decoder library for all three supported CPU architectures;
ffmpeg/ffprobe command-line executables are host testing tools, not bundled APK
executables.

## Release package checks

Final release build and release lint passed using JDK21, Gradle9.5.0 and SDK36.
The final unsigned APK was aligned and signed with the existing personal key.

- APK: `AerioTV-Que-0.5.13-que.1.apk`
- Bytes: 12323400
- SHA-256: `790049da67e264700ca518571b490a8b614f53183da4ac73f39f8299faa1e76e`
- APK Signature Schemes v2 and v3 verify.
- 16KiB ZIP alignment and archive integrity verify.
- Package/version/SDK/ABI metadata and absent upstream-updater permission verify.
- FFmpeg JNI decoder library is present for ARM64, ARMv7 and x86_64.
- Certificate SHA-256 matches the previous Blue APK:
  `c66eabc3339a28adcfca2e3003ecdb679812def71f59e6068a20d0c9d1ca389e`.
- All 455 recorded final build-input hashes match the delivered source.

See [APK metadata](downloads/apk-metadata.txt),
[signature verification](downloads/signature-verification.txt),
[build-input hashes](downloads/build-inputs-sha256.json) and
[source hashes](downloads/source-sha256.json). The
[complete source archive](downloads/AerioTV-Que-0.5.13-source.zip) and
[upstream-relative patch](downloads/que-v0.5.13.patch) correspond to this build.

## Runtime and visual limits

A copied Android 15/API35 software emulator reached ADB and framework services,
but its framework restarted before the new app could be installed/launched.
SystemUI/PermissionController reported DeadSystemException and Bluetooth aborted.
This environment has no KVM acceleration and only two CPU cores; the emulator
was stopped to release memory for the build after a compiler daemon was killed
at the environment's 8GiB limit. Compilation then recovered without the emulator.

No rendered screenshot of this new APK, physical-TV D-pad session, live-provider
stream, recording, or playback was verified. The reference layout is implemented
in code; exact pixel parity is not claimed without rendered-device comparison.
Existing playback preferences remain respected.

## Identity and optional integrations

Label AerioTV Blue; package com.aeriotv.blue; version 0.5.13-que.1 (6901).
Minimum Android8/API26, targetAPI36; ARM64, ARMv7 and x86_64.
The personal signing identity is retained so the previous Blue APK can be updated.
The original AerioTV package remains separate, with separate app data.
The upstream updater is omitted; Google Drive and Cast IDs are empty and require
personal registrations. No upstream private signing key or credentials were used.

GPL source, exceptions, third-party notices and FFmpeg build instructions are
included. Source and output checksums accompany the artifacts; private signing
material, credentials and playlists are excluded.
