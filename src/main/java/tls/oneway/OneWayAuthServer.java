package tls.oneway;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

import static tls.Common.getKeyManagerFactory;

public class OneWayAuthServer {
    public static void main(String[] args) throws Exception {
        String keystoreFile = "oneway/server-keystore.jks";
        char[] password = "zte123".toCharArray();

        KeyManagerFactory kmf = getKeyManagerFactory(keystoreFile, password);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(8444);
        System.out.println("[Server][OneWay-Auth-Mode]: TLSv1.2 Server running on port 8444...");

        while (true) {
            try {
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                String request = reader.readLine();
                System.out.println("[Client-->Server]: " + request);

                String echo = String.format("Hello {%s}, welcome!!!\n", request);
                writer.write(echo + "\n");
                writer.flush();
                System.out.println("[Server-->Client]: " + echo);
            } catch (Exception e){
              System.out.println(e.getMessage());
            }
        }
    }
}
