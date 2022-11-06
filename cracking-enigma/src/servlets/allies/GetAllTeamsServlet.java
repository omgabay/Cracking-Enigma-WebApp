package servlets.allies;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;
import utils.servlet.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static utils.Constants.GSON_INSTANCE;

public class GetAllTeamsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        UserManager users = ServletUtils.getUserManager(getServletContext());
        List<String> teams = users.getAllTeams();

        String jsonResponse = GSON_INSTANCE.toJson(teams);
        try(PrintWriter out = response.getWriter()){
            out.print(jsonResponse);
        }

        response.setStatus(HttpServletResponse.SC_OK);




    }


}
