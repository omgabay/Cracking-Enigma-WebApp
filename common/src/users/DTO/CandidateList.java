package users.DTO;

import bruteforce.AgentSolutionEntry;

import java.util.List;

public class CandidateList {
    String agentName;
    String teamName;
    List<AgentSolutionEntry> agentSolutions;

    AgentSolutionEntry contestWinner;

    public CandidateList(String agentName, String teamName, List<AgentSolutionEntry> agentSolutions) {
        this.agentName = agentName;
        this.teamName = teamName;
        this.agentSolutions = agentSolutions;
        this.contestWinner = null;
    }

    public AgentSolutionEntry getContestWinner() {
        return contestWinner;
    }

    public void setContestWinner(AgentSolutionEntry contestWinner) {
        this.contestWinner = contestWinner;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getTeamName() {
        return teamName;
    }

    public List<AgentSolutionEntry> getAgentSolutions() {
        return agentSolutions;
    }



}
