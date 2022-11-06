package agentProgressTable;

import users.DTO.AgentProgressData;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Timer;

public class ProgressTableController implements Closeable {
    @FXML TableView<AgentProgressData> progressDataTable;
    @FXML TableColumn<AgentProgressData, String> agentNameCol;
    @FXML TableColumn<AgentProgressData, Integer> candidatesFoundCol;
    @FXML TableColumn<AgentProgressData, Integer> pendingCountCol;
    @FXML TableColumn<AgentProgressData, Integer> totalCountCol;
    @FXML TableColumn<AgentProgressData, String> lastResultCol;
    Timer timer;

    @FXML
    public void initialize(){
        agentNameCol.setCellValueFactory(
                cellData -> cellData.getValue().AgentNameProperty()
        );
        candidatesFoundCol.setCellValueFactory(
                cellData -> cellData.getValue().CandidateFoundProperty()
        );
        lastResultCol.setCellValueFactory(
                cellData -> cellData.getValue().LastCandidateProperty()
        );
        pendingCountCol.setCellValueFactory(
                cellData -> cellData.getValue().PendingCountProperty()
        );
        totalCountCol.setCellValueFactory(
                cellData -> cellData.getValue().TotalCountProperty()
        );


    }

    public void updateTable(List<AgentProgressData> newData){
        this.progressDataTable.getItems().clear();
        this.progressDataTable.getItems().addAll(newData);
    }


    public void startAgentProgressRefresher(String allyName){

        this.timer = new Timer(true);
        ProgressTableRefresher progressRefresher = new ProgressTableRefresher(allyName,null ,this::updateTable);
        timer.schedule(progressRefresher, 2000, 4000);
    }

    @Override
    public void close() throws IOException {
        timer.cancel();
    }
}
