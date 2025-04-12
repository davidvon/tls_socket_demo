package tls.mutual;
import javax.net.ssl.*;
import java.io.*;
import static tls.Common.getKeyManagerFactory;
import static tls.Common.getTrustManagerFactory;


public class MutualAuthClient {
    public static void main(String[] args) throws Exception {
        String keystoreFile = "mutual/client-keystore.jks";
        String truststoreFile = "mutual/client-truststore.jks";
        char[] password = "zte123".toCharArray();

        KeyManagerFactory kmf = getKeyManagerFactory(keystoreFile, password);
        TrustManagerFactory tmf = getTrustManagerFactory(truststoreFile, password);

        // 初始化 TLSv1.2 上下文
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        SSLSocketFactory factory = sslContext.getSocketFactory();
        SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 8443);
        System.out.println("[Client][Mutual-Auth-Mode]: socket connected.");

        socket.startHandshake(); // 手动触发握手（可选）

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String echo = "10039182";
        writer.write(echo + "\n");
        writer.flush();
        System.out.println("[Client-->Server]: " + echo);

        String response = reader.readLine();
        System.out.println("[Server-->Client]: " + response);
    }

}
