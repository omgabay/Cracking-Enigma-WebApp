package servlets.login;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UBoat;
import users.UserManager;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        System.out.println("Clearing session for " + username);
        userManager.removeUser(username);
        SessionUtils.clearSession(request);

        UBoat myUboat = userManager.getUboat(username);
        myUboat.logout();
        try (PrintWriter out = response.getWriter()) {
            response.setContentType("text/plain");
            out.print(username + " logged out of contest");
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

}
