package tls.handshake;

import tls.Common;

import javax.net.ssl.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TLSServer {
    public static void main(String[] args) throws Exception {
        String keystoreFile = "mutual/server-keystore.jks";
        String truststoreFile = "mutual/server-truststore.jks";
        String password = "zte123";

        SSLContext serverCtx = Common.createSSLContext(
                keystoreFile,
                truststoreFile,
                password
        );

        try (ServerSocket ss = new ServerSocket(8443)) {
            System.out.println("🔐 Server waiting on port 8443...");
            Socket socket = ss.accept();
            System.out.println("🔗 Client connected!");

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            SSLEngine engine = serverCtx.createSSLEngine();
            engine.setUseClientMode(false);
            engine.setNeedClientAuth(true);
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
            System.out.println("1111");
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
                default -> {
                    System.out.println("x4-start");
                    System.out.println("Unexpected status: " + hsStatus);
                    System.out.println("x4-end");
                    throw new IllegalStateException("Unexpected status: " + hsStatus);
                }
            }
        }
        System.out.println("✅ Server handshake complete.");
    }
}
