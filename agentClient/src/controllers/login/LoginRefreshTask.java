package controllers.login;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class LoginRefreshTask extends TimerTask {

    private final Consumer<List<String>> updateTeams;

    private int teamCounter;


    public LoginRefreshTask(Consumer<List<String>> updateTeamNames){

        this.updateTeams = updateTeamNames;
        teamCounter = 0;
    }

    @Override
    public void run() {
        requestForAllyTeams();
    }

    public void requestForAllyTeams(){
        String finalUrl = HttpUrl
                .parse(Constants.GET_LIST_OF_TEAMS_RESOURCE)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.err.print("requestForAllyTeams- AGENT CLIENT");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
               try {
                   if (response.isSuccessful()) {
                       String rawBody = response.body().string();
                       Type listType = new TypeToken<List<String>>() {
                       }.getType();
                       List<String> teamsResponse = Constants.GSON_INSTANCE.fromJson(rawBody, listType);
                       List<String> teams = new ArrayList<>();
                       for (int i = teamCounter; i < teamsResponse.size(); i++) {
                           teams.add(teamsResponse.get(i));
                       }
                       teamCounter += teams.size();
                       Platform.runLater(() -> {
                           if (teams.size() > 0) {
                               updateTeams.accept(teams);
                           }
                       });

                   }
               }catch(RuntimeException exception){
                   exception.printStackTrace();
               }
            }

        });
    }


}
