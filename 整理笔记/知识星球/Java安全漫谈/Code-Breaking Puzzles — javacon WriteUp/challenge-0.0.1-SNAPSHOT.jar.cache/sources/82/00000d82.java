package org.apache.tomcat.util.net.jsse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.res.StringManager;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/jsse/PEMFile.class */
public class PEMFile {
    private static final StringManager sm = StringManager.getManager(PEMFile.class);
    private String filename;
    private List<X509Certificate> certificates;
    private PrivateKey privateKey;

    public List<X509Certificate> getCertificates() {
        return this.certificates;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PEMFile(String filename) throws IOException, GeneralSecurityException {
        this(filename, null);
    }

    public PEMFile(String filename, String password) throws IOException, GeneralSecurityException {
        this.certificates = new ArrayList();
        this.filename = filename;
        List<Part> parts = new ArrayList<>();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.US_ASCII));
        Throwable th = null;
        Part part = null;
        while (true) {
            try {
                String line = in.readLine();
                if (line == null) {
                    break;
                } else if (line.startsWith(Part.BEGIN_BOUNDARY)) {
                    part = new Part();
                    part.type = line.substring(Part.BEGIN_BOUNDARY.length(), line.length() - 5).trim();
                } else if (line.startsWith(Part.END_BOUNDARY)) {
                    parts.add(part);
                    part = null;
                } else if (part != null && !line.contains(":") && !line.startsWith(" ")) {
                    Part part2 = part;
                    part2.content += line;
                }
            } finally {
            }
        }
        if (in != null) {
            if (0 != 0) {
                try {
                    in.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            } else {
                in.close();
            }
        }
        for (Part part3 : parts) {
            String str = part3.type;
            boolean z = true;
            switch (str.hashCode()) {
                case -2076506627:
                    if (str.equals("X509 CERTIFICATE")) {
                        z = true;
                        break;
                    }
                    break;
                case -283732602:
                    if (str.equals("ENCRYPTED PRIVATE KEY")) {
                        z = true;
                        break;
                    }
                    break;
                case -189606537:
                    if (str.equals("CERTIFICATE")) {
                        z = true;
                        break;
                    }
                    break;
                case -170985982:
                    if (str.equals("PRIVATE KEY")) {
                        z = false;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    this.privateKey = part3.toPrivateKey(null);
                    break;
                case true:
                    this.privateKey = part3.toPrivateKey(password);
                    break;
                case true:
                case true:
                    this.certificates.add(part3.toCertificate());
                    break;
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/net/jsse/PEMFile$Part.class */
    private class Part {
        public static final String BEGIN_BOUNDARY = "-----BEGIN ";
        public static final String END_BOUNDARY = "-----END ";
        public String type;
        public String content;

        private Part() {
            this.content = "";
        }

        private byte[] decode() {
            return Base64.decodeBase64(this.content);
        }

        public X509Certificate toCertificate() throws CertificateException {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(decode()));
        }

        public PrivateKey toPrivateKey(String password) throws GeneralSecurityException, IOException {
            KeySpec keySpec;
            String[] strArr;
            if (password == null) {
                keySpec = new PKCS8EncodedKeySpec(decode());
            } else {
                EncryptedPrivateKeyInfo privateKeyInfo = new EncryptedPrivateKeyInfo(decode());
                SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(privateKeyInfo.getAlgName());
                SecretKey secretKey = secretKeyFactory.generateSecret(new PBEKeySpec(password.toCharArray()));
                Cipher cipher = Cipher.getInstance(privateKeyInfo.getAlgName());
                cipher.init(2, secretKey, privateKeyInfo.getAlgParameters());
                keySpec = privateKeyInfo.getKeySpec(cipher);
            }
            InvalidKeyException exception = new InvalidKeyException(PEMFile.sm.getString("jsse.pemParseError", PEMFile.this.filename));
            for (String algorithm : new String[]{"RSA", "DSA", "EC"}) {
                try {
                    return KeyFactory.getInstance(algorithm).generatePrivate(keySpec);
                } catch (InvalidKeySpecException e) {
                    exception.addSuppressed(e);
                }
            }
            throw exception;
        }
    }
}