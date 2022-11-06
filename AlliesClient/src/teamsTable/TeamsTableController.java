package teamsTable;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import users.AllyTeam;
import utils.Constants;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

public class TeamsTableController implements Closeable {


    @FXML TableView<AllyTeam> teamsTableView;
    @FXML TableColumn<AllyTeam, String> teamNameCol;
    @FXML TableColumn<AllyTeam, Integer> agentCountCol;

    @FXML TableColumn<AllyTeam, Integer> missionSizeCol;
    @FXML TableColumn<AllyTeam, String> teamReadyCol;

    Timer timer;
    String uboatName;



    @FXML
    public void initialize(){
        teamNameCol.setCellValueFactory(
                cellData -> cellData.getValue().NameProperty()
        );
        agentCountCol.setCellValueFactory(
                cellData -> cellData.getValue().AgentCountProperty()
        );
        missionSizeCol.setCellValueFactory(
                cellData -> cellData.getValue().missionSizeProperty()
        );
        teamReadyCol.setCellValueFactory(
                cellData -> cellData.getValue().readyStatusProperty()
        );
    }

    public void startRefreshTable(){
        this.timer = new Timer(true);
        RefreshTeamsTask refreshTeamsTask = new RefreshTeamsTask(uboatName, this::updateTable);
        timer.schedule(refreshTeamsTask, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    public void updateTable(List<AllyTeam> teamsList){
        if(teamsList != null) {
            this.teamsTableView.getItems().clear();
            teamsTableView.getItems().addAll(teamsList);
        }
    }


    public void setUboatName(String uboatName){
        this.uboatName = uboatName;
    }


    @Override
    public void close() throws IOException {
        timer.cancel();
    }


}
