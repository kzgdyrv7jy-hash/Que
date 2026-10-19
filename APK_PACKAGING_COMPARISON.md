# Daylight, official AerioTV and Que APK packaging comparison

The user supplied DaylightTV 1.0.0 as another APK that uploads to VirusTotal
without the 3MB/multiple-contained-files warning. The official AerioTV 0.5.13 APK
also uploads without it, according to the user. The supplied Daylight APK is
analysed locally and is not redistributed in this repository.

All three packages pass Android signature verification, ZIP integrity checks and
16KiB native-library alignment. They all use ZIP version-needed 0, deflated
binary Android manifests, normal unencrypted entries and Gradle metadata first.
The official and Que manifests occupy the same entry index, so a late manifest
does not by itself explain their different upload warnings. Daylight uses a
debug signing certificate; Que has a valid personal release certificate. Signing
validity is not the identified problem.

## Remaining structural correlation

| APK | User-reported upload warning | V2 signature | Gap before APK signing block |
| --- | --- | --- | ---: |
| DaylightTV 1.0.0 | No | Verifies | 0 bytes |
| Official AerioTV 0.5.13 | No | Verifies | 0 bytes |
| Que, v2-only copy | Yes | Verifies | 2,512 bytes |
| Que, preserved local-entry layout | Yes | Verifies | 2,480 bytes |
| New Que no-gap copy | Not yet tested | Verifies | 0 bytes |

The gap is valid page padding produced by the standalone signing tool. Its
presence correlates with the warning in these examples, but this comparison does
not prove that VirusTotal's upload classifier uses it. The cause remains a
hypothesis pending the controlled upload test below.

## No-gap comparison APK

File: `AerioTV-Que-0.5.13-que.1-v2-no-gap.apk`.
Size: 12,222,417 bytes.
SHA-256: `b93c7d855634f936746c85689c42eb8fda1922b4d89ff8dcbb921469dc5b52fe`.

This copy is generated with Android's apksig library. A narrow engine adapter
uses the library's legacy `outputZipSections` method, which creates a valid
signature without adding a gap before the signing block. `ApkSigner` still
writes the APK and `DefaultApkSignerEngine` still performs the cryptographic
signing. No custom signature algorithm, modified application code, ZIP payload
rewriting or new key is used. The reproducible
[adapter source](tools/SignV2WithoutGap.java) is included.

Verified checks:

- V2 verifies; v1, v3, v3.1 and v4 absent.
- Certificate SHA-256 matches the existing personal key:
  `c66eabc3339a28adcfca2e3003ecdb679812def71f59e6068a20d0c9d1ca389e`.
- All 1,024 app payloads match the previous Que build byte for byte.
- All 12,154,448 bytes of local ZIP entries, headers, padding and compressed
  streams match the verified unsigned Gradle output exactly.
- The 63,851-byte central-directory contents match the unsigned output exactly;
  the end-of-central-directory offset is updated for the new signature block.
- The v2 block starts immediately after the last local ZIP entry, with zero gap.
- CRC checks and 4-byte / 16KiB alignment checks pass.
- Package `com.aeriotv.blue`, version `0.5.13-que.1` / `6901`, minimum API26 and
  target API36 are preserved. FFmpeg libraries remain present for all three ABIs.
- All 455 recorded [application build inputs](downloads/build-inputs-sha256.json)
  still match. The existing [source archive](downloads/AerioTV-Que-0.5.13-source.zip)
  applies to the application payload. No application rebuild or JVM test rerun
  was required for this packaging-only comparison.

## Reproduction

Compile `tools/SignV2WithoutGap.java` with JDK21 and Android build-tools 36.0.0
`lib/apksigner.jar` on the classpath. Run with the same classpath and these five
arguments: unsigned input APK, output APK, existing PKCS12 keystore, key alias,
and password-file path. Keep the keystore and password file outside the
repository. The helper does not print their contents.

The legacy library method is deprecated; it is used only to produce this
controlled packaging comparison. APK verification uses the unmodified Android
build-tools `apksigner verify` and `zipalign` commands.

## VirusTotal test status

Upload the no-gap APK using the same browser and workflow as the other APKs,
and record whether the warning appears. No VirusTotal upload or scan was run
by this build workflow. Warning removal and per-engine scan coverage remain
unverified. A result of `Undetected` is not a byte-coverage measurement; see the
[previous comparison results and limits](V2_ONLY_VALIDATION.md).

Public verification files:

- [Signature verification](downloads/v2-no-gap-signature-verification.txt)
- [APK metadata](downloads/v2-no-gap-apk-metadata.txt)
- [Payload and signing-block checks](downloads/v2-no-gap-comparison.json)
- [Download checksums](downloads/SHA256SUMS.txt)
