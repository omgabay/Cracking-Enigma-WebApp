package controllers;
import users.DTO.MachineSetupData;
import auxiliary.Dictionary;
import auxiliary.Message;
import auxiliary.Battlefield;
import bruteforce.AgentSolutionEntry;
import candidateTable.CandidateTableController;
import teamsTable.TeamsTableController;
import exceptions.EnigmaException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import machine.Engine;
import machine.Enigma;
import machine.IEngine;
import okhttp3.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.NotNull;
import users.UBoat;
import utils.Constants;
import utils.gui.CreateAlertBox;
import utils.http.HttpClientUtil;
import static utils.Configuration.*;
import static utils.Constants.*;
import static utils.Constants.GSON_INSTANCE;
import static utils.http.HttpClientUtil.HTTP_CLIENT;

public class UBoatController {

    private Stage stage;

    @FXML TextField fileLoadLabel;

    @FXML Label nameLabel;
    @FXML Label rotorCountLabel;
    @FXML Label machineRotorCountLabel;
    @FXML Label reflectorCountLabel;
    @FXML Label alphabetLabel;
    @FXML Label battlefieldLabel;
    @FXML Label difficultyLabel;
    @FXML ToggleGroup reflectorSelection;


    // ComboBoxes for Code setup

    @FXML HBox rotorPositionsCbox;

    @FXML HBox rotorIdsCbox;


    // Current Machine Setup Label

    @FXML Label currentSetupLabel;
    @FXML Label currentSetupLabel2;


    @FXML TextField uboatMessage;

    @FXML TextField secretMessage;

    @FXML ComboBox<String> dictionaryCbox;


    @FXML Label updateMessageLabel;

    @FXML HBox candidateHBox;

    @FXML BorderPane contestTeamBP;


    private String clientName;



    private  StringProperty loadStatusMessageProperty;

    private  StringProperty currentMachineSetupProperty;

    private final IEngine engine = new Engine();

    private Enigma enigmaMachine;

    private StringProperty uboatMessageProperty;

    CandidateTableController candidatesController;

    TeamsTableController teamsTableController;




    @FXML
    public void initialize(){
        currentMachineSetupProperty = new SimpleStringProperty("");
        loadStatusMessageProperty = new SimpleStringProperty("");
        uboatMessageProperty = new SimpleStringProperty("");
        fileLoadLabel.textProperty().bind(loadStatusMessageProperty);
        currentSetupLabel.textProperty().bind(currentMachineSetupProperty);
        currentSetupLabel2.textProperty().bind(currentSetupLabel.textProperty());



        uboatMessageProperty.bind(this.uboatMessage.textProperty());
        loadCandidateTable();
    }


    public void setClientName(String uboatName){
        this.nameLabel.setText(uboatName+"'s UBoat");
        this.clientName = uboatName;
        loadContestTeamTable(uboatName);
    }




    public void loadFileClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Enigma XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Enigma XML", "*.xml"));
        File file = fileChooser.showOpenDialog(stage);

        if(file == null){
            return;
        }

        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart(file.getName(), file.getName(), RequestBody.create(file, MediaType.parse("text/plain")))
                        .build();

        Request request = new Request.Builder()
                .url(UPLOAD_FILE_URL)
                .post(body)
                .build();

        Call call = HTTP_CLIENT.newCall(request);

