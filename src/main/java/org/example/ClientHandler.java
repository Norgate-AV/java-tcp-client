package org.example;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> handlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            this.username = this.reader.readLine();

            handlers.add(this);

            this.broadcast("Client connected: " + this.username);
        } catch (IOException e) {
            this.close();
        }
    }

    @Override
    public void run() {
        String message;

        while (this.socket.isConnected()) {
            try {
                message = this.reader.readLine();
                if (message == null) {
                    break;
                }

                System.out.println("Received: " + message);
                this.broadcast(message);
            } catch (IOException e) {
                this.close();
                break;
            }
        }
    }

    private void broadcast(String message) {
        for (var handler : handlers) {
            try {
                if (handler == this) {
                    continue;
                }

                handler.writer.write(message + "\n");
                handler.writer.flush();
            } catch (IOException e) {
                handler.close();
            }
        }
    }

    public void removeClient() {
        handlers.remove(this);
        this.broadcast("Client disconnected: " + this.username);
    }

    private void close() {
        this.removeClient();

        try {
            if (this.socket != null) {
                this.socket.close();
            }

            if (this.reader != null) {
                this.reader.close();
            }

            if (this.writer != null) {
                this.writer.close();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
