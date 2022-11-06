package bruteforce;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import java.util.List;
import java.util.Timer;

public class AgentSolutionEntry {

    private final String machineCode;
    private final String agentName;
    private final String teamName;
    private final String candidate;

    private Timer timer;




    public AgentSolutionEntry(String agentName,String teamName,  String candidate, List<Integer> rotors, List<Character> positions, String reflector){
        this.candidate = candidate;
        this.agentName = agentName;
        this.teamName = teamName;
        this.machineCode = buildMachineCodeString(rotors,positions,reflector);
    }

    public AgentSolutionEntry(String agentName,String teamName,  String candidate, String machineCode){
        this.agentName = agentName;
        this.teamName = teamName;
        this.candidate = candidate;
        this.machineCode = machineCode;
    }


    private String buildMachineCodeString(List<Integer> rotors,List<Character> positions, String reflector){
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < rotors.size(); i++) {
            if(i == 0){
                sb.append("<").append(rotors.get(i));
            }else{
                sb.append(",").append(rotors.get(i));
            }
        }
        sb.append(">");
        for (int i = 0; i < positions.size(); i++) {
            if(i == 0){
                sb.append("<").append(positions.get(i));
            }else{
                sb.append(",").append(positions.get(i));
            }
        }
        sb.append(">");
        sb.append("<").append(reflector).append(">");

        return sb.toString();

    }

    public String getAgentName(){
        return this.agentName;
    }

    public ObservableValue<String> AgentNameProperty(){return new SimpleStringProperty(this.agentName); }

    public ObservableValue<String> TeamNameProperty(){return new SimpleStringProperty(this.teamName);}

    public ObservableValue<String> CandidateProperty(){return  new SimpleStringProperty(this.candidate);}

    public ObservableValue<String> MachineCodeProperty(){return new SimpleStringProperty(this.machineCode);}


    public String getCandidate(){
        return candidate;
    }


    public String getTeamName(){return this.teamName;}



    public String toString(){
        StringBuilder sb = new StringBuilder("");
        sb.append(this.agentName).append(" from team ").append(this.teamName).append(" found candidate - ").append(this.candidate);
        return sb.toString();
    }

}
