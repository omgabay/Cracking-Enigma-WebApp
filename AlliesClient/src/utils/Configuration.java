package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Configuration {

    // FXML Paths

    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/fxml/Allies.fxml";


    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/cracking-enigma";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;


    public final static String PROGRESS_TABLE_FXML = "/fxml/progressTable.fxml";
    public final static String TEAMS_TABLE_FXML = "/fxml/teamsTable.fxml";





    public final static String UPLOAD_FILE_URL = FULL_SERVER_PATH + "/upload-enigma-file";

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";






    // GSON instance

    public final static Gson GSON_INSTANCE = new GsonBuilder().setPrettyPrinting().create();
    //public final static Gson GSON_INSTANCE = new Gson();






 }
