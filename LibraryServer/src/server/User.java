package server;
import library.LibraryItem;

import javax.sql.rowset.serial.SerialBlob;
import java.io.Serializable;
import java.util.ArrayList;

/* A User */
public class User implements Serializable {
    private static final long serialVersionUID = 39L;
    private String username;
    private String password;
    ArrayList<LibraryItem> borrowedItems;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        borrowedItems = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addToHistory(LibraryItem item) {
        borrowedItems.add(item);
    }

    public void removeFromHistory(LibraryItem item) {
        for(int i = 0; i < borrowedItems.size(); i++){
            if (borrowedItems.get(i).equals(item)) {
                borrowedItems.remove(i);
                break;
            }
        }
    }

    public ArrayList<LibraryItem> getBorrowedItems() {
        return borrowedItems;
    }
}
