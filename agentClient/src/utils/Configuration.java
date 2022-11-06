package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Configuration {

    // FXML Paths

    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/fxml/Uboat.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/fxml/uboatLogin.fxml";

    public static final String AGENT_PAGE_FXML_RESOURCE_LOCATION = "/fxml/Agent.fxml";



    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";

    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/cracking-enigma";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;





    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public static final String TEAMS_LIST_RESOURCE = FULL_SERVER_PATH + "/teams";
    public final static String SUBMIT_CANDIDATE_SOLUTION = FULL_SERVER_PATH + "/candidate-solution";





    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();






 }
