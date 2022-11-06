package users;

import users.DTO.AgentProgressData;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.util.List;

public class AgentEntry extends User {

    private final String teamName; // the team agent is part of

    private final int tasksToPull; // number of tasks to pull from the server

    /**
     * Number of threads for BruteForce task
     */
    private final int workerCount;
    private boolean startBruteforce;

    private String secretMessage;

    private final AgentProgressData progressData;

    List<String> myCandidateSolutions;


    public AgentEntry(String name, String team, int taskCount, int workers){
        super(name, ClientType.AGENT);
        this.teamName = team;
        this.tasksToPull = taskCount;
        this.startBruteforce = false;
        this.progressData = new AgentProgressData(name);
        this.secretMessage = null;
        if(workers < 0 || workers > 4){
            workers = 1;
        }
        this.workerCount = workers;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getTasksToPull() {
        return tasksToPull;
    }

    public int getWorkerCount() {
        return workerCount;
    }




    public ObservableValue<String> WorkerCountProperty() {
        return new SimpleStringProperty(String.valueOf(this.workerCount));
    }

    public ObservableValue<String> TaskSizeProperty() {
        String taskSizeString = String.valueOf(this.tasksToPull);
        return new SimpleStringProperty(taskSizeString);
    }

    public void setReady(boolean startBF) {
        this.startBruteforce = startBF;
    }


    public AgentProgressData getProgressData() {
        return progressData;
    }

    public void setSecretMessage(String secret) {
        this.secretMessage = secret;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Agent ").append(this.username).append(" from team ").append(this.teamName);
        return sb.toString();
    }

    public void updateTaskInQueueCount(int size) {
        this.progressData.setPendingTasks(size);
    }

    public void updateProgressWithCandidate(String candidate) {
        progressData.setLastCandidateFound(candidate);


    }
}
