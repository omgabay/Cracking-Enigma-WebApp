package servlets.uboat;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UBoat;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class GetUBoatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        UserManager users = ServletUtils.getUserManager(getServletContext());
        List<UBoat> uboatList = users.getAllUBoats();

        String jsonResponse = Constants.GSON_INSTANCE.toJson(uboatList);
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
        response.setStatus(HttpServletResponse.SC_OK);

    }
}
