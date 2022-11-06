package users;

import users.DTO.AgentProgressData;
import bruteforce.AgentSolutionEntry;
import bruteforce.AgentTask;
import bruteforce.Decryption;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import machine.IEngine;
import bruteforce.Difficulty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllyTeam extends User{

    List<AgentEntry> agentList;
    GameStatus status;

    Decryption DM = null;
    boolean isReady;
    int missionSize;

    String uboatName;

    Map<String, AgentEntry> mapNameToAgent;



    public AllyTeam(String name){
        super(name,ClientType.ALLY);
        agentList = new ArrayList<>();
        status = GameStatus.WAITING_FOR_TEAMS;
        isReady = false;
        missionSize = 900;
        uboatName = null;
        mapNameToAgent = new HashMap<>();
    }




    public List<AgentEntry> getAgentList(){
        return this.agentList;
    }



    public void addAgentToTeam(AgentEntry agent) {
        this.agentList.add(agent);
        this.mapNameToAgent.put(agent.getName(), agent);
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public ObservableValue<Integer> AgentCountProperty() {
        int size = this.agentList.size();
        return new SimpleIntegerProperty(size).asObject();
    }


    public SimpleStringProperty readyStatusProperty(){
        if(this.isReady){
            return  new SimpleStringProperty("TEAM IS READY");
        }
        return new SimpleStringProperty("NOT READY");
    }

    public void setMissionSize(int size) {
        this.missionSize = size;
    }


    public void createTeamDM(IEngine engine,String secretMessage, Difficulty difficulty ){
        this.DM = new Decryption(engine, secretMessage, difficulty, missionSize);
        this.status = GameStatus.CONTEST_RUNNING;
    }



    public ObservableValue<Integer> missionSizeProperty() {
        return new SimpleIntegerProperty(this.missionSize).asObject();
    }

    public List<AgentTask> getNewTasks(String agentName , long taskCount) {

        List<AgentTask> tasks = new ArrayList<>();
        if(isReady && !DM.isDone()){
            tasks.addAll(DM.fetchAgentTasks((int)taskCount));
        }
        AgentEntry myAgent = mapNameToAgent.get(agentName);
        myAgent.updateTaskInQueueCount(tasks.size());
        return tasks;
    }

    public AgentEntry getAgentInTeam(String agentName){
        return this.mapNameToAgent.get(agentName);
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("team name: ").append(this.username).append('\n');
        System.out.println("team name: " + this.username);
        for (AgentEntry agent   : this.agentList ) {
            sb.append(agent.username).append(" ");
        }
        sb.append('\n');
        return sb.toString();
    }

    public String getBattle() {
        return this.uboatName;
    }

    public void setBattle(String name) {
        this.uboatName = name;
    }

    public void addCandidateToAgentProgress(AgentSolutionEntry candidate) {
        AgentEntry myAgent = this.mapNameToAgent.get(candidate.getAgentName());
        if(myAgent != null){
            myAgent.updateProgressWithCandidate(candidate.getCandidate());
        }

    }

    public List<AgentProgressData> getAgentsProgress() {
        List<AgentProgressData> progressLst = new ArrayList<>();
        for (AgentEntry agentEntry :  this.agentList) {
            progressLst.add(agentEntry.getProgressData());
        }
        return progressLst;
    }

    public int getTaskSize() {
        return this.missionSize;
    }
}
