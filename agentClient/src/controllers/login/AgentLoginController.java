package controllers.login;

import controllers.AgentMainController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AgentEntry;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

import static utils.Configuration.*;
import static utils.Constants.REFRESH_RATE;

public class AgentLoginController implements Closeable{

    private Stage stage;
    private AgentMainController mainController;
    private Timer timer;

    @FXML TextField userText;
    @FXML TextField taskSizeInput;
    @FXML ToggleGroup workersSelection;
    @FXML ComboBox<String> teamSelection;

    @FXML Slider taskCountSlider;
    @FXML Label errorMessageLabel;

    @FXML public void initialize(){
        LoginRefreshTask loginRefreshTask = new LoginRefreshTask(this::updateTeamComboBox);
        timer = new Timer();
        timer.schedule(loginRefreshTask,1000,2*REFRESH_RATE);
    }


    public void updateTeamComboBox(List<String> teams){

            for (String ally : teams) {
                teamSelection.getItems().add(ally);
            }

    }


    public void setStage(Stage primaryStage){
        this.stage = primaryStage;

    }


    public void loginButtonClicked() {

        if(teamSelection.getSelectionModel().isEmpty()){
            // update error message and return
            this.errorMessageLabel.setText("You cant login without a team, wait if needed");
            return;
        }

        String username = userText.getText();
        String team =  teamSelection.getSelectionModel().getSelectedItem();

        int taskCount = (int) taskCountSlider.getValue();


        RadioButton radioButton = (RadioButton) workersSelection.getSelectedToggle();
        int workers = Integer.parseInt(radioButton.getText());

        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, username)
                .addQueryParameter(Constants.CLIENT_TYPE, Constants.AGENT_PARAM)
                .addQueryParameter(Constants.ALLY, team)
                .addQueryParameter(Constants.AGENT_WORKER_COUNT, String.valueOf(workers))
                .addQueryParameter(Constants.AGENT_TASK_COUNT, String.valueOf(taskCount))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String json = response.body().string();
                    System.out.println(json);
                    AgentEntry agent =  GSON_INSTANCE.fromJson(json,AgentEntry.class);
                    timer.cancel();
                    timer.purge();
                    Platform.runLater(() -> {
                            loadAgentScreen(agent);
                    });
                }
            }


        });


    }

    private void loadAgentScreen(AgentEntry agent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(AGENT_PAGE_FXML_RESOURCE_LOCATION));
        Parent root;
        try {
            System.out.println(agent);
            root = loader.load();
            mainController = (AgentMainController) loader.getController();
            mainController.setAgentEntry(agent);
            mainController.setStage(stage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            mainController.startClientTimerTask();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




    @Override
    public void close() throws IOException {
        this.timer.cancel();
        this.timer.purge();
    }
}
