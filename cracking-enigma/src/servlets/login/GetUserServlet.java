package servlets.login;

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

public class GetUserServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter(Constants.USERNAME);
        String clientType = request.getParameter(Constants.CLIENT_TYPE);

        if(username == null){
            username = SessionUtils.getUsername(request);
        }




        UserManager users = ServletUtils.getUserManager(getServletContext());
        User user = users.getUser(username);

        if(user == null){
            response.setContentType("text/plain");
            try(PrintWriter out = response.getWriter()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("User was not found in database");
                out.flush();
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
//        if(clientType != null){
//            switch (clientType){
//                case Constants.ALLY:
//                    if( !(user instanceof AllyTeam)){
//                        printMessage(response,"User was found but it is not of type ally");
//                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                        return;
//                    }
//                case Constants.UBOAT:
//                    if( !(user instanceof UBoat)){
//                        printMessage(response,"User was found but it is not of type uboat");
//                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                        return;
//                    }
//
//                case Constants.AGENT_PARAM:
//                    if( !(user instanceof AgentEntry)){
//                        printMessage(response,"User was found but it is not of type agent");
//                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                        return;
//                }
//            }
//        }
        response.setContentType("application/json");
        String json = writeToJson(user,clientType);

        try(PrintWriter out = response.getWriter()){
            out.print(json);
            out.flush();
        }
        response.setStatus(HttpServletResponse.SC_OK);


    }

    private void printMessage(HttpServletResponse response, String message) {
        response.setContentType("text/plain");
        try(PrintWriter out = response.getWriter()){
            out.print(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String writeToJson(User user, String type){
        String json = null;
        if(user instanceof AllyTeam){
            AllyTeam ally = (AllyTeam) user;
            json = GSON_INSTANCE.toJson(ally);
        } else if (user instanceof  UBoat) {
            UBoat uboat = (UBoat) user;
            if(type!= null && type.equals(Constants.BATTLE_TEAMS)){
                json = GSON_INSTANCE.toJson(uboat.getAllyTeams());
            }else{
                json = GSON_INSTANCE.toJson(uboat);
            }
        } else if (user instanceof  AgentEntry) {
            AgentEntry agent = (AgentEntry) user;
            json = GSON_INSTANCE.toJson(agent);
        }
      return json;
    }


}
