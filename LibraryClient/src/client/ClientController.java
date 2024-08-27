package client;

import enums.Action;
import enums.ItemType;
import enums.Result;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import library.Library;
import library.LibraryItem;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ClientController {
    //Login Page Elements
    @FXML
    private TextField usernameField;
    @FXML
    private VBox mainPage;
    @FXML
    private VBox loginScreen;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label warningText;
    @FXML
    private Button createAccountButton;
    @FXML
    private Button passwordResetButton;

    //Account Creation Page elements
    @FXML
    private VBox accountCreationPage;
    @FXML
    private TextField setUsername;
    @FXML
    private TextField setPassword;
    @FXML
    private Button accountCreationBackButton;

    //Main Page Elements
    @FXML
    private StackPane stackPane;
    @FXML
    private TextField searchBar;
    @FXML
    private ListView<ImageView> historyDisplay;
    @FXML
    private ListView<ImageView> catalogDisplay;
    @FXML
    private Button searchButton;
    @FXML
    private Button logoutButton;

    //Item Page elements
    @FXML
    private VBox itemPage;
    @FXML
    private Button checkoutButton;
    @FXML
    private ImageView bookCover;
    @FXML
    private Button itemPageBackButton;
    @FXML
    private Label typeText;
    @FXML
    private Label titleText;
    @FXML
    private Label authorsText;
    @FXML
    private Label descriptionText;
    @FXML
    private Label checkoutWarningText;
    @FXML
    private Button viewReviewButton;

    //search page fields
    @FXML
    private VBox searchPage;
    @FXML
    private Button searchButton_S;
    @FXML
    private TextField searchBar_S;
    @FXML
    private Button backButton_S;
    @FXML
    private ListView<ImageView> resultsDisplay;
    @FXML
    private ComboBox<String> searchFilter;

    //Password Reset Page
    @FXML
    private VBox passwordResetPage;
    @FXML
    private TextField getUsername;
    @FXML
    private TextField newPassword;
    @FXML
    private TextField confirmNewPassword;
    @FXML
    private Button passwordResetBackButton;
    @FXML
    private Button resetPasswordButton;
    @FXML
    private Label passwordResetWarningText;

    //Review page
    @FXML
    private VBox reviewPage;
    @FXML
    private ListView<String> reviewDisplay;
    @FXML
    private Label reviewPageTitle;
    @FXML
    private TextArea review;
    @FXML
    private Button sendReviewButton;

    //Internal fields
    private ArrayList<LibraryItem> libraryCatalog = new ArrayList<>();
    private ArrayList<LibraryItem> userHistory = new ArrayList<>();
    private ArrayList<LibraryItem> returnedResults = new ArrayList<>();
    private ObjectOutputStream writer;
    private ObjectInputStream reader;
    private boolean isConnected = false;
    private boolean returning = false;

    //Checks the given username and password
    public void checkLogin(KeyEvent event) throws Exception {
        if (event.getCode() == KeyCode.ENTER) {
            setUpNetworking();
            String password = passwordField.getText();
            //Process login request
            writer.reset();
            writer.writeObject(Action.LOGIN);
            writer.writeObject(new String[]{usernameField.getText(), password});
            writer.flush();
            Result received = (Result) reader.readObject();
            if (received == Result.NO_ACCOUNT) {
                warningText.setText("No Account Found");
                return;
            }
            if (received == Result.INCORRECT_PASSWORD) {
                warningText.setText("Incorrect Password");
                return;
            } else if (received == Result.SUCCESS) {
                //if login successful
                System.out.println("login successful");
                loginScreen.setVisible(false);
                mainPage.setVisible(true);
                initializeMainPage();
            }
        }
    }

    private void initializeMainPage() {
        try {
            warningText.setText("");
            ArrayList<LibraryItem> catalog = (ArrayList<LibraryItem>) reader.readObject(); //catalog
            ArrayList<LibraryItem> items = (ArrayList<LibraryItem>) reader.readObject(); //user's history of checked out items

            //display icons for items
            List<ImageView> catalogItems = new ArrayList<>();
            for (LibraryItem i : catalog) {
                if (!i.isCheckedOut()) {
                    ImageView cover = new ImageView(new Image(new ByteArrayInputStream(i.getCover())));
                    cover.setPreserveRatio(true);
                    cover.setFitWidth(100);
                    catalogItems.add(cover);
                    libraryCatalog.add(i);
                }
            }
            List<ImageView> historyItems = new ArrayList<>();
            catalogDisplay.getItems().addAll(catalogItems);
            for (LibraryItem i : items) {
                ImageView cover = new ImageView(new Image(new ByteArrayInputStream(i.getCover())));
                cover.setPreserveRatio(true);
                cover.setFitWidth(100);
                historyItems.add(cover);
                userHistory.add(i);
            }
            historyDisplay.getItems().addAll(historyItems);
            System.out.println("Library data get!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void viewAccountCreationPage() throws IOException {
        accountCreationPage.setVisible(true);
        loginScreen.setVisible(false);
    }

    public void backToLogin() {
        accountCreationPage.setVisible(false);
        loginScreen.setVisible(true);
    }

    //Creates a new account
    public void createAccount() {
        backToLogin();
        try {
            setUpNetworking();
            writer.reset();
            writer.writeObject(Action.CREATE);
            writer.writeObject(new String[]{setUsername.getText(), setPassword.getText()});
            writer.flush();

            System.out.println(reader.readObject());

        } catch (Throwable e) {
        }
    }

    public void viewPasswordResetPage() {
        loginScreen.setVisible(false);
        passwordResetPage.setVisible(true);
    }

    public void resetPassword() {
        try {
            setUpNetworking();
            String username = getUsername.getText();
            String np = newPassword.getText();
            String np_confirm = confirmNewPassword.getText();

            //check confirmation
            if (!np.equals(np_confirm)) {
                passwordResetWarningText.setText("Confirmation does not match");
                return;
            }
            writer.reset();
            writer.writeObject(Action.RESET);
            writer.writeObject(username);
            writer.writeObject(np);
            writer.flush();
            Result received = (Result) reader.readObject();
            if (received == Result.SUCCESS) {
                backToLoginReset();
                System.out.println("password reset");
            }
            else if (received == Result.NO_ACCOUNT) {
                passwordResetWarningText.setText("No account associated with that username");
                return;
            }
            else if (received == Result.SAME_PASSWORD) {
                passwordResetWarningText.setText("New password same as old password. \n Please choose a different password.");
                return;
            }



        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void backToLoginReset() {
        passwordResetPage.setVisible(false);
        loginScreen.setVisible(true);
        newPassword.clear();
        getUsername.clear();
        confirmNewPassword.clear();
    }

    public void getItemPage() {
        descriptionText.setWrapText(true);
        if (catalogDisplay.getSelectionModel().getSelectedIndex() == -1) return;
        itemPage.setVisible(true);
        mainPage.setVisible(false);
        LibraryItem item = libraryCatalog.get(catalogDisplay.getSelectionModel().getSelectedIndex());
        bookCover.setImage(new Image(new ByteArrayInputStream(item.getCover())));
        typeText.setText(item.getItemType().name());
        titleText.setText(item.getTitle());
        //clean up the description
        descriptionText.setText(item.getDescription());
        checkoutButton.setText("Checkout");
        System.out.println(item.getHistory());
        returning = false;
    }

    public void checkoutItem() throws Exception {
        if (returning) {
            System.out.println("returning");
            writer.reset();
            writer.writeObject(Action.RETURN);
            writer.writeObject(titleText.getText());
            writer.writeObject(usernameField.getText());
            writer.flush();
            itemPage.setVisible(false);
            mainPage.setVisible(true);
            Result received = (Result) reader.readObject();
            if (received == Result.SUCCESS) {
                libraryCatalog.clear();
                userHistory.clear();
                catalogDisplay.getItems().clear();
                historyDisplay.getItems().clear();
                //update main page
                initializeMainPage();
            }
        } else {
            writer.reset();
            writer.writeObject(Action.BORROW);
            writer.writeObject(titleText.getText());
            writer.writeObject(usernameField.getText());
            writer.flush();
            itemPage.setVisible(false);
            mainPage.setVisible(true);
            Result received = (Result) reader.readObject();
            if (received == Result.SUCCESS) {
                libraryCatalog.clear();
                userHistory.clear();
                catalogDisplay.getItems().clear();
                historyDisplay.getItems().clear();
                //update main page
                initializeMainPage();
            }
            //if item could not be checked out
            if (received == Result.FAILURE) {
                checkoutWarningText.setText("Could not check out item");
                System.err.println("failed");
            }
        }
    }

    public void backToMainPage() {
        itemPage.setVisible(false);
        mainPage.setVisible(true);
    }

    public void getBorrowedItemPage() {
        descriptionText.setWrapText(true);
        if (historyDisplay.getSelectionModel().getSelectedIndex() == -1) return;
        itemPage.setVisible(true);
        mainPage.setVisible(false);
        LibraryItem item = userHistory.get(historyDisplay.getSelectionModel().getSelectedIndex());
        bookCover.setImage(new Image(new ByteArrayInputStream(item.getCover())));
        typeText.setText(item.getItemType().name());
        titleText.setText(item.getTitle());
        //clean up the description
        descriptionText.setText(item.getDescription());
        checkoutButton.setText("Return");
        System.out.println(item.getHistory());
        returning = true;
    }

    public void backToMainPage_S() {
        searchPage.setVisible(false);
        searchFilter.setValue(null);
        searchBar_S.clear();
        mainPage.setVisible(true);
    }

    public void getSearchedItemPage() {
        if (resultsDisplay.getSelectionModel().getSelectedIndex() == -1) return;
        itemPage.setVisible(true);
        searchPage.setVisible(false);
        LibraryItem item = returnedResults.get(resultsDisplay.getSelectionModel().getSelectedIndex());
        bookCover.setImage(new Image(new ByteArrayInputStream(item.getCover())));
        typeText.setText(item.getItemType().name());
        titleText.setText(item.getTitle());
        //clean up the description
        descriptionText.setText(item.getDescription());
        checkoutButton.setText("Checkout");
        System.out.println(item.getHistory());
        returning = false;
    }

    public void filterResults() {
        String type = searchFilter.getValue();
        String query = searchBar_S.getText();
        returnedResults.clear();
        resultsDisplay.getItems().clear();
        for (LibraryItem i : libraryCatalog) {
            if ((i.getTitle().toLowerCase().contains(query.toLowerCase())) && i.getItemType().name().equals(type)) {
                returnedResults.add(i);
                ImageView cover = new ImageView(new Image(new ByteArrayInputStream(i.getCover())));
                cover.setPreserveRatio(true);
                cover.setFitWidth(100);
                resultsDisplay.getItems().add(cover);
            }
        }
        System.out.println("filtered");
    }

    public void searchCatalog() {
        try {
            resultsDisplay.getItems().clear();
            returnedResults.clear();
            String query;
            if (mainPage.isVisible()) query = searchBar.getText();
            else query = searchBar_S.getText();
            searchBar_S.setText(query);

            searchPage.setVisible(true);
            mainPage.setVisible(false);
            //search for item
            writer.reset();
            writer.writeObject(Action.SEARCH);
            writer.flush();
            libraryCatalog = (ArrayList<LibraryItem>) reader.readObject(); //get updated catalog from server
            for (LibraryItem i : libraryCatalog) {
                if (i.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    returnedResults.add(i);
                    ImageView cover = new ImageView(new Image(new ByteArrayInputStream(i.getCover())));
                    cover.setPreserveRatio(true);
                    cover.setFitWidth(100);
                    resultsDisplay.getItems().add(cover);
                }
            }
            searchFilter.getItems().clear();
            EnumSet<ItemType> types = EnumSet.allOf(ItemType.class);
            for (ItemType i : types) {
                searchFilter.getItems().add(i.name());
            }
            System.out.println(query);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void viewReviewPage(){
        reviewDisplay.getItems().clear();
        reviewPageTitle.setText("");
        itemPage.setVisible(false);
        reviewPage.setVisible(true);
        reviewPageTitle.setText("Recent Reviews for " +  titleText.getText());
        LibraryItem item;
        if (!returning) item = libraryCatalog.get(catalogDisplay.getSelectionModel().getSelectedIndex());
        else item = userHistory.get(historyDisplay.getSelectionModel().getSelectedIndex());
        ArrayList<String> reviews = item.getReviews();
        if (reviews != null) {
            reviewDisplay.getItems().addAll(reviews);
        }
    }

    public void sendReview(){
        try {
            String r = review.getText();
            writer.reset();
            writer.writeObject(Action.REVIEW);
            writer.writeObject(titleText.getText());
            writer.writeObject(r);
            writer.flush();

            libraryCatalog = (ArrayList<LibraryItem>) reader.readObject();
            reviewDisplay.getItems().clear();
            reviewDisplay.getItems().addAll(libraryCatalog.get(catalogDisplay.getSelectionModel().getSelectedIndex()).getReviews());
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
    }
    public void backToItemPage(){
        itemPage.setVisible(true);
        reviewPage.setVisible(false);
    }

    public void logout() {
        try {
            writer.reset();
            writer.writeObject(Action.LOGOUT);
            writer.flush();
            writer.close();
            reader.close();
            writer = null;
            reader = null;
            //clear all local data
            libraryCatalog.clear();
            userHistory.clear();
            catalogDisplay.getItems().clear();
            historyDisplay.getItems().clear();

            //reset login page
            mainPage.setVisible(false);
            loginScreen.setVisible(true);
            usernameField.setText("");
            passwordField.setText("");
            isConnected = false;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setUpNetworking() throws IOException {
        if (!isConnected) {
            Socket socket = new Socket("localhost", 4444); //change if hosting server remotely
            writer = new ObjectOutputStream(socket.getOutputStream());
            reader = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
        }
    }
}
