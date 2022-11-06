package candidateTable;

import bruteforce.AgentSolutionEntry;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AllyTeam;
import utils.Constants;
import utils.http.HttpClientUtil;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

import static utils.Constants.GSON_INSTANCE;

public class CandidateTableController {

    @FXML TableView<AgentSolutionEntry>  candidateTable;
    @FXML TableColumn<AgentSolutionEntry,String> candidateCol;
    @FXML TableColumn<AgentSolutionEntry, String> agentNameCol;
    @FXML TableColumn<AgentSolutionEntry,String> teamNameCol;
    @FXML TableColumn<AgentSolutionEntry,String> machineCodeCol;

    private Timer timer;

    private final IntegerProperty version = new SimpleIntegerProperty(0);


    public void initialize(){
        candidateCol.setCellValueFactory(
                cellData -> cellData.getValue().CandidateProperty()
        );

        agentNameCol.setCellValueFactory(
                cellData -> cellData.getValue().AgentNameProperty()
        );

        teamNameCol.setCellValueFactory(
                cellData -> cellData.getValue().TeamNameProperty()
        );

        machineCodeCol.setCellValueFactory(
                cellData -> cellData.getValue().MachineCodeProperty()
        );

    }


    public void addAgentSolutions(List<AgentSolutionEntry> solutionEntryList){
        int newVal = version.get() + solutionEntryList.size();
        version.set(newVal);
        candidateTable.getItems().addAll(solutionEntryList);
    }


    public void startRefreshTask(String uboatName){
        RefreshCandidateTableTask task = new RefreshCandidateTableTask(uboatName,this::addAgentSolutions, version);
        timer = new Timer();
        timer.schedule(task,1000, Constants.REFRESH_RATE);
    }


    public void cancelRefresher() {
        this.timer.cancel();
    }

    public void startRefreshTask() {
        String finalUrl = HttpUrl
                .parse(Constants.GET_USER_RESOURCE_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.CLIENT_TYPE, Constants.ALLY)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error in refreshing candidates table");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String jsonResponse = response.body().string();
                    AllyTeam allyTeam = GSON_INSTANCE.fromJson(jsonResponse, AllyTeam.class);
                    String uboatName = allyTeam.getBattle();
                    if(uboatName  != null){
                        startRefreshTask(uboatName);
                    }
                }
            }
        });



    }

    public void clearCandidateTable() {
        this.candidateTable.getItems().clear();
    }
}
