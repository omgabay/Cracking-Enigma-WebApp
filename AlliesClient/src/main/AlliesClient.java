package main;

import controllers.AlliesController;
import javafx.application.Application;
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
import javafx.scene.image.Image;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AllyTeam;
import utils.Configuration;
import utils.http.HttpClientUtil;

import java.io.IOException;

import static utils.Configuration.MAIN_PAGE_FXML_RESOURCE_LOCATION;

public class AlliesClient extends Application {

    private final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/fxml/AllyLogin.fxml";
    private final StringProperty errorMessageProperty = new SimpleStringProperty("");


    @FXML TextField usernameInput;
    @FXML Label errorMessageLabel;

    private AlliesController alliesController;

    Stage stage;


    public void initialize(){
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION));
            loader.setController(this);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image("resources/images/england.png"));
            primaryStage.show();
            this.stage = primaryStage;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void registerAlly(ActionEvent actionEvent){

        String allyName = usernameInput.getText();

        if(allyName.isEmpty()){
            errorMessageProperty.set("Name field is empty, you cannot login with no name");
            return;
        }

        String finalUrl = HttpUrl
                .parse(Configuration.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", allyName)
                .addQueryParameter("type", "ally")
                .build()
                .toString();
        System.out.println("New request is launched for: " + finalUrl);
        errorMessageProperty.set("New request is launched for: " + finalUrl);

        HttpClientUtil.runAsync(finalUrl, new Callback(){

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Platform.runLater(() ->
                            loadAllyScreen(allyName)
                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("error - " + e.getMessage())
                );
            }
        });


    }

    public void loadAllyScreen(String allyName){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_PAGE_FXML_RESOURCE_LOCATION));

        System.out.println("Ally " + allyName + " successfully logged in");
        DTOHolder holder = DTOHolder.getInstance();
        holder.setUser(new AllyTeam(allyName));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        AlliesController controller = (AlliesController) loader.getController();
        controller.setStage(stage);
        stage.show();
    }



    @Override
    public void stop() throws Exception {
        HttpClientUtil.shutdown();
    }

}
