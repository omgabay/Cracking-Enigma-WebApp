package servlets.uboat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jaxb.generated.CTEEnigma;
import machine.Engine;
import users.UBoat;
import users.User;
import users.UserManager;
import utils.Constants;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;
import java.io.*;


@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileUploadServlet extends HttpServlet {


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String uboatName = SessionUtils.getUsername(request);
        UserManager users = ServletUtils.getUserManager(getServletContext());
        User user = users.getUser(uboatName);

        if(!(user instanceof UBoat)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        PrintWriter out = response.getWriter();
        Part part = request.getParts().iterator().next();

        InputStream is = new DataInputStream(part.getInputStream());
        Engine engine = new Engine();
        try {
            CTEEnigma cteEnigma  = engine.loadMachineFromInputStream(is);
            UBoat uboat = (UBoat) user;
            uboat.setMachine(cteEnigma);
            String battleName = cteEnigma.getCTEBattlefield().getBattleName();




            String json = Constants.GSON_INSTANCE.toJson(cteEnigma);
            out.print(json);
            response.setStatus(HttpServletResponse.SC_OK);
            saveEnigmaMachine((UBoat) user,cteEnigma);


        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");
            response.getOutputStream().println(e.getMessage());
        }






    }

    private void saveEnigmaMachine(UBoat uboat, CTEEnigma cteEnigma) {
        uboat.setMachine(cteEnigma);
    }



}





