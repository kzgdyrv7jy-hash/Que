# Que v2-only signing comparison

This APK tests whether the signing format affects VirusTotal's archive warning
during upload. The official AerioTV 0.5.13 release uses v2 signing only. This
comparison copy uses that same signature scheme with the personal Que/Blue key.

It is generated from the existing verified release-unsigned APK. No application
code, assets, native libraries, manifest, package ID or version is changed.
All 1,024 app entry payloads match the existing Que APK byte for byte. The three
legacy v1 signature entries are absent, and the APK signing block contains v2
instead of v2 and v3. There is one signer, using the existing certificate.

| Property | Comparison APK |
| --- | --- |
| File | `AerioTV-Que-0.5.13-que.1-v2-only.apk` |
| Size | 12,245,377 bytes |
| SHA-256 | `541a9068ce0338e2e40cd4f475ee226519955e2b6718df6c392df7b00fe1eada` |
| Package | `com.aeriotv.blue` |
| Version | `0.5.13-que.1` / `6901` |
| Signature | v2 verifies; v1, v3, v3.1 and v4 absent |
| Certificate SHA-256 | `c66eabc3339a28adcfca2e3003ecdb679812def71f59e6068a20d0c9d1ca389e` |
| ZIP integrity | Passed for every entry |
| Alignment | Android zipalign 4-byte / 16KiB native-library check passed |
| Android metadata | Parsed successfully; Android 8/API26 minimum, target API36 |
| Native architectures | ARM64, ARMv7, x86_64; FFmpeg decoder present for all three |

The existing [source archive](downloads/AerioTV-Que-0.5.13-source.zip) and
[build-input manifest](downloads/build-inputs-sha256.json) apply to the app payload.
All 455 recorded build-input hashes were checked again before signing. No new
compilation or unit-test run is required for this signature-only comparison;
the previous [application validation](VALIDATION.md) records the test results.

## Reproducing the signing format

Using Android build-tools 36.0.0 apksigner, the signing options are:

```text
--v1-signing-enabled false
--v2-signing-enabled true
--v3-signing-enabled false
--v4-signing-enabled false
--lib-page-alignment 16384
```

The existing personal keystore and password file remain outside the repository.

## VirusTotal comparison

Upload this APK as a file using the same browser and workflow used for the
original Que APK. Record whether the archive warning appears, the detected file
type, the reported SHA-256 and size, and the individual engine result statuses.

The previous Que report's SHA-256 matches the full 12,323,400-byte APK and its
displayed 11.75MiB size. That identifies the complete uploaded APK; it does not
prove how much content each engine analysed. The warning alone does not show
that scanning stopped after the first 3MB. A 0/69 detection summary also does
not establish identical engine coverage between two files.

This comparison APK has not yet been uploaded to VirusTotal by this build
workflow. Whether v2-only signing removes the warning remains to be tested.

Public verification output:

- [Signature verification](downloads/v2-only-signature-verification.txt)
- [Android metadata](downloads/v2-only-apk-metadata.txt)
- [Payload comparison](downloads/v2-only-comparison.json)
- [Download checksums](downloads/SHA256SUMS.txt)
