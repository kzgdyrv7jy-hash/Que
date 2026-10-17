# AerioTV Blue

Personal version of AerioTV 0.5.11 with a royal-blue theme, improved text
contrast, and clearer TV focus indicators.

## Download and install

[**Download AerioTV Blue APK**](https://github.com/kzgdyrv7jy-hash/Que/raw/refs/heads/main/downloads/AerioTV-Blue-0.5.11-blue.1.apk)

The signed APK is also in [downloads](downloads/). Android 8.0 or newer is
required. ARM64, ARMv7 and x86_64 devices are supported. Android may ask you
to allow installation from your browser or file manager.

AerioTV Blue installs alongside the original as `com.aeriotv.blue`. Add or
import your playlists in the new app. The original app's data is separate.

## Changes

- Royal-blue gradient and navy panels, inspired by the supplied colour reference.
- Readable text and icons across light/dark modes and custom accent colours.
- Guide text and focus outlines adapt to category and selection backgrounds.
- Clearer focus borders, input cursors and dialog labels.
- Separate app identity and deep links; the upstream self-updater is disabled.

## Validation and limitations

The release build, 19 selected unit tests, release lint, APK signature checks,
and ZIP-alignment verification passed. Android installation was confirmed.
The cloud emulator's Android framework crashed before launch validation;
rendered UI, real-device remote navigation and playback remain unverified.

Google Drive sync and Google Cast require separate Google app registrations
for this package and signing certificate. They are not configured in this build.

See the [validation report](VALIDATION.md), [APK checksum](downloads/SHA256SUMS.txt),
and [build and modification notes](BLUE_EDITION.md).

## Source and licence

This repository contains the modified source corresponding to the APK.
It is based on [AerioTV Android](https://github.com/jonzey231/AerioTV-Android),
upstream commit `7cb2ccb0ca7456db1f55c2b25290ba127df939c6` (tag `v0.5.11`).
Private signing keys, credentials and build caches are not included.

The original GPL-3.0-or-later licence, exceptions and third-party notices are
retained. See [LICENSE](LICENSE), [LICENSE-EXCEPTIONS.md](LICENSE-EXCEPTIONS.md),
and [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md).
The [upstream README](UPSTREAM_README.md) is retained for reference; its release
and store links refer to the original project.
