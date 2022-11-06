package controllers;

import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.UBoat;
import utils.Configuration;
import utils.http.HttpClientUtil;
import java.io.IOException;
import java.net.URL;


import static utils.Configuration.MAIN_PAGE_FXML_RESOURCE_LOCATION;
import static utils.Configuration.GSON_INSTANCE;

public class UBoatLoginController {
    @FXML TextField userText;
    @FXML Label errorMessageLabel;
    private Stage stage;

    private final StringProperty errorMessageProperty = new SimpleStringProperty("");
    UBoatController uboatController;


    public void initialize(){
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }
    public void registerUboat(ActionEvent actionEvent) {
        String userName = userText.getText();

        if(userName.isEmpty()){
            errorMessageProperty.set("Name field is empty, you cannot login with no name");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Configuration.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .addQueryParameter("type", "uboat")
                .build()
                .toString();
        System.out.println("New request is launched for: " + finalUrl);
        errorMessageProperty.set("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("error - "+e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    String body = response.body().string();
                    UBoat uboat = GSON_INSTANCE.fromJson(body, UBoat.class);
                    if (response.isSuccessful()) {
                        Platform.runLater(() ->
                                loadUboatScreen(uboat)
                        );
                    }
                }catch(JsonSyntaxException jse){
                    jse.printStackTrace();
                }
            }
        });

    }


    public void loadUboatScreen(UBoat uboat){
        URL uboatMainPage = getClass().getResource(MAIN_PAGE_FXML_RESOURCE_LOCATION);

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(uboatMainPage);
            Parent root = loader.load();
            this.uboatController = (UBoatController) loader.getController();
            System.out.println(uboat.getName());
            uboatController.setStage(stage);
            uboatController.setClientName(uboat.getName());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setStage(Stage primaryStage){
         stage = primaryStage;
    }
}
