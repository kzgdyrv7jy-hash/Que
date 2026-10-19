# Que APK with preserved ZIP entry layout

This comparison uses v2-only signing and preserves the release-unsigned APK's
ZIP entry layout. It addresses the packaging difference observed after the
[first v2-only comparison](V2_ONLY_VALIDATION.md) still produced VirusTotal's
archive warning.

The signing tool's default realignment is disabled with
`--alignment-preserved true`. All local entry headers, zero-filled padding,
entry order, offsets and compressed streams are preserved byte for byte from
the verified Gradle release output. This retains the style of ZIP entry padding
used by the official AerioTV APK rather than rewriting it into Android alignment
extra fields.

## Verified package

| Property | Value |
| --- | --- |
| APK | `AerioTV-Que-0.5.13-que.1-v2-preserved.apk` |
| Bytes | 12,224,897 |
| SHA-256 | `ad8be584ebd27329d9b4edfa3e6ae0d9d27480da390a2715f1fc34fed9dc74a9` |
| Package | `com.aeriotv.blue` |
| Version | `0.5.13-que.1` / `6901` |
| Signing | v2 verifies; v1, v3, v3.1 and v4 absent |
| Certificate SHA-256 | `c66eabc3339a28adcfca2e3003ecdb679812def71f59e6068a20d0c9d1ca389e` |
| App payload comparison | All 1,024 entries identical to the previous Que APK, excluding its three legacy signature entries |
| Raw ZIP entry comparison | All 12,154,448 bytes through the last local ZIP entry identical to the verified unsigned Gradle output |
| ZIP integrity | Passed for every entry |
| Alignment | 4-byte stored entries and 16KiB native libraries passed |
| Metadata | Parsed successfully; minimum API26, target API36; ARM64, ARMv7, x86_64 |

All 455 recorded [build-input hashes](downloads/build-inputs-sha256.json) still
match. The [existing build source](downloads/AerioTV-Que-0.5.13-source.zip)
applies to this APK. App code, assets, manifest and native libraries are
unchanged, including the FFmpeg decoder libraries. No application compilation
or JVM test rerun was needed for this packaging-only comparison.

## What signing still adds

The final APK includes a new v2 signing block, 2,480 bytes of page padding before
that block, and updated ZIP central-directory offsets. Android's signer creates
that padding for signing-block page alignment. The official APK uses different
signing-block allocation, so this is not a claim that the two complete archive
layouts are identical. The local ZIP entries and their padding are preserved.

The signature options used with Android build-tools 36.0.0 are:

```text
--v1-signing-enabled false
--v2-signing-enabled true
--v3-signing-enabled false
--v4-signing-enabled false
--alignment-preserved true
```

Private signing material remains outside the repository. The same personal
certificate is retained.

## VirusTotal test status

The user subsequently reported the same archive warning for this copy. Retaining
the local ZIP entry layout did not remove it. A comparison with a second APK that
uploads without the warning found a remaining pre-signing-block gap difference.
See the [Daylight comparison and no-gap test](APK_PACKAGING_COMPARISON.md).

Verification output:

- [Signature verification](downloads/v2-preserved-signature-verification.txt)
- [APK metadata](downloads/v2-preserved-apk-metadata.txt)
- [Layout and payload comparison](downloads/v2-preserved-comparison.json)
- [Checksums](downloads/SHA256SUMS.txt)
