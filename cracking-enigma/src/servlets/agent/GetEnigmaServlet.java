package servlets.agent;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UBoat;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

public class GetEnigmaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String teamName = request.getParameter(Constants.ALLY);
        UserManager users = ServletUtils.getUserManager(getServletContext());
        UBoat uboat = users.getUboatByTeamName(teamName);


        PrintWriter out = response.getWriter();
        if(uboat == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            out.print("team " + teamName +"did not register to contest");
            return;
        }
        if(uboat.getMachine() == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            out.print("UBoat " +uboat.username + "did not load engima file");
            return;
        }

        response.setContentType("application/json");
        String json =  Constants.GSON_INSTANCE.toJson(uboat);
        out.print(json);
        response.setStatus(HttpServletResponse.SC_OK);
    }


}
