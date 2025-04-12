package tls;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class Common {
    public static TrustManagerFactory getTrustManagerFactory(String truststoreFile, char[] password) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        // 加载信任服务端的 truststore
        InputStream trustStoreStream = Common.class.getClassLoader().getResourceAsStream(truststoreFile);
        if (trustStoreStream == null) {
            throw new FileNotFoundException("Truststore not found in resources.");
        }
        KeyStore ts = KeyStore.getInstance("JKS");
        ts.load(trustStoreStream, password);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);
        return tmf;
    }

    public static KeyManagerFactory getKeyManagerFactory(String keystoreFile, char[] password) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        // 加载客户端证书 keystore
        InputStream keystoreStream = Common.class.getClassLoader().getResourceAsStream(keystoreFile);
        if (keystoreStream == null) {
            throw new FileNotFoundException("Keystore not found in resources.");
        }
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(keystoreStream, password);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);
        return kmf;
    }
}

