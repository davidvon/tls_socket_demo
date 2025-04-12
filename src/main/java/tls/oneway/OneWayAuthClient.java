package tls.oneway;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

import static tls.Common.getTrustManagerFactory;

public class OneWayAuthClient {
    public static void main(String[] args) throws Exception {
        String truststoreFile = "oneway/client-truststore.jks";
        char[] password = "zte123".toCharArray();

        TrustManagerFactory tmf = getTrustManagerFactory(truststoreFile, password);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        SSLSocketFactory factory = sslContext.getSocketFactory();
        SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 8444);
        System.out.println("[Client][OneWay-Auth-Mode]: socket connected");

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

