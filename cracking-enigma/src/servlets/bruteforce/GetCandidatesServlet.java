package servlets.bruteforce;

import bruteforce.AgentSolutionEntry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UBoat;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class GetCandidatesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {



        String uboatName = request.getParameter(Constants.UBOAT);
        int solutionsVersion = ServletUtils.getIntParameter(request,Constants.CANDIDATES_VERSION);
        if(solutionsVersion == Constants.INT_PARAMETER_ERROR){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Invalid version for candidate list request");
            return;
        }


        UserManager users = ServletUtils.getUserManager(getServletContext());
        UBoat uboat = users.getUboat(uboatName);
        if(uboat == null){
            response.setContentType("text/plain");
            response.getWriter().print("uboat was not found - GetCandidatesServlet");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        List<AgentSolutionEntry> solutions = uboat.getSolutionsWithVersion(solutionsVersion);
        if(solutions.size() > 0){
            String usernameFromSession = SessionUtils.getUsername(request);
            System.out.println("request for candidates from uboat " + uboat.username + " version=" + solutionsVersion);
            System.out.println("Sending candidates for " + usernameFromSession + "...");
        }

        String jsonResponse = Constants.GSON_INSTANCE.toJson(solutions);
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }






    private Cookie getVersionCookie(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("solutionVersion")){
                return cookie;
            }
        }
        return new Cookie("solutionVersion", "0");
    }
}
