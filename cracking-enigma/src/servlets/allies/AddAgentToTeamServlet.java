package servlets.allies;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.AgentEntry;
import users.AllyTeam;
import users.User;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.IOException;

import static utils.Constants.*;
import static utils.Constants.AGENT_TASK_COUNT;

public class AddAgentToTeamServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String teamNameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        AllyTeam myTeam = userManager.getTeam(teamNameFromSession);

        if (myTeam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String agentName = request.getParameter(Constants.AGENT_PARAM);
        String workers = request.getParameter(AGENT_WORKER_COUNT);
        String taskCount = request.getParameter(AGENT_TASK_COUNT);

        if (agentName == null || workers == null || taskCount == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        User user = new AgentEntry(agentName, teamNameFromSession, Integer.parseInt(taskCount), Integer.parseInt(workers));
        synchronized(this){
            if(userManager.isUserExists(agentName)){
                String errorMessage = "Agent " + agentName + " already exists!";
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain");
                response.getWriter().print(errorMessage);
            }else{
                userManager.addUser(user.getName(), user);
                response.getWriter().print(GSON_INSTANCE.toJson(user));
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }


    }






}
