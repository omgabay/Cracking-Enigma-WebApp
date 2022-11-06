package candidateTable;

import bruteforce.AgentSolutionEntry;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class main extends Application {

    public static final String path = "/candidateTable/candidateTable.fxml";
    private  CandidateTableController candidateTableController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getResource(path);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(url);
            Parent root = loader.load();
            candidateTableController = loader.getController();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            List<AgentSolutionEntry> solutionEntryList = new ArrayList<>();
            AgentSolutionEntry ase2 = new AgentSolutionEntry("abc", "Rotem", "secret", "<aaaa>");
            solutionEntryList.add(ase2);
            candidateTableController.addAgentSolutions(solutionEntryList);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void stop(){
        this.candidateTableController.cancelRefresher();
    }



}
