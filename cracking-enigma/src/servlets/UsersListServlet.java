package servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;

import users.User;
import users.UserManager;

import utils.servlet.ServletUtils;



public class UsersListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        try(PrintWriter out = response.getWriter()){
            Gson gson = new Gson();
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            List<User> usersList = userManager.getUsers();
            if (usersList.isEmpty()){
                response.setContentType("text/plain;charset=UTF-8");
                out.print("No users have registered");
                return;
            }
            response.setContentType("application/json");
            String json = gson.toJson(usersList.toArray());
            out.println(json);


        }
    }


}
