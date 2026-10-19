import com.android.apksig.ApkSigner;
import com.android.apksig.ApkSignerEngine;
import com.android.apksig.DefaultApkSignerEngine;
import com.android.apksig.apk.ApkFormatException;
import com.android.apksig.util.DataSource;
import com.android.apksig.util.RunnablesExecutor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/** Use apksig's older, no-pre-block-padding entry point; retain APK ZIP records. */
public final class SignV2WithoutGap {
    private static final class NoGapEngine implements ApkSignerEngine {
        private final DefaultApkSignerEngine delegate;

        NoGapEngine(DefaultApkSignerEngine delegate) { this.delegate = delegate; }

        @Override public void setExecutor(RunnablesExecutor executor) {
            delegate.setExecutor(executor);
        }
        @Override public Set<String> initWith(byte[] manifest, Set<String> entries) {
            return delegate.initWith(manifest, entries);
        }
        @Override public void inputApkSigningBlock(DataSource block) {
            delegate.inputApkSigningBlock(block);
        }
        @Override public InputJarEntryInstructions inputJarEntry(String name) {
            return delegate.inputJarEntry(name);
        }
        @Override public InspectJarEntryRequest outputJarEntry(String name) {
            return delegate.outputJarEntry(name);
        }
        @Override public InputJarEntryInstructions.OutputPolicy inputJarEntryRemoved(String name) {
            return delegate.inputJarEntryRemoved(name);
        }
        @Override public void outputJarEntryRemoved(String name) {
            delegate.outputJarEntryRemoved(name);
        }
        @Override public OutputJarSignatureRequest outputJarEntries()
                throws ApkFormatException, NoSuchAlgorithmException,
                       InvalidKeyException, SignatureException {
            return delegate.outputJarEntries();
        }
        @Override public OutputApkSigningBlockRequest outputZipSections(
                DataSource entries, DataSource directory, DataSource eocd)
                throws IOException, NoSuchAlgorithmException,
                       InvalidKeyException, SignatureException {
            return delegate.outputZipSections(entries, directory, eocd);
        }
        @Override public OutputApkSigningBlockRequest2 outputZipSections2(
                DataSource entries, DataSource directory, DataSource eocd)
                throws IOException, NoSuchAlgorithmException,
                       InvalidKeyException, SignatureException {
            OutputApkSigningBlockRequest request =
                    delegate.outputZipSections(entries, directory, eocd);
            if (request == null) return null;
            return new OutputApkSigningBlockRequest2() {
                @Override public byte[] getApkSigningBlock() {
                    return request.getApkSigningBlock();
                }
                @Override public int getPaddingSizeBeforeApkSigningBlock() { return 0; }
                @Override public void done() { request.done(); }
            };
        }
        @Override public void outputDone() { delegate.outputDone(); }
        @Override public void signV4(DataSource apk, File output, boolean ignoreFailures)
                throws SignatureException {
            delegate.signV4(apk, output, ignoreFailures);
        }
        @Override public boolean isEligibleForSourceStamp() {
            return delegate.isEligibleForSourceStamp();
        }
        @Override public byte[] generateSourceStampCertificateDigest()
                throws SignatureException {
            return delegate.generateSourceStampCertificateDigest();
        }
        @Override public void close() { delegate.close(); }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            throw new IllegalArgumentException(
                    "Usage: SignV2WithoutGap input.apk output.apk keystore.p12 alias password-file");
        }
        char[] password = Files.readString(Path.of(args[4])).strip().toCharArray();
        try {
            KeyStore store = KeyStore.getInstance("PKCS12");
            try (InputStream input = Files.newInputStream(Path.of(args[2]))) {
                store.load(input, password);
            }
            PrivateKey key = (PrivateKey) store.getKey(args[3], password);
            List<X509Certificate> certificates = new ArrayList<>();
            for (java.security.cert.Certificate cert : store.getCertificateChain(args[3])) {
                certificates.add((X509Certificate) cert);
            }
            var signer = new DefaultApkSignerEngine.SignerConfig.Builder(
                    args[3], key, certificates).build();
            var delegate = new DefaultApkSignerEngine.Builder(List.of(signer), 26)
                    .setV1SigningEnabled(false)
                    .setV2SigningEnabled(true)
                    .setV3SigningEnabled(false)
                    .setVerityEnabled(false)
                    .build();
            try (var engine = new NoGapEngine(delegate)) {
                new ApkSigner.Builder(engine)
                        .setInputApk(new File(args[0]))
                        .setOutputApk(new File(args[1]))
                        .setAlignmentPreserved(true)
                        .build()
                        .sign();
            }
            System.out.println("V2-only APK signed with ZIP entry layout preserved and no pre-block gap.");
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
