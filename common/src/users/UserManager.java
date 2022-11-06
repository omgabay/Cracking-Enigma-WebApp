package users;

import java.util.*;

public class UserManager {
    private final Map<String, User> mapNameToUser;

    private final List<UBoat> uboats;

    private final List<String>  allyTeams;

    private final Map<String, UBoat> mapTeamToUBoat;


    public UserManager() {
        mapNameToUser = new HashMap<>();
        mapTeamToUBoat = new HashMap<>();
        uboats = new ArrayList<>();
        allyTeams = new ArrayList<>();
    }

    public synchronized void addUser(String username, User user) {
        mapNameToUser.put(username, user);
        switch(user.type){
            case AGENT:
                AgentEntry agent = (AgentEntry) user;
                String teamName = agent.getTeamName();
                if(mapNameToUser.containsKey(teamName)){
                    if(mapNameToUser.get(teamName) instanceof AllyTeam){
                        AllyTeam team = (AllyTeam) mapNameToUser.get(teamName);
                        team.addAgentToTeam(agent);
                        System.out.println(agent.username + " was added to team " + teamName);
                    }
                }
                break;
            case ALLY:
                System.out.println("welcome team " + username +"!");
                allyTeams.add(username);
                break;
            case UBOAT:
                System.out.println("welcome uboat " + username +"!");
                uboats.add((UBoat)user);
        }
    }

    public synchronized void removeUser(String username) {
        mapNameToUser.remove(username);
    }

    public synchronized List<User> getUsers() {
        return new ArrayList<>(mapNameToUser.values());
    }

    public boolean isUserExists(String username) {
        return mapNameToUser.containsKey(username);
    }

    public User getUser(String name) {
        if(name == null){
            return null;
        }
        return mapNameToUser.get(name);
    }


    public UBoat getUboat(String name){
        if(name == null){
            return null;
        }
        User uboat = mapNameToUser.get(name);
        if(uboat instanceof UBoat){
            return (UBoat) uboat;
        }
        return null;
    }


    public AllyTeam getTeam(String teamName) {
        if(teamName == null || teamName.isEmpty()){
            return null;
        }
        User user = this.mapNameToUser.get(teamName);
        if(user instanceof AllyTeam){
            return (AllyTeam) user;
        }
        return null;
    }

    public AgentEntry getAgent(String name) {
        if(name == null){
            return null;
        }
        User agent = mapNameToUser.get(name);
        if(agent instanceof AgentEntry){
            return (AgentEntry) agent;
        }
        return null;


    }

    public List<UBoat> getAllUBoats() {
        return this.uboats;
    }

    public List<String> getAllTeams() {
        return this.allyTeams;
    }


    public UBoat getUboatByTeamName(String teamName) {

        if(this.mapTeamToUBoat.containsKey(teamName)){
            return this.mapTeamToUBoat.get(teamName);
        }
        return null;

    }

    public UBoat joinContest(String uboatName, String teamName) {
        UBoat contest = getUboat(uboatName);
        AllyTeam team = getTeam(teamName);
        if(contest == null || team == null){
            return null;
        }
        mapTeamToUBoat.put(teamName,contest);
        return contest;
    }

}
