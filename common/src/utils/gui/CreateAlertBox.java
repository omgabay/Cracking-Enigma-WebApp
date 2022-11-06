package utils.gui;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import utils.CommonConstants;

public class CreateAlertBox {

    public static void createAlert(String message, Stage stage){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(message);
        if(stage != null){
            alert.initOwner(stage);
        }
        Stage alertWindow = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(CommonConstants.ICON_RESOURCE));
        alert.show();
    }

}
