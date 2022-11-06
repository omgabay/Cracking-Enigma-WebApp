package servlets.uboat;

import users.DTO.MachineSetupData;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UBoat;
import users.UserManager;
import utils.servlet.ServletUtils;
import utils.servlet.SessionUtils;
import java.io.IOException;


import static utils.Constants.*;

public class UpdateMachineSetupServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String setupJson = request.getParameter(ENIGMA_SETUP);
        if(setupJson == null || setupJson.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("Update Setup Servlet - parameter is missing!");
            return;
        }
        System.out.println(setupJson);
        MachineSetupData setupData = GSON_INSTANCE.fromJson(setupJson, MachineSetupData.class);


        String uboatName = SessionUtils.getUsername(request);
        UserManager users = ServletUtils.getUserManager(getServletContext());
        UBoat uboat = users.getUboat(uboatName);
        if(uboat == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.out.println("Update Setup Servlet - uboat not found!");
            return;
        }
        uboat.updateEnigmaSetup(setupData);
        response.setStatus(HttpServletResponse.SC_OK);
    }



    }
