package info.kgeorgiy.ja.latanov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author created by Daniil Latanov
 */
public class HelloUDPClient implements HelloClient {
    public static String hostName;
    public static int port;
    public static String prefix;
    public static int threads;
    public static int requests;


    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.out.println(" Problems with args");
            return;
        }
        try {
            hostName = args[0];
            port = Integer.parseInt(args[1]);
            prefix = args[2];
            threads = Integer.parseInt(args[3]);
            requests = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            return;
        }
        HelloUDPClient helloClient = new HelloUDPClient();
        helloClient.run(hostName, port, prefix, threads, requests);
    }

    @Override
    public void run(String host, int port, String prefix, int threads, int requests) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            int finalI = i + 1;
            executor.submit(() -> udp(host, port, prefix, requests, finalI));
        }
        executor.shutdown();
        if (!executor.isTerminated()) {
            executor.shutdown();
            boolean interrupted = false;
            while (!executor.isTerminated()) {
                try {
                    if (executor.awaitTermination(1L, TimeUnit.DAYS)) {
                        executor.shutdown();
                    }
                } catch (InterruptedException e) {
                    if (!interrupted) {
                        executor.shutdownNow();
                        interrupted = true;
                    }
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }


    private void udp(String host, int port, String prefix, int requests, int finalI) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(1000);
            for (int j = 0; j < requests; j++) {
                String request = prefix + finalI + "_" + (j + 1);
                DatagramPacket packet = new DatagramPacket(request.getBytes(), 0, request.length(), new InetSocketAddress(host, port));
                waitForGoodResponse(socket, request, packet);
            }
        } catch (IOException e) {
            System.err.println("IOException" + e.getMessage());
        }
    }

    private void waitForGoodResponse(DatagramSocket socket, String request, DatagramPacket packet) throws SocketException {
        while (true) {
            DatagramPacket responsePacket = getResponse();
            try {
                sendAndReceive(socket, packet, responsePacket);
            } catch (IOException e) {
                continue;
            }
            String createResponse = new String(responsePacket.getData(), 0, responsePacket.getLength());
            if (createResponse.endsWith(request)) {
                System.out.println(createResponse);
                break;
            }
        }
    }

    private DatagramPacket getResponse() {
        byte[] responseBuffer = new byte[1024];
        return new DatagramPacket(responseBuffer, 0, responseBuffer.length);
    }

    private void sendAndReceive(DatagramSocket socket, DatagramPacket packet, DatagramPacket responsePacket) throws IOException {
        socket.send(packet);
        socket.receive(responsePacket);
    }
}
