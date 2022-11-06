package users;
import users.DTO.MachineSetupData;
import bruteforce.AgentSolutionEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import jaxb.generated.CTEEnigma;
import machine.Engine;
import bruteforce.Difficulty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UBoat extends User{


    // Battle Related Fields:
    private String battleName;
    private int maximumTeams;

    private bruteforce.Difficulty difficulty;

    private final List<AgentSolutionEntry> contestCandidateSolutions;


    private CTEEnigma machine;
    private List<AllyTeam> teams;

    // Contest related fields
    private String secretMessage;
    private AgentSolutionEntry winner;

    private boolean isReady = false;

    private transient Engine engine;
    private Map<String, AllyTeam> mapNameToTeam;


    public UBoat(String name){
        super(name, ClientType.UBOAT);
        machine = null;
        engine = null;
        // Create an empty list of allies teams
        teams = new ArrayList<>();
        contestCandidateSolutions = new ArrayList<>();
        secretMessage = null;
        difficulty = Difficulty.LOAD_ENIGMA;
        this.maximumTeams = 0;
        mapNameToTeam = new HashMap<>();
        this.engine = new Engine();
    }

    public boolean addTeam(AllyTeam ally){
        if(ally != null && teams.size() < maximumTeams && this.machine != null){
            teams.add(ally);
            return true;
        }
        return false;
    }




    public void setMachine(CTEEnigma enigma){
        this.machine = enigma;
        this.battleName = enigma.getCTEBattlefield().getBattleName();
        this.maximumTeams = enigma.getCTEBattlefield().getAllies();
        this.winner = null;
        // Update difficulty level of Brute Force Task
        switch(enigma.getCTEBattlefield().getLevel()){
            case "Easy": default:
                difficulty = Difficulty.EASY;
                break;
            case "Medium":
                difficulty = Difficulty.MEDIUM;
                break;
            case "Hard":
                difficulty = Difficulty.HARD;
                break;
            case "Insane":
                difficulty = Difficulty.INSANE;
                break;
        }
    }

    public CTEEnigma getMachine(){
        return machine;
    }




    public int getAllies() {
        return this.machine.getCTEBattlefield().getAllies();
    }

    public void setSecretMessage(String secret) {
        this.secretMessage = secret;
    }




    private void createDMs() {
        if(engine == null){
            return;
        }
        for (AllyTeam team : this.teams)
         {
            team.createTeamDM(engine,this.secretMessage,this.difficulty);
        }

    }


    public List<AgentSolutionEntry> getSolutionsWithVersion(int version){
        List<AgentSolutionEntry> response = new ArrayList<>();

        if(version < 0){ version = 0;}
        for(int i=version; i<this.contestCandidateSolutions.size(); i++){
            response.add(contestCandidateSolutions.get(i));
        }
        return response;
    }





    public void addAgentSolution(AgentSolutionEntry agentSolution){
            this.contestCandidateSolutions.add(agentSolution);
    }





    public List<AllyTeam> getAllyTeams() {
        return this.teams;
    }


    public ObservableValue<String> BattleNameProperty() {
        return new SimpleStringProperty(this.battleName);
    }

    public ObservableValue<String> DifficultyProperty() {
        return new SimpleStringProperty(this.difficulty.name());
    }

    public ObservableValue<String> TeamsRegisteredProperty() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.teams.size()).append("/").append(this.maximumTeams).append(" registered");
        return new SimpleStringProperty(sb.toString());
    }

    public String getSecretMessage() {
        return this.secretMessage;
    }
    public boolean isReady(){
        return isReady;
    }

    public boolean setReady(){
        if(isReady){
            return false;
        }
        for (AllyTeam myTeam : this.teams) {
            if(!myTeam.isReady){
                return false;
            }
        }
        this.isReady = true;
        createDMs();
        startAgents();
        return true;
    }

    private void startAgents() {
        for (AllyTeam team : this.teams) {
            for (AgentEntry agent  : team.agentList) {
                agent.setReady(true);
                agent.setSecretMessage(this.secretMessage);
            }
        }
    }





    public void logout(){
        super.logout();
        this.teams = new ArrayList<>();
        this.contestCandidateSolutions.clear();
        this.winner = null;
        this.secretMessage = null;
        this.isReady = false;
        this.mapNameToTeam = new HashMap<>();

    }


    public String getDifficulty() {
        switch(this.difficulty){
            case EASY:
                return "Easy";
            case MEDIUM:
                return "Medium";
            case HARD:
                return "Hard";
            case INSANE:
                return "Insane";
        }
        return "";
    }

    public void addAllCandidates(List<AgentSolutionEntry> agentSolutions) {
        if (agentSolutions == null) {
            return;
        }

        this.contestCandidateSolutions.addAll(agentSolutions);
        for (AgentSolutionEntry candidate : agentSolutions) {
            synchronized(this) {
                if (candidate.getCandidate().equals(this.secretMessage)) {
                    this.winner = candidate;
                    System.out.println("The Winner is " + candidate);
                }
            }
            String teamName = candidate.getTeamName();
            AllyTeam allyTeam =  this.mapNameToTeam.get(teamName);
            if(allyTeam != null){
                allyTeam.addCandidateToAgentProgress(candidate);
            }
        }

    }


    public void updateEnigmaSetup(MachineSetupData setupData) {
        engine.loadCTEnigma(this.machine);
        engine.setupMachine(setupData.getRotorIDs(),setupData.getRotorPositions(), setupData.getReflector(),new HashMap<>());
    }


    public AgentSolutionEntry getWinner(){
        return this.winner;
    }
}
