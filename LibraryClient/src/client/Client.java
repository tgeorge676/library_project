package client;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


//User Client
public class Client extends Application {


    @Override
    public void start(Stage applicationStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/gui/ClientGUI.fxml"));
            VBox vbox = loader.<VBox>load();
            Scene app = new Scene(vbox);
            applicationStage.setScene(app);
            applicationStage.setTitle("The Library");
            applicationStage.show();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Application.launch(args);
    }
}
