package controllers;

import users.DTO.AgentProgressData;
import candidateTable.CandidateTableController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import bruteforce.BruteforceAgentTask;
import machine.Engine;
import users.AgentEntry;
import users.UBoat;
import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;

import static utils.Constants.REFRESH_RATE;

public class AgentMainController implements Closeable {
    private Stage stage;

    @FXML Label agentName;
    @FXML Label teamLabel;
    @FXML Label statusMessageLabel;
    @FXML Label tasksInQueueLabel;
    @FXML Label tasksCompletedLabel;
    @FXML Label workersLabel;
    @FXML AnchorPane candidatesTableAP;

    CandidateTableController candidatesController;

    RequestEnigmaTimerTask enigmaRequest;
    private IntegerProperty tasksCompleted;
    private IntegerProperty tasksInQueue;
    private AgentEntry agentEntry;

    BruteforceAgentTask bfTask;

    Timer timer;


    @FXML public void initialize(){
        this.loadCandidatesTable();
        tasksInQueue = new SimpleIntegerProperty(0);
        tasksInQueueLabel.textProperty().bind(tasksInQueue.asString());
        tasksCompleted = new SimpleIntegerProperty(0);
        tasksCompletedLabel.textProperty().bind(tasksCompleted.asString());
    }



    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setAgentEntry(AgentEntry agent) {
        this.agentEntry = agent;
        statusMessageLabel.setText("Waiting for contest to start");
        workersLabel.setText(String.valueOf(agentEntry.getWorkerCount()));

        agentName.setText("Agent " + agentEntry.username);
        teamLabel.setText("Team " + agentEntry.getTeamName());
    }


    private void startGetEnigmaTimerTask() {
        String teamName = agentEntry.getTeamName();
        timer = new Timer(true);
        this.enigmaRequest = new RequestEnigmaTimerTask(teamName, this::startContest,timer);
        timer.schedule(enigmaRequest, REFRESH_RATE, REFRESH_RATE);
    }

    public void startContest(UBoat uboat){
        if(uboat.getMachine() == null){
            return;
        }
        enigmaRequest.cancelTask();

        // Load Enigma Machine
        Engine engine = new Engine();
        engine.loadCTEnigma(uboat.getMachine());
        engine.setupMachineAtRandom();

        bfTask = new BruteforceAgentTask(engine,this.agentEntry, this::updateAgentProgress);
        Thread t = new Thread(bfTask);
        t.setDaemon(true);
        t.start();
        candidatesController.startRefreshTask(uboat.username);
        statusMessageLabel.setText("Agent pulled Enigma Machine from Server\n");

    }


    public void startClientTimerTask(){
        startGetEnigmaTimerTask();
    }

    public void loadCandidatesTable(){
        URL candidateTableUrl = getClass().getResource("/fxml/candidateTable.fxml");
        try{
            FXMLLoader loader = new FXMLLoader(candidateTableUrl);
            candidatesTableAP.getChildren().add(loader.load());
            candidatesController = (CandidateTableController) loader.getController();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    public void updateAgentProgress(AgentProgressData agentProgressData){
        tasksInQueue.set(agentProgressData.getPendingTasks());
        tasksCompleted.set(agentProgressData.getCompletedTasks());
    }


    @Override
    public void close() throws IOException {
        this.timer.cancel();
        this.bfTask.cancel();
        this.candidatesController.cancelRefresher();
    }
}
