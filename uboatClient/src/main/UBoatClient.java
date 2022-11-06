package main;

import controllers.UBoatLoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.http.HttpClientUtil;
import java.io.IOException;
import java.util.Objects;

import static utils.Configuration.LOGIN_PAGE_FXML_RESOURCE_LOCATION;

public class UBoatClient extends Application {



    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION));
            Parent root = loader.load();
            UBoatLoginController controller = loader.getController();
            controller.setStage(primaryStage);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            try {
                Image icon = new Image("/resources/images/submarine.png");
                primaryStage.getIcons().add(icon);
            }catch(RuntimeException rte){
                rte.printStackTrace();
            }
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void stop() throws Exception {
        HttpClientUtil.shutdown();
    }
}
