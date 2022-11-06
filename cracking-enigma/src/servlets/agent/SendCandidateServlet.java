package servlets.agent;

import users.DTO.CandidateList;
import bruteforce.AgentSolutionEntry;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.AllyTeam;
import users.UBoat;
import users.UserManager;
import utils.servlet.ServletUtils;

import java.io.*;

import static utils.Constants.*;

public class SendCandidateServlet extends HttpServlet {



    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String agentName = request.getParameter(AGENT_PARAM);
        String teamName = request.getParameter(ALLY);
        String candidate = request.getParameter(CANDIDATES);
        String machineCode = request.getParameter(MACHINECODE_PARAM);

        // Getting User Manager
        UserManager users = ServletUtils.getUserManager(getServletContext());


        if (agentName == null || teamName == null || candidate == null || machineCode == null) {
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("could not add candidate, one parameter is missing");
        } else {
            AgentSolutionEntry ase = new AgentSolutionEntry(agentName,teamName,candidate,machineCode);
            AllyTeam myTeam = users.getTeam(teamName);
            if(myTeam == null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain");
                response.getWriter().print("Ally team " + teamName + " was not found -- addCandidateServlet");
            }else{
                UBoat uboat = users.getUboatByTeamName(teamName);
                if(uboat != null){
                    uboat.addAgentSolution(ase);
                    response.setContentType("application/json");
                    String json = GSON_INSTANCE.toJson(uboat);
                    response.getWriter().print(json);
                }


                response.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }





    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String jsonCandidates = request.getParameter(CANDIDATES);

        CandidateList candidates = GSON_INSTANCE.fromJson(jsonCandidates, CandidateList.class);

        if(candidates == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String teamName = candidates.getTeamName();

        UserManager users = ServletUtils.getUserManager(getServletContext());
        UBoat uboat = users.getUboatByTeamName(teamName);
        System.out.println("Received candidates from agent " + candidates.getAgentName());
        System.out.println("Candidates to Uboat named " + uboat.username + " from team " + teamName);



        if(uboat != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            System.out.println("Got candidates from team " + teamName);
            synchronized (this) {
                uboat.addAllCandidates(candidates.getAgentSolutions());
            }
        }else {
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Candidate List is missing");
        }
    }
}


//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String uboatName = request.getParameter(UBOAT);
//        String teamName = request.getParameter(TEAM_NAME_PARAM);
//        String agentName = request.getParameter(AGENT_PARAM);
//        String candidate = request.getParameter(CANDIDATES);
//        String machineCode = request.getParameter(MACHINECODE_PARAM);
//
//        UserManager users = ServletUtils.getUserManager(getServletContext());
//        UBoat uboat = users.getUboat(uboatName);
//
//        if(candidate == null || uboat == null){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//
//        AgentSolutionEntry candidateEntry = new AgentSolutionEntry(agentName,teamName,candidate,machineCode);
//        String jsonResponse = GSON_INSTANCE.toJson(candidateEntry);
//        response.setContentType("application/json");
//        uboat.addAgentSolution(candidateEntry);
//
//        response.getWriter().print(candidateEntry);
//        response.setStatus(HttpServletResponse.SC_OK);
//
//    }
//}

//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
//        response.setContentType("application/json");
//
//        // Getting candidate in JSON format
//        String candidate = request.getParameter(Constants.CANDIDATE_PARAM);
//        String agent = request.getParameter(Constants.AGENT_PARAM);
//        String team = request.getParameter(Constants.TEAM_NAME_PARAM);
//        String machineCode = request.getParameter(Constants.MACHINECODE_PARAM);
//
//
//        // Getting Uboat name
//        String uboatName = request.getParameter(Constants.UBOAT_PARAM);
//        UBoat uboat = ServletUtils.getUserManager(getServletContext()).getUboat(uboatName);
//
//        if(uboat == null || candidate== null || agent == null || machineCode == null){
//            response.setContentType("text/plain");
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        AgentSolutionEntry solutionEntry = new AgentSolutionEntry(agent,team,candidate,machineCode);
//
//        uboat.addAgentSolution(solutionEntry);
//
//        try(PrintWriter writer = response.getWriter()){
//            String jsonResponse = Constants.GSON_INSTANCE.toJson(solutionEntry);
//            writer.print(jsonResponse);
//            writer.flush();
//        }
//
//        response.setStatus(HttpServletResponse.SC_OK);
//
//
//
//
//    }

//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
//        System.out.println("In get");
//        String usernameFromSession = SessionUtils.getUsername(request);
//        if(usernameFromSession == null){
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        }
//
//        UserManager users = ServletUtils.getUserManager(getServletContext());
//        AgentEntry agent = users.getAgent(usernameFromSession);
//        if(agent == null){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        // Getting parameter from request
//        String candidate = request.getParameter(Constants.CANDIDATE_PARAM);
//        if(candidate != null && !candidate.isEmpty()){
//            agent.addNewCandidateSolution(candidate);
//        }
//
//        response.setStatus(HttpServletResponse.SC_OK);
//
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//       System.out.println("in post");
//        response.setContentType("application/json");
//
//       Part part = request.getParts().iterator().next();
//       if(part == null){
//           response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//           return;
//       }
//
//       InputStream is = part.getInputStream();
//       InputStreamReader isr = new InputStreamReader(is);
//        JsonReader jsonReader = new JsonReader(isr);
//       AgentSolutionEntry solutionEntry = Constants.GSON_INSTANCE.fromJson(jsonReader, AgentSolutionEntry.class);
//       Gson gson = new GsonBuilder().setPrettyPrinting().create();
//       String json = gson.toJson(solutionEntry);
//       System.out.println(json);
//       try(PrintWriter writer = response.getWriter()){
//           writer.print(json);
//           writer.flush();
//       }
//       response.setStatus(HttpServletResponse.SC_OK);
//
//    }
//}
