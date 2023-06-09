package info.kgeorgiy.ja.latanov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.nio.charset.StandardCharsets;

public class HelloUDPServer implements HelloServer {

    static class UdpSenderReceiver {
        private final DatagramSocket socket;

        UdpSenderReceiver(int port) throws SocketException {
            socket = new DatagramSocket(port);
        }

        DatagramPacket receive() throws IOException {
            byte[] getMessage = new byte[1024];
            DatagramPacket getPacket = new DatagramPacket(getMessage, getMessage.length);
            socket.receive(getPacket);
            return getPacket;
        }

        void send(DatagramPacket getPacket, String message) throws IOException {
            byte[] sendMessage = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length, getPacket.getSocketAddress());
            socket.send(sendPacket);
        }

        void close() {
            socket.close();
        }
    }

    private UdpSenderReceiver udpSenderReceiver;
    private ExecutorService receiver;

    @Override
    public void start(int port, int threads) {
        try {
            udpSenderReceiver = new UdpSenderReceiver(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        receiver = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            receiver.submit(() -> {
                while (!receiver.isShutdown()) {
                    DatagramPacket getPacket;
                    try {
                        getPacket = udpSenderReceiver.receive();
                        String sendBuffer = createResponse(getPacket);
                        udpSenderReceiver.send(getPacket, sendBuffer);
                    } catch (IOException e) {
                        if (!receiver.isShutdown()) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

    }

    private String createResponse(DatagramPacket getPacket) {
        return "Hello, " + new String(getPacket.getData(), getPacket.getOffset(), getPacket.getLength(), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        int threadsNumber = Integer.parseInt(args[1]);
        HelloUDPServer server = new HelloUDPServer();
        server.start(port, threadsNumber);
    }

    @Override
    public void close() {
        receiver.shutdown();
        udpSenderReceiver.close();
    }
}
