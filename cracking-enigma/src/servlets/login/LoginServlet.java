package servlets.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.*;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;
import jakarta.servlet.http.HttpServlet;
import java.io.IOException;
import static utils.Constants.*;



public class LoginServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        if (usernameFromSession == null) { //user is not logged in yet

            String usernameFromParameter = request.getParameter(Constants.USERNAME);
            String clientType = request.getParameter(CLIENT_TYPE);   // UBoat , Allies team or Agent
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                //no username in session and no username in parameter - not standard situation. it's a conflict

                // stands for conflict in server state
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            } else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();


                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";

                        // stands for unauthorized as there is already such user with this name
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("text/plain");
                        response.getWriter().print(errorMessage);
                    }
                    else {
                        request.getSession(true).setAttribute(USERNAME, usernameFromParameter);
                        User user = createUserObject(request, usernameFromParameter, clientType);
                        userManager.addUser(usernameFromParameter, user);


                        // Writing back to the client the name that was added
                        response.getWriter().print(Constants.GSON_INSTANCE.toJson(user));
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            }
        } else {
            //user is already logged in
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }

    private User createUserObject(HttpServletRequest request ,String username,  String type) {
        User user = null;
        switch(type) {
            case "uboat":
                user = new UBoat(username);
                break;
            case "ally":
                user = new AllyTeam(username);
                break;
            case "agent":
                String teamName = request.getParameter(ALLY);
                int workers = Integer.parseInt(request.getParameter(AGENT_WORKER_COUNT));
                int taskCount = Integer.parseInt(request.getParameter(AGENT_TASK_COUNT));
                user = new AgentEntry(username, teamName, taskCount, workers);
                System.out.println(user);
                request.getSession(false).setAttribute(ALLY,teamName);
                break;
        }
        return user;
    }
}
