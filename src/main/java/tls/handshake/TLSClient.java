package tls.handshake;

import tls.Common;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TLSClient {
    public static void main(String[] args) throws Exception {
        String keystoreFile = "mutual/client-keystore.jks";
        String truststoreFile = "mutual/client-truststore.jks";
        String password = "zte123";

        SSLContext clientCtx = Common.createSSLContext(
            keystoreFile,
            truststoreFile,
            password
        );

        try (Socket socket = new Socket("localhost", 8443)) {
            System.out.println("🔌 Connected to server.");

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            SSLEngine engine = clientCtx.createSSLEngine();
            engine.setUseClientMode(true);
            engine.beginHandshake();

            runHandshake(engine, in, out);
        }
    }

    private static void runHandshake(SSLEngine engine, InputStream in, OutputStream out) throws Exception {
        SSLSession session = engine.getSession();
        ByteBuffer appData = ByteBuffer.allocate(session.getApplicationBufferSize());
        ByteBuffer netIn = ByteBuffer.allocate(session.getPacketBufferSize());
        ByteBuffer netOut = ByteBuffer.allocate(session.getPacketBufferSize());

        SSLEngineResult.HandshakeStatus hsStatus = engine.getHandshakeStatus();
        while (hsStatus != SSLEngineResult.HandshakeStatus.FINISHED &&
                hsStatus != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
            System.out.println("2222");
            switch (hsStatus) {
                case NEED_WRAP -> {
                    System.out.println("NEED_WRAP-start");
                    netOut.clear();
                    SSLEngineResult result = engine.wrap(ByteBuffer.allocate(0), netOut);
                    hsStatus = result.getHandshakeStatus();
                    out.write(netOut.array(), 0, netOut.position());
                    out.flush();
                    System.out.println("NEED_WRAP-end");
                }
                case NEED_UNWRAP -> {
                    System.out.println("NEED_UNWRAP-start");
                    netIn.clear();
                    int read = in.read(netIn.array());
                    if (read < 0) throw new IOException("Connection closed");
                    netIn.limit(read);
                    SSLEngineResult result = engine.unwrap(netIn, appData);
                    hsStatus = result.getHandshakeStatus();
                    System.out.println("NEED_UNWRAP-end");
                }
                case NEED_TASK -> {
                    System.out.println("NEED_TASK-start");
                    Runnable task;
                    while ((task = engine.getDelegatedTask()) != null) task.run();
                    hsStatus = engine.getHandshakeStatus();
                    System.out.println("NEED_TASK-end");
                }
                default -> throw new IllegalStateException("Unexpected status: " + hsStatus);
            }
        }
        System.out.println("✅ Client handshake complete.");
    }
}
