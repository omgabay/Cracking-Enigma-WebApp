package servlets.allies;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import users.AllyTeam;
import users.UBoat;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static utils.Constants.GSON_INSTANCE;

public class GetContestTeamsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
        String uboatName = request.getParameter(Constants.UBOAT);
        if(uboatName == null){
            uboatName = SessionUtils.getUsername(request);
        }


        UserManager users = ServletUtils.getUserManager(getServletContext());
        UBoat uboat = users.getUboat(uboatName);

        if(uboat == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain");
            response.getWriter().print("Cannot fetch competing teams.. UBoat " + uboatName + " was not found." );
            return;
        }


        List<AllyTeam> teams = uboat.getAllyTeams();
        String jsonResponse = GSON_INSTANCE.toJson(teams);
        try(PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }

        response.setStatus(HttpServletResponse.SC_OK);


    }



//    @Override
//    protected  void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
//        String teamName = request.getParameter(Constants.TEAM_NAME_PARAM);
//        if(teamName == null || teamName.isEmpty()){
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        TeamManager teamManager = ServletUtils.getTeamManager(getServletContext());
//        List<AgentData> agentList = teamManager.getTeamMembers(teamName);
//
//        if(agentList == null){
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            return;
//        }
//        response.setContentType("application/json");
//        String jsonResponse = GSON_INSTANCE.toJson(agentList);
//        try(PrintWriter out = response.getWriter()) {
//            out.print(jsonResponse);
//            out.flush();
//        }
//
//    }

}
