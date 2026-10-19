# AerioTV Que edition

AerioTV Android **0.5.13** with the screenshot-inspired Que Blue TV interface
and contrast fixes. The Android TV screens use a cobalt sidebar, separate Live
TV and Guide entries, blue programme cards, media grids and theme previews.

Version: `0.5.13-que.1` (`6901`); package: `com.aeriotv.blue`.
Android 8.0+; ARM64, ARMv7 and x86_64.

[Download the signed Que APK](https://github.com/kzgdyrv7jy-hash/Que/raw/refs/heads/main/downloads/AerioTV-Que-0.5.13-que.1.apk).
[Checksums](downloads/SHA256SUMS.txt) and the
[complete build source archive](downloads/AerioTV-Que-0.5.13-source.zip) accompany it.

For the VirusTotal upload-warning comparison, download the
[v2 copy with no gap before the signing block](https://github.com/kzgdyrv7jy-hash/Que/raw/refs/heads/main/downloads/AerioTV-Que-0.5.13-que.1-v2-no-gap.apk).
It retains Gradle's original entry headers, padding and compressed app files,
with the same signing certificate and v2 signing only. See the
[Daylight comparison and verification](APK_PACKAGING_COMPARISON.md), the
[earlier ZIP layout checks](ZIP_LAYOUT_VALIDATION.md) and the
[signature-only comparison result](V2_ONLY_VALIDATION.md).

The signed APK can update the earlier personal AerioTV Blue build and installs
alongside the original AerioTV. The original app's data is separate. The upstream
self-updater is omitted; Google Drive/Cast require personal registrations.

Source base: [AerioTV v0.5.13](https://github.com/jonzey231/AerioTV-Android/tree/v0.5.13),
commit `8b2d935a307d99203a0fd91e44a83c9f2ae430fd`.

See [build notes](BLUE_EDITION.md) and [validation](VALIDATION.md).
The original [GPL licence](LICENSE), [exceptions](LICENSE-EXCEPTIONS.md),
[third-party notices](THIRD_PARTY_LICENSES.md), and [upstream README](UPSTREAM_README.md)
are retained. Private signing material, credentials and playlists are excluded.
