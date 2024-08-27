package library;

import enums.ItemType;
import javafx.scene.image.Image;
import server.User;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class Library implements Serializable {
    ArrayList<LibraryItem> catalog;

    ArrayList<User> users;

    public ArrayList<User> getUsers() {return users;}

    public Library(){
        catalog = new ArrayList<>();
        users = new ArrayList<>();
        loadCatalogData();

        //for debugging only; delete this when ready to submit
        users.add(new User("test","test"));
    }

    public ArrayList<LibraryItem> getCatalog() {
        return catalog;
    }

    private void loadCatalogData(){
        try {
            File libraryFile = new File("src/library/libraryFile.ser");
            if (libraryFile.length() == 0) { //if the backup file is empty, process input from a text file
                File inputFile = new File("src/library/catalogInput.txt");
                BufferedReader br = new BufferedReader(new FileReader(inputFile));
                String line;
                while((line = br.readLine()) != null) {
                    LibraryItem item = new LibraryItem();
                    item.setItemType(ItemType.valueOf(line));
                    line = br.readLine();
                    item.setTitle(line);
                    line = br.readLine();
                    if (!line.contains(",")) item.setAuthors(new String[]{line});
                    else if (line.contains(",")){
                        String[] authors = line.split(",");
                        item.setAuthors(authors);
                    }
                    line = br.readLine();
                    item.setDescription(line);
                    line = br.readLine();
                    line = "src/library/images/" + line;
                    File cover = new File(line);
                    byte[] coverData = Files.readAllBytes(cover.toPath());
                    item.setCover(coverData);
                    line = br.readLine();
                    if (!line.isEmpty()) {
                        String[] pastUsers = line.split(",");
                        for(String u: pastUsers) {
                            item.addToHistory(u);
                        }
                    }
                    catalog.add(item);
                    line = br.readLine(); //move to next item
                }
                System.err.println("loaded from input file");
            }
            else { //if not, load from the backup file
                FileInputStream fis = new FileInputStream(libraryFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Library lib = (Library) ois.readObject();
                catalog = lib.getCatalog();
                users = lib.getUsers();
                System.err.println("loaded from backup file");
            }
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
    }

    public void addAccount(String[] u) {
        users.add(new User(u[0],u[1]));
    }

    public boolean containsUsername(String username) {
        for(User u: users) {
            if (u.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    public User getUser(String username) {
        for(User u: users) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }
    public LibraryItem getItem(LibraryItem item) {
        for(LibraryItem i: catalog) {
            if (i.equals(item)) return i;
        }
        return null;
    }

    public LibraryItem getItem(String title) {
        for(LibraryItem i: catalog) {
            if (i.getTitle().equals(title)) return i;
        }
        return null;
    }
    public LibraryItem processBorrow(String title) {
        LibraryItem borrowedItem = null;
        for(LibraryItem i: catalog) {
            if (i.getTitle().equals(title) && !i.isCheckedOut()) {
                i.setCheckedOut(true);
                borrowedItem = new LibraryItem(i);
                return borrowedItem;
            }
        }
        return null;
    }

    public LibraryItem processReturn(String title) {
        LibraryItem borrowedItem = null;
        for(LibraryItem i: catalog) {
            if (i.getTitle().equals(title)) {
                i.setCheckedOut(false);
                borrowedItem = new LibraryItem(i);
                break;
            }
        }
        return borrowedItem;
    }
    public void save() { //Saves contents of the library to a file
        try {
            FileOutputStream fos = new FileOutputStream(new File("src/library/libraryFile.ser"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
    }
}
