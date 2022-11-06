package main;

import controllers.login.AgentLoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.http.HttpClientUtil;

import java.io.IOException;

public class AgentClient extends Application {

    private static final String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/fxml/AgentLogin.fxml";
    AgentLoginController loginController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION));
            Parent root = loader.load();
            loginController = loader.getController();
            loginController.setStage(primaryStage);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image("/resources/images/turing.png"));
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void stop() throws Exception {
        HttpClientUtil.shutdown();
        loginController.close();
    }


}
