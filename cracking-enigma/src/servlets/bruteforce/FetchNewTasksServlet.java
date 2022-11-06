package servlets.bruteforce;

import bruteforce.AgentTask;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.AllyTeam;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static utils.Constants.GSON_INSTANCE;

public class FetchNewTasksServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        int numTasks =   ServletUtils.getIntParameter(request, Constants.NUM_OF_TASKS);
        String agentName = SessionUtils.getUsername(request);
        String allyName = request.getParameter(Constants.ALLY);

        if(numTasks == Constants.INT_PARAMETER_ERROR){
            numTasks = 5;
        }


        UserManager users = ServletUtils.getUserManager(getServletContext());
        AllyTeam myTeam = users.getTeam(allyName);
        if(myTeam != null){
            List<AgentTask> tasks = myTeam.getNewTasks(agentName, numTasks);
            String jsonResponse = GSON_INSTANCE.toJson(tasks);
            response.setContentType("application/json");
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                response.setStatus(HttpServletResponse.SC_OK);
            }
            if(tasks.size() > 0) {
                System.out.println(agentName + " from team " + allyName + " received " + tasks.size() + " tasks");
            }
        }else{
            response.setContentType("text/plain");
            response.getWriter().println("Could not fetch tasks from ally " + allyName + " ally was not found");
        }
    }


}






