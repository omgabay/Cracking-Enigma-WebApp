package utils;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Configuration {

    // FXML Paths

    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/fxml/uboatMain.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/fxml/uboatLogin.fxml";


    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/cracking-enigma";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;




    public final static String UPLOAD_FILE_URL = FULL_SERVER_PATH + "/upload-enigma-file";

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";




    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();



    public static void createAlert(String message, ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Button b = (Button) event.getSource();
        Stage stage =  (Stage) b.getScene().getWindow();
        alert.setHeaderText(message);
        if(stage != null){
            alert.initOwner(stage);
        }
        Stage alertWindow = (Stage) alert.getDialogPane().getScene().getWindow();
        alert.show();
    }






 }
