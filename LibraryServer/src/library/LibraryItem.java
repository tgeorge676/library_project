package library;

import enums.ItemType;
import javafx.scene.image.Image;
import server.User;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

public class LibraryItem implements Serializable {
    private static final long serialVersionUID = 42L;

    private ItemType itemType;
    private String title;
    private String[] authors;
    private String description;
    private ArrayList<String> history;
    private boolean checkedOut;
    byte[] cover;

    private ArrayList<String> reviews;

    public LibraryItem() {
        history = new ArrayList<>();
        reviews = new ArrayList<>();
    }

    public LibraryItem(ItemType itemType, String title, String[] authors, String description, byte[] cover) {
        this.itemType = itemType;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.cover = cover;
        checkedOut = false;

        reviews = new ArrayList<>();
        history = new ArrayList<>();
    }

    public LibraryItem(LibraryItem libraryItem) {
        this.itemType = libraryItem.getItemType();
        this.title = libraryItem.getTitle();
        this.authors = libraryItem.getAuthors();
        this.description = libraryItem.getDescription();
        this.cover = libraryItem.getCover();
        this.history = libraryItem.getHistory();
        this.checkedOut = libraryItem.isCheckedOut();
        this.reviews = libraryItem.getReviews();
    }

    public ArrayList<String> getReviews() {return reviews;}
    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getAuthors() {
        return authors;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public void addToHistory(String user) {
        history.add(user);
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public boolean isCheckedOut() {return checkedOut;}
    public void setCheckedOut(boolean c) {checkedOut = c;}

    public boolean equals(LibraryItem item) {
        return item.getTitle().equals(title) && item.getItemType() == itemType && Arrays.equals(item.getAuthors(), authors) && Arrays.equals(item.getCover(), cover) && item.getDescription().equals(description);
    }

    public void addReview(String review) {reviews.add(review);}
    @Override
    public String toString() {
        return this.itemType + ":" + this.title + " by " + Arrays.toString(authors) + ":\n" + description;
    }
}
