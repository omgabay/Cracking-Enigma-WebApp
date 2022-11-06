package controllers;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AllyTeam;
import users.UBoat;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;

public class AlliesClientRefreshTask extends TimerTask {

    Consumer<AllyTeam> updateMyAgentsTable;
    Consumer<List<UBoat>> updateContestTable;

    BooleanProperty contestStarted;

    String clientName;



    public AlliesClientRefreshTask(String client, Consumer<List<UBoat>> updateContestTable, Consumer<AllyTeam> updateMyAgents){
        this.clientName = client;
        updateMyAgentsTable = updateMyAgents;
        this.updateContestTable = updateContestTable;
        this.contestStarted = new SimpleBooleanProperty(false);
    }

    @Override
    public void run() {
        getUBoatListRequest(this.updateContestTable);
        getAllyRequest();

    }


    public void getAllyRequest(){
        String finalUrl = HttpUrl
                .parse(Constants.GET_USER_RESOURCE_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, clientName)
                .build()
                .toString();
        System.out.println("Allies refresher: request ally " + clientName);

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String jsonResponse = response.body().string();
                    AllyTeam ally = GSON_INSTANCE.fromJson(jsonResponse,AllyTeam.class);
                    Platform.runLater(()->{
                        updateMyAgentsTable.accept(ally);
                    });
                }
            }
        });
    }


    public static void getUBoatListRequest(Consumer<List<UBoat>> uboatListConsumer){
        String finalUrl = HttpUrl
                .parse(Constants.UBOAT_LIST_RESOURCE)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        System.out.println("Something went wrong with UBoat list request in Allies client");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            String rawBody = response.body().string();
                            Type listType = new TypeToken<List<UBoat>>(){}.getType();
                            List<UBoat> uBoats = GSON_INSTANCE.fromJson(rawBody,listType);
                            Platform.runLater(() ->
                                    uboatListConsumer.accept(uBoats)
                                    );
                        }
                    }
                }

        );
    }




}
