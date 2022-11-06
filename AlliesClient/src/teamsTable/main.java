package teamsTable;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class main extends Application {


    public static final String path = "/fxml/teamsTable.fxml";
    private TeamsTableController teamsTableController;




    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getResource(path);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(url);
            Parent root = loader.load();
            teamsTableController = loader.getController();
            teamsTableController.setUboatName("myUboat");
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            teamsTableController.startRefreshTable();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws IOException {
        this.teamsTableController.close();
    }
}
