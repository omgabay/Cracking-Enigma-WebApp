package controllers;
import agentProgressTable.ProgressTableController;
import bruteforce.AgentSolutionEntry;
import candidateTable.CandidateTableController;
import teamsTable.TeamsTableController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.DTOHolder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AgentEntry;
import users.AllyTeam;
import users.UBoat;
import users.User;
import utils.Constants;
import utils.http.HttpClientUtil;
import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Timer;

import static utils.CommonConstants.REFRESH_RATE;
import static utils.Configuration.*;

public class AlliesController implements Closeable {

    @FXML TableView<AgentEntry> agentsTable;
    @FXML TableColumn<AgentEntry, String> agentNameCol;
    @FXML TableColumn<AgentEntry,String> agentWorkersCol;

    @FXML TableColumn<AgentEntry,String> taskSizeCol;

    @FXML TableView<UBoat> uboatsTable;

    @FXML TableColumn<UBoat,String> battleNameCol;
    @FXML TableColumn<UBoat, String> uboatNameCol;
    @FXML TableColumn<UBoat, String> gameStatusCol;

    @FXML TableColumn<UBoat, String> difficultyCol;

    @FXML Tab contestTab;
    @FXML TabPane alliesTabPane;
    @FXML Label allyHeadingLabel;
    @FXML TextField contestPreviewLabel;

    @FXML TextField missionSizeTF;
    @FXML Label battleLabel;
    @FXML Label uboatLabel;
    @FXML Label statusLabel;
    @FXML Label difficultyLabel;
    @FXML Label registeredLabel;
    @FXML HBox candidateHBox;

    @FXML HBox agentProgressTable;
    @FXML BorderPane contestTeamBP;


    private Stage stage;
    private AlliesClientRefreshTask clientRefresher;
    CandidateTableController candidatesController;
    TeamsTableController teamsTableController;
    ProgressTableController agentProgressController;

    private User client;

    private String clientName;

    UBoat contestUboat;


    Timer timer;