        call.enqueue(new Callback() {
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code() != 200){
                    String responseBody = response.body().string();
                    Platform.runLater(()->
                            loadStatusMessageProperty.set("Something went wrong: " + responseBody)
                    );
                }
                if(response.body() != null){
                    try {
                        String json = response.body().string();
                        System.out.println("uboat client received:" + json);
                        engine.loadFromJson(json);

                        Platform.runLater(() -> {
                            loadStatusMessageProperty.set(file.getName());
                            updateUI();
                        });

                    }catch(NullPointerException e){
                        e.printStackTrace();
                    }catch(EnigmaException ee){
                        String message = ee.getMessage();
                        CreateAlertBox.createAlert(message,stage);
                    }


                }

            }
            public void onFailure(Call call, IOException e) {}
        });

    }

    private void updateUI() {
        int totalNumOfRotors = engine.getTotalAvailableRotors();
        int machineRotorsSize = engine.getNumberOfMachineRotors();
        int reflectorCount = engine.getNumOfReflectors();
        String alphabet = engine.getMachineAlphabet().toString();

        // Update Machine Info
        rotorCountLabel.setText(String.valueOf(totalNumOfRotors));
        machineRotorCountLabel.setText(String.valueOf(machineRotorsSize));
        reflectorCountLabel.setText(String.valueOf(reflectorCount));
        alphabetLabel.setText(alphabet);


        // Update Battle Info
        Battlefield battle = engine.getBattlefield();
        battlefieldLabel.setText(battle.getBattleName());
        difficultyLabel.setText(battle.getBattleDifficulty());

        // Clear Cboxes
        this.rotorIdsCbox.getChildren().clear();
        this.rotorPositionsCbox.getChildren().clear();


        // Create Cboxes
        createRotorIDCbox(machineRotorsSize,totalNumOfRotors);
        createRotorPositionCbox(machineRotorsSize,alphabet);

        // Update Reflector Options
        updateReflectorOptions(reflectorCount);

        // Update dictionary
        Dictionary dictionary = engine.getDictionary();
        ObservableList<String> words = FXCollections.observableArrayList(dictionary.getWordsList());
        this.dictionaryCbox.setItems(words);
    }

    private void createRotorPositionCbox(int machineRotorsSize, String alphabet) {
        List<Character> letters = alphabet.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        this.rotorPositionsCbox.setSpacing(10.0);

        for(int i=0; i<machineRotorsSize; i++){
            ComboBox<Character> cb = new ComboBox<>(FXCollections.observableArrayList(letters));
            //cb.setMaxWidth(20);
            cb.getSelectionModel().select(0);
            this.rotorPositionsCbox.getChildren().add(cb);
        }
    }

    /**
     * @param rotorsCount the number of rotors inside the machine
     * @param totalNumberOfRotors  the total number of rotors at our disposal      *
     * This function will create the comboboxes for choosing the rotors to use inside the machine
     * The function will be called after each time we load enigma xml file
     */
    private void createRotorIDCbox(int rotorsCount, int totalNumberOfRotors) {
        List<Integer> range = IntStream.rangeClosed(1,totalNumberOfRotors).boxed().collect(Collectors.toList());
        this.rotorIdsCbox.setSpacing(10.0);
        for(int i=0; i<rotorsCount; i++){
            ComboBox<Integer> cb = new ComboBox<>(FXCollections.observableArrayList(range));
            cb.getSelectionModel().select(0);
            this.rotorIdsCbox.getChildren().add(cb);
        }
    }

    private void updateReflectorOptions(int reflectorCount){
        int count = reflectorCount;
        for (Toggle toggle : this.reflectorSelection.getToggles()) {
            RadioButton radioButton = (RadioButton) toggle;
            if(count <= 0){
                radioButton.setVisible(false);
            }
            count--;
        }
    }


    public void logoutClicked() {
        String finalUrl = HttpUrl
                .parse(LOGOUT)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String text = response.body().string();
                    System.out.println("Message from server: " + text);
                    updateMessageLabel.setText(text);
                    candidatesController.clearCandidateTable();
                }

            }
        });


    }

    @FXML public void uboatReadyClicked(ActionEvent event){
        String encrypted = this.secretMessage.getText();
        if(encrypted == null || encrypted.isEmpty()){
            return;
        }
        System.out.println("Sending to server ready request");
        String finalUrl = HttpUrl
                .parse(Constants.READY_UPDATE_URL)
                .newBuilder()
                .addQueryParameter(Constants.SECRET_PARAM_NAME, encrypted)
                .build()
                .toString();




        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("UBoat client failed to send Ready update to the server");
            }

            @Override

            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    response.close();
                    System.out.print(rawBody);
                    UBoat uboat = GSON_INSTANCE.fromJson(rawBody, UBoat.class);
                    if(uboat.isReady()) {
                        System.out.println(uboat.username + " uboat client is starting to download candidates");
                        candidatesController.startRefreshTask(uboat.username);
                    }
                    else{
                        Platform.runLater(()->{
                            System.out.println(rawBody);
                            createAlert("Wait for all teams to be ready",event);
                        });
                    }
                }
            }
        });


    }

    public void setMachineCodeClicked(ActionEvent event){

        // Collecting user input for machine setup
        List<Integer> rotorIDs = fetchRotorIDSelection();
        List<Character> rotorPositions = fetchRotorPositionSelection();
        String reflectorChoice = getSelectedReflector();

        if(rotorIDs == null || rotorPositions == null){
            System.out.println("error");
            return;
        }
        System.out.println(Arrays.toString(rotorIDs.toArray()));
        System.out.println(Arrays.toString(rotorPositions.toArray()));
        System.out.println("Reflector " + reflectorChoice);

        engine.setupMachine(rotorIDs,rotorPositions, reflectorChoice, null);
        updateServerWithSetup(rotorIDs,rotorPositions,reflectorChoice);
        this.enigmaMachine = engine.getMachine();

        // Updating current machine configuration label after setup
        String currentMachineConfiguration = engine.getMachine().getCurrentConfiguration();
        this.currentMachineSetupProperty.set(currentMachineConfiguration);
    }



    public void createRandomMachineCode(ActionEvent event){
        this.enigmaMachine = engine.setupMachineAtRandom();
        String currentMachineConfiguration = engine.getMachine().getCurrentConfiguration();
        this.currentMachineSetupProperty.set(currentMachineConfiguration);
        updateServerWithSetup(engine.getMachine().getRotorIDs(),engine.getMachine().getRotorPositions(),engine.getMachine().getReflector().toString());
    }

    private String getSelectedReflector() {
        RadioButton selected = (RadioButton) this.reflectorSelection.getSelectedToggle();
        return selected.getText();
    }

    private List<Integer> fetchRotorIDSelection() {
        List<Integer> ids = new ArrayList<>();
        for(Node node : this.rotorIdsCbox.getChildren()){
            if(node instanceof ComboBox){
                ComboBox<?> comboBox = (ComboBox<?>) node;
                Object selected = comboBox.getSelectionModel().getSelectedItem();
                if (selected instanceof Integer){
                    ids.add((Integer) selected);
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }
        return ids;
    }


    private List<Character> fetchRotorPositionSelection(){
        List<Character> rotorPositions = new ArrayList<>();
        for(Node node : this.rotorPositionsCbox.getChildren()){
            if(!(node instanceof ComboBox)){
                return null;
            }
            ComboBox<?> comboBox = (ComboBox<?>) node;
            Object selected = comboBox.getSelectionModel().getSelectedItem();

            if(!(selected instanceof Character)){
                return null;
            }
            rotorPositions.add((Character) selected);

        }
        return rotorPositions;
    }


    public void setStage(Stage stage){
        this.stage = stage;
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


     public void addWordToMessage(ActionEvent event){
        String word = (String) dictionaryCbox.getSelectionModel().getSelectedItem();
        // Getting message from uboat text field
        String message = this.uboatMessage.getText();
        if(!message.isEmpty()) {
            message = message + " " + word;
        }else{
            message = word;
        }

        this.uboatMessage.setText(message);
        this.updateSecretMessage(message);
    }

    private void updateSecretMessage(String original) {
        if(enigmaMachine == null){
            return;
        }
        this.enigmaMachine.resetMachine();
        Message m = this.enigmaMachine.processText(original);
        String encrypted = m.getProcessed();
        this.secretMessage.setText(encrypted);
    }






    private void updateServerWithSetup(List<Integer> rotors, List<Character> positions,String reflectorChoice) {
        MachineSetupData setupData = new MachineSetupData(rotors,positions,reflectorChoice);
        String json = GSON_INSTANCE.toJson(setupData);
        String finalUrl = HttpUrl.parse(UPDATE_SERVER_WT_SETUP_URL)
                .newBuilder().
                addQueryParameter(ENIGMA_SETUP,json)
                .build().toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    System.out.println("Machine updated successfully!");
                    StringBuilder sb = new StringBuilder();
                    sb.append("Rotors:").append(rotors).append(" Reflector: ").append(reflectorChoice);
                    System.out.println(sb);
                }
            }
        });

    }

    private void declareWinner(AgentSolutionEntry solutionEntry) {
        String finalUrl = HttpUrl
                .parse(Constants.WINNER_UPDATE_URL)
                .newBuilder()
                .addQueryParameter(Constants.AGENT_PARAM, solutionEntry.getAgentName())
                .addQueryParameter(Constants.CANDIDATES, solutionEntry.getCandidate())
                .addQueryParameter(Constants.UBOAT, this.clientName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    AgentSolutionEntry solutionEntry = GSON_INSTANCE.fromJson(rawBody, AgentSolutionEntry.class);
                    System.out.println(solutionEntry.getAgentName() +": won the contest and found word " + uboatMessageProperty.get());
                    response.close();
                }
            }
        });
    }


    private void loadContestTeamTable(String uboatName){
        URL contestTeamsTableUrl = getClass().getResource("/fxml/teamsTable.fxml");
        FXMLLoader loader = new FXMLLoader(contestTeamsTableUrl);
        try {
            this.contestTeamBP.setCenter(loader.load());
            teamsTableController = loader.getController();
            teamsTableController.setUboatName(uboatName);
            teamsTableController.startRefreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void clearTextClicked(ActionEvent event){
        this.uboatMessage.clear();
        this.secretMessage.clear();
    }


}
