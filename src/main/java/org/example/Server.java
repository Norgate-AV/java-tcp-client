package org.example;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private final ServerSocket socket;

    public Server(ServerSocket socket) {
        this.socket = socket;
    }

    public void start() {
        try {
            while (!this.socket.isClosed()) {
                var client = this.socket.accept();
                System.out.println("Client connected: " + client.getInetAddress());

                var clientHandler = new ClientHandler(client);

                var thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            this.close();
        }
    }

    public void close() {
        try {
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try (var socket = new ServerSocket(9000)) {
            System.out.println("Server listening on port 9000");

            var server = new Server(socket);
            server.start();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
