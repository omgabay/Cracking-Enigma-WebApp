package servlets.agent;

import users.DTO.AgentProgressData;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.AgentEntry;
import users.AllyTeam;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.GSON_INSTANCE;

public class AgentProgressUpdateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserManager users = ServletUtils.getUserManager(getServletContext()); // getting User-Manager
        String allyName = request.getParameter(Constants.ALLY);
        AllyTeam myAlly = users.getTeam(allyName);
        List<AgentProgressData> agentProgressLst = new ArrayList<>();
        if(myAlly != null){
            agentProgressLst.addAll(myAlly.getAgentsProgress());
        }else{
            // Getting agent from parameter
            String agentName = request.getParameter(Constants.AGENT_PARAM);
            if(agentName == null){
                agentName = SessionUtils.getUsername(request);
            }

            AgentEntry myAgent = users.getAgent(agentName);

            if(myAgent == null){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain");
                response.getWriter().println("Could not get agent progress data - agent " + agentName + " was not found");
                return;
            }else {
                agentProgressLst.add(myAgent.getProgressData());
            }
        }

            response.setContentType("application/json");
            String json = GSON_INSTANCE.toJson(agentProgressLst);
            try(PrintWriter out = response.getWriter()){
                out.print(json);
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }


