package servlets.allies;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.AllyTeam;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

import static utils.Constants.GSON_INSTANCE;

public class GetTeamServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String usernameFromParameter = request.getParameter(Constants.USERNAME);

        if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UserManager users = ServletUtils.getUserManager(getServletContext());

        AllyTeam myTeam = users.getTeam(usernameFromParameter);

        if(myTeam == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String jsonResponse = GSON_INSTANCE.toJson(myTeam);
        try(PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }

        response.setStatus(HttpServletResponse.SC_OK);


    }



}
