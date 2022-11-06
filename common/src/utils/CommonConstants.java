package utils;


import com.google.gson.Gson;

public class CommonConstants {


    public final static int REFRESH_RATE = 2000;



    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    public static final String ICON_RESOURCE = "/images/turing_icon.png";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "web_app_Web";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;


    public static final String TEAMS_LIST_RESOURCE = FULL_SERVER_PATH +  "/teams";

    public static final String UBOAT_LIST_RESOURCE = FULL_SERVER_PATH + "/uboats";




    // Parameters
    public static final String USERNAME = "username";






    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}