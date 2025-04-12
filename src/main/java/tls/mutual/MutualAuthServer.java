package tls.mutual;

import javax.net.ssl.*;
import java.io.*;
import static tls.Common.getKeyManagerFactory;
import static tls.Common.getTrustManagerFactory;


public class MutualAuthServer {
    public static void main(String[] args) throws Exception {
        String keystoreFile = "mutual/server-keystore.jks";
        String truststoreFile = "mutual/server-truststore.jks";
        char[] password = "zte123".toCharArray();

        KeyManagerFactory kmf = getKeyManagerFactory(keystoreFile, password);
        TrustManagerFactory tmf = getTrustManagerFactory(truststoreFile, password);

        // 初始化 TLSv1.2 SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        // 启动 SSL 服务端
        SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(8443);
        serverSocket.setNeedClientAuth(true); // 开启客户端认证
        System.out.println("[Server][Mutual-Auth-Mode]: TLSv1.2 Server running on port 8443...");

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
