# AerioTV Que edition

Based on AerioTV Android v0.5.13, upstream commit
`8b2d935a307d99203a0fd91e44a83c9f2ae430fd`.
The downloaded release APK was verified against GitHub's published SHA-256:
`1ed8fe2ec0f943f2fcdc4fed9d02e3cfcde562cf4d675d6d2a2cf79bf50e80ca`.

## Que design

Que Blue is the default theme. The Android TV rendition follows the supplied
reference: a fixed cobalt navigation rail; separate Live TV and Guide entries;
a diagonal blue glow and translucent circles; large category pills; a text-only
programme hero; rounded channel/programme cards; five-column poster grids;
three-column recording grids; a centred Dispatcharr sign-in panel; and an
Appearance palette gallery.

Live TV uses a channel list and a measured 16:9 preview slot for the existing
Media3 player. Guide/list choice is session-only. Existing playback preferences,
server data, search, recording, radio, menus and programme-info actions remain.
Other existing themes retain the upstream shell. Explicit Light and True Black
preferences remain available. Four additional pale-blue presets provide both
light and dark renditions.

Contrast fixes choose foregrounds against actual fills, including white custom
accents, focused filters, guide/category fills, action menus and alphabet rails.
Material text and outlined actions use readable accent ink. Host media tests
locate FFmpeg/ffprobe through PATH instead of assuming a macOS Homebrew path.
Theme tests enforce
4.5:1 normal text and 3:1 focus contrast, including the blue gradient/decorations.

## Identity and installation

Package `com.aeriotv.blue`, label AerioTV Blue, version `0.5.13-que.1`, version
code `6901`. This can update the previous personal Blue build with the same key
and coexist with the original AerioTV app. Original-app data is separate.

The Play distribution variant is built for sideloading because it excludes the
upstream self-updater. Updates signed with the upstream key cannot replace this
personal package. Google Drive and Google Cast need personal app registrations;
this source defaults their application IDs to empty.

## Build

JDK 21, Android SDK 36/build-tools 36.0.0, and Gradle 9.5.0:

```sh
./gradlew :app:testPlayDebugUnitTest :app:assemblePlayRelease
```

Release output is unsigned unless your own gitignored `keystore.properties` is
configured. Align and sign using Android SDK zipalign/apksigner and retain your
key for future updates. Never commit signing keys or passwords.

## Licence

Original GPL-3.0-or-later, exceptions, third-party notices and bundled FFmpeg
source/build instructions are retained. See LICENSE, LICENSE-EXCEPTIONS.md,
THIRD_PARTY_LICENSES.md and app/libs/README.md. Modifications: October 2026.
See VALIDATION.md for checks actually performed and their limits.