    @FXML
    public void initialize(){
        this.client = DTOHolder.getInstance().getUser();
        this.clientName = client.getName();
        contestTab.setDisable(true);
        setupUBoatTable();
        setupAgentTable();
        allyHeadingLabel.setText("Ally " + clientName);


        uboatsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                contestPreviewLabel.setText(newSelection.getName());
            }
        });

        startDashboardRefresher();
    }

    private void setupAgentTable() {
        // Setting table columns for Agents Table
        agentNameCol.setCellValueFactory(
                cellData -> cellData.getValue().NameProperty()
        );

        agentWorkersCol.setCellValueFactory(
                cellData -> cellData.getValue().WorkerCountProperty()
        );

        taskSizeCol.setCellValueFactory(
                cellData -> cellData.getValue().TaskSizeProperty()
        );


    }

    private void setupUBoatTable() {
        // Setting table columns for UBoat

        battleNameCol.setCellValueFactory(
                cellData -> cellData.getValue().BattleNameProperty()
        );

        uboatNameCol.setCellValueFactory(
                cellData -> cellData.getValue().NameProperty()
        );

        gameStatusCol.setCellValueFactory(
                cellData -> cellData.getValue().TeamsRegisteredProperty()
        );

        difficultyCol.setCellValueFactory(
                cellData -> cellData.getValue().DifficultyProperty()
        );
    }




    @FXML public void joinContestClicked(){
        if(contestPreviewLabel.getText().isEmpty()){
            return;
        }

        String uboatName = contestPreviewLabel.getText();

        String finalUrl = HttpUrl
                .parse(Constants.JOIN_CONTEST_URL)
                .newBuilder()
                .addQueryParameter(Constants.UBOAT,uboatName)
                .addQueryParameter(Constants.USERNAME, this.clientName)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error in joining contest");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Platform.runLater(()->{
                        contestTab.setDisable(false);
                        alliesTabPane.getSelectionModel().select(1);
                        updateContestTab(uboatName);
                        loadCandidateTable();
                        loadProgressTable();
                        loadContestTeamTable(uboatName);
                    });
                }else{
                    String message = "Wait for UBoat to load contest to join";
                    createAlert(message,stage);

                }
            }
        });
    }




    @FXML public void readyBtnClicked(){
        String taskSize = missionSizeTF.getText();
        String finalUrl = HttpUrl
                .parse(Constants.READY_UPDATE_URL)
                .newBuilder()
                .addQueryParameter(Constants.ALLY_TASK_SIZE, taskSize)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error sending ready update from Allies Client");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    AllyTeam myTeam = GSON_INSTANCE.fromJson(rawBody, AllyTeam.class);
                    candidatesController.startRefreshTask(myTeam.getBattle());
                    agentProgressController.startAgentProgressRefresher(clientName);

                }
            }
        });
    }

    private void loadCandidateTable() {
        URL candidateTableUrl = getClass().getResource("/fxml/candidateTable.fxml");
        try{
            FXMLLoader loader = new FXMLLoader(candidateTableUrl);
            candidateHBox.getChildren().add(loader.load());
            this.candidatesController = loader.getController();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void loadProgressTable(){
        URL url = getClass().getResource(PROGRESS_TABLE_FXML);
        try{
            FXMLLoader loader = new FXMLLoader(url);
            agentProgressTable.getChildren().add(loader.load());
            agentProgressController = loader.getController();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void loadContestTeamTable(String uboatName){
        URL url = getClass().getResource(TEAMS_TABLE_FXML);

        try {
            FXMLLoader loader = new FXMLLoader(url);
            this.contestTeamBP.setCenter(loader.load());
            teamsTableController = loader.getController();
            teamsTableController.setUboatName(uboatName);
            teamsTableController.startRefreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void updateUboatsTable(List<UBoat> uBoatList){
        uboatsTable.getItems().clear();
        uboatsTable.getItems().addAll(uBoatList);
    }
    public void updateAgentsTable(AllyTeam myTeam){
        agentsTable.getItems().clear();
        agentsTable.getItems().addAll(myTeam.getAgentList());
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }



    private void startDashboardRefresher(){
        this.clientRefresher = new AlliesClientRefreshTask(this.clientName ,this::updateUboatsTable,this::updateAgentsTable);
        timer = new Timer();
        timer.schedule(clientRefresher, REFRESH_RATE, REFRESH_RATE);
    }



    @Override
    public void close() throws IOException {
        if (clientRefresher != null && timer != null) {
            clientRefresher.cancel();
            timer.cancel();
        }
    }


    public void plus50(ActionEvent event){
        int newVal = Integer.parseInt(missionSizeTF.getText()) + 50;
        missionSizeTF.setText(String.valueOf(newVal));
    }

    public void minus50(ActionEvent event){
        int newVal = Integer.parseInt(missionSizeTF.getText()) - 50;
        missionSizeTF.setText(String.valueOf(newVal));
    }

    private void updateContestTab(String uboatName){
        String finalUrl = HttpUrl
                .parse(Constants.GET_USER_RESOURCE_PAGE)
                 .newBuilder()
                 .addQueryParameter(Constants.USERNAME, uboatName)
                 .build()
                 .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.err.println("Error in update contest tab");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    UBoat uboat = GSON_INSTANCE.fromJson(rawBody,UBoat.class);
                    Platform.runLater(()->{

                        String battleName = uboat.BattleNameProperty().getValue();
                        String uboatName = uboat.getName();
                        String difficulty = uboat.getDifficulty();
                        String registered = uboat.TeamsRegisteredProperty().getValue();
                        battleLabel.setText(battleName);
                        uboatLabel.setText(uboatName);
                        difficultyLabel.setText((difficulty));
                        registeredLabel.setText(registered);
                    });
                }
            }
        });

    }





    public void winnerWasFound(AgentSolutionEntry solution){
        this.timer.cancel();
    }


    public void setClientName(String allyName) {
        this.clientName = allyName;
    }


    public static void createAlert(String message, Stage stage){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(message);
        if(stage != null){
            alert.initOwner(stage);
        }
        Stage alertWindow = (Stage) alert.getDialogPane().getScene().getWindow();
        alert.show();
    }
}
