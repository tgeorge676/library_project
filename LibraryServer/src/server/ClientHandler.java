package server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

import enums.Action;
import enums.Result;
import library.Library;
import library.LibraryItem;
import server.User;

public class ClientHandler implements Runnable {
    private Library library;
    private Socket socket;
    private ObjectInputStream reader;
    private ObjectOutputStream writer;

    private Object lock;

    public ClientHandler(Library library, Socket clientSocket, Object lock) {
        this.library = library;
        socket = clientSocket;
        this.lock = lock;
        try {
            reader = new ObjectInputStream(socket.getInputStream());
            writer = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Action data;
            while ((data = (Action) reader.readObject()) != null) {
                System.out.println("Received from Client " + socket.getInetAddress());
                //Handle the input (will be the main way of determining what actions to take)
                if (data == Action.CREATE) {
                    String[] credentials = (String[]) reader.readObject();
                    library.addAccount(credentials);
                    writer.writeObject("Account added.");
                    writer.flush();
                }
                if (data == Action.LOGIN) {
                    String[] credentials = (String[]) reader.readObject();
                    processLogin(credentials);
                    writer.flush();
                }
                if (data == Action.BORROW) {
                    synchronized (lock) {
                        String title = (String) reader.readObject();
                        LibraryItem item = library.processBorrow(title);
                        if (item != null) {
                            String user = (String) reader.readObject();
                            library.getUser(user).addToHistory(item);
                            library.getItem(item).addToHistory(user);
                            writer.reset();
                            writer.writeObject(Result.SUCCESS);
                            writer.writeObject(library.getCatalog());
                            writer.writeObject(library.getUser(user).getBorrowedItems());
                        } else {
                            writer.reset();
                            writer.writeObject(Result.FAILURE);
                        }
                        writer.flush();
                    }
                }
                if (data == Action.RETURN) {
                    synchronized (lock) {
                        String title = (String) reader.readObject();
                        LibraryItem item = library.processReturn(title);
                        String user = (String) reader.readObject();
                        library.getUser(user).removeFromHistory(item);
                        library.getItem(item).getHistory().remove(user);
                        writer.reset();
                        writer.writeObject(Result.SUCCESS);
                        writer.writeObject(library.getCatalog());
                        writer.writeObject(library.getUser(user).getBorrowedItems());
                        writer.flush();
                    }
                }
                if (data == Action.SEARCH) {
                    writer.reset();
                    writer.writeObject(library.getCatalog());
                    writer.flush();
                }
                if (data == Action.REVIEW) {
                    String title = (String) reader.readObject();
                    String review = (String) reader.readObject();
                    LibraryItem item = library.getItem(title);
                    item.addReview(review);
                    writer.reset();
                    writer.writeObject(library.getCatalog());
                    writer.flush();
                }
                if (data == Action.RESET) {
                    String username = (String) reader.readObject();
                    String password = (String) reader.readObject();
                    Result pReset = resetPassword(username, password);
                    writer.reset();
                    writer.writeObject(pReset);
                    writer.flush();
                }
                if (data == Action.LOGOUT) {
                    socket.close();
                    System.out.println("Client " + socket + " Disconnected");
                    break;
                }
                library.save();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public Result resetPassword(String user, String pw) {
        for(User u: library.getUsers()) {
            if (u.getUsername().equals(user)) {
                if (u.getPassword().equals(pw)) {
                    return Result.SAME_PASSWORD;
                }
                u.setPassword(pw);
                return Result.SUCCESS;
            }
        }
        return Result.NO_ACCOUNT;
    }


    private void processLogin(String[] login) throws IOException {
        //check if user exists
        if (!library.containsUsername(login[0])) { //user doesn't exist
            writer.writeObject(Result.NO_ACCOUNT);
            return;
        }
        if (!library.getUser(login[0]).getPassword().equals(login[1])) { //incorrect password
            writer.writeObject(Result.INCORRECT_PASSWORD);
        }
        writer.reset();
        writer.writeObject(Result.SUCCESS);
        writer.writeObject(library.getCatalog());
        writer.writeObject(library.getUser(login[0]).getBorrowedItems());
    }
}
