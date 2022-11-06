package users.DTO;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

public class AgentProgressData {
    private final String agentName;
    private int candidatesFound;
    private String lastCandidateFound;
    private int pendingTasks;
    private int completedTasks;

    public AgentProgressData(String agentName) {
        this.agentName = agentName;
        this.candidatesFound = 0;
        this.lastCandidateFound = "";
        this.pendingTasks = 0;
        this.completedTasks = 0;
    }

    public void setCandidatesFound(Integer candidatesFound) {
        this.candidatesFound = candidatesFound;
    }

    public void setLastCandidateFound(String candidate) {
        this.lastCandidateFound = candidate;
        this.candidatesFound++;
    }

    public void setPendingTasks(int count) {
        this.completedTasks += this.pendingTasks;
        this.pendingTasks = count;
    }

    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }




    public StringProperty LastCandidateProperty(){
        return new SimpleStringProperty(lastCandidateFound);
    }

    public ObservableValue<Integer> CandidateFoundProperty() {
        return new ReadOnlyObjectWrapper<>(this.candidatesFound);

    }

    public ObservableValue<Integer> PendingCountProperty() {
        return new ReadOnlyObjectWrapper<>(this.pendingTasks);
    }

    public ObservableValue<String> AgentNameProperty() {
        return new ReadOnlyObjectWrapper<>(this.agentName);

    }

    public ObservableValue<Integer> TotalCountProperty() {
        return new ReadOnlyObjectWrapper<>(this.completedTasks);
    }


    public int getPendingTasks() {
        return this.pendingTasks;
    }

    public int getCompletedTasks(){
        return this.completedTasks;
    }
}
