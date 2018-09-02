package com.gmail.shellljx.MultiServerChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class SocketUtil {

    private ArrayList<ClientThread> clients;
    private final MultiServerChat plugin;
    private ServerSocket serverSocket;
    private ServerThread serverThread;
    private boolean isStart = false;

    public SocketUtil(MultiServerChat plugin) {
        this.plugin = plugin;
    }

    public boolean startServer(int port) {
        try {
            clients = new ArrayList<>();
            serverSocket = new ServerSocket(port);
            serverThread = new ServerThread(serverSocket);
            serverThread.start();
            isStart = true;
            return true;
        } catch (IOException e) {
            isStart = false;
            return false;
        }

    }

    public void closeServer() {
        try {
            if (serverThread != null) {
                serverThread.stop();
            }
            for (ClientThread client : clients) {
                client.getWriter().println("CLOSE");
                client.getWriter().flush();
                client.stop();
                client.writer.close();
                client.reader.close();
                client.socket.close();
                clients.remove(client);
            }
            if (serverSocket != null)
                serverSocket.close();
            isStart = false;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            isStart = true;
        }
    }

    //������߳�
    class ServerThread extends Thread {

        private ServerSocket serverSocket;

        public ServerThread(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    ClientThread client = new ClientThread(socket);
                    client.setName(String.valueOf(socket.getPort()));
                    client.start();
                    clients.add(client);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    class ClientThread extends Thread {

        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientThread(Socket socket) {
            try {
                this.socket = socket;
                reader = new BufferedReader(new InputStreamReader(socket
                        .getInputStream()));
                writer = new PrintWriter(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public BufferedReader getReader() {
            return this.reader;
        }

        public PrintWriter getWriter() {
            return this.writer;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String message = null;
            try {
                while (true) {
                    message = reader.readLine();
                    if (message.equalsIgnoreCase("CLOSE")) {
                        reader.close();
                        writer.close();
                        socket.close();
                        for (ClientThread client : clients) {
                            if (client.getName().equalsIgnoreCase(String.valueOf(socket.getPort()))) {
                                ClientThread tmp = client;
                                clients.remove(client);
                                tmp.stop();
                                return;
                            }
                        }
                    }
                    dispatcherMessage(message, getName());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void dispatcherMessage(String message, String clientName) {
        for (ClientThread client : clients) {
            if (!clientName.equals(client.getName())) {
                client.getWriter().println(message);
                client.getWriter().flush();
            }
        }

    }
}
