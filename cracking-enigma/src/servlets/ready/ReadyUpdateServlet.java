package servlets.ready;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.*;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

import static utils.Constants.GSON_INSTANCE;

public class ReadyUpdateServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String username = SessionUtils.getUsername(request);
        if(username == null){
            username = request.getParameter(Constants.USERNAME);
        }

        UserManager users = ServletUtils.getUserManager(getServletContext());
        User user = users.getUser(username);

        if(user instanceof UBoat){
            UBoat uboat = (UBoat) user;
            String secret = request.getParameter(Constants.SECRET_PARAM_NAME);
            System.out.println("Creating Contest crack the code:" + secret);
            uboat.setSecretMessage(secret);
            uboat.setReady();
        }
        else if (user instanceof AllyTeam) {
            AllyTeam ally = (AllyTeam) user;
            ally.setReady(true);
            int taskSize = ServletUtils.getIntParameter(request,Constants.ALLY_TASK_SIZE);
            if(taskSize != Constants.INT_PARAMETER_ERROR){
                ally.setMissionSize(taskSize);
            }
        }else if (user == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("User was not found - Ready Servlet");
            return;
        }


        try(PrintWriter out = response.getWriter()){
            String jsonResponse = GSON_INSTANCE.toJson(user);
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResponse);
            out.flush();
        }

    }



}
