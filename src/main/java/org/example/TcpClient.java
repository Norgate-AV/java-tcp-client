package org.example;

import java.io.*;
import java.net.Socket;

public class TcpClient {
    private Socket socket;
    private BufferedInputStream reader;
    private BufferedOutputStream writer;

    public TcpClient(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.reader = new BufferedInputStream(this.socket.getInputStream());
            this.writer = new BufferedOutputStream(this.socket.getOutputStream());

            System.out.printf("Connected to %s:%d\n", this.socket.getInetAddress(), this.socket.getPort());
            this.listen();
        } catch (IOException e) {
            this.close();
        }
    }

    public void send(String message) {
        try {
            this.writer.write(message.getBytes());
            this.writer.flush();
            this.listen();
        } catch (IOException e) {
            this.close();
        }
    }

    private void close() {
        System.out.printf("Closing connection to %s:%d\n", this.socket.getInetAddress(), this.socket.getPort());

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

    private void listen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;

                while (socket.isConnected()) {
                    try {
                        int available = reader.available();
                        if (available == 0) {
                            continue;
                        }

                        byte[] buffer = new byte[available];
                        var result = reader.read(buffer);

                        if (result == -1) {
                            close();
                            break;
                        }

                        message = new String(buffer);
                        System.out.print(message);
                    } catch (IOException e) {
                        close();
                        break;
                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        var client = new TcpClient("192.168.10.47", 23);
        client.send("get connection\r\n");
        client.send("get device\r\n");
        client.send("get ip\r\n");
    }
}
