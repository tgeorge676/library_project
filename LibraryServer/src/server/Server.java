package server;
import library.Library;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private Library library;

    public Server() {
        library = new Library();
    }

    public Library getLibrary() {
        return library;
    }

    public void runServer(){
        try{
            setUpNetworking();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void setUpNetworking() throws Exception {
        ServerSocket serverSocket = new ServerSocket(4444);
        Object lock = new Object();
        while(true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connecting to port " + serverSocket + "...");
            ClientHandler handler = new ClientHandler(library,clientSocket,lock);
            Thread th = new Thread(handler);
            th.start();
            System.out.println("Connected to " + serverSocket);
        }
    }
    public static void main(String[] args) {new Server().runServer();
    }
}
