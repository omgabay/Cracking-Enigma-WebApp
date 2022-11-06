package servlets.uboat;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.AgentEntry;
import users.UBoat;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.IOException;


/**
 * UBoat will declare the winner
 */
public class DeclareWinnerServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        String userFromSession = SessionUtils.getUsername(request);
        UserManager users = ServletUtils.getUserManager(getServletContext());
        UBoat uboat = users.getUboat(userFromSession);
        if(uboat == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String agentName = request.getParameter(Constants.AGENT_PARAM);
        // Get the winner agent in the contest
        AgentEntry theWinner = users.getAgent(agentName);

        if(theWinner == null){
            response.getWriter().print("winner agent was not found!");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        response.getWriter().print("The winner is " + theWinner.getName() + " from team " + theWinner.getTeamName());
        response.setStatus(HttpServletResponse.SC_OK);
    }



    }
