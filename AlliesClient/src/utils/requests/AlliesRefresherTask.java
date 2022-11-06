package utils.requests;

import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AgentEntry;
import users.AllyTeam;
import users.UBoat;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Configuration.GSON_INSTANCE;

public class AlliesRefresherTask extends TimerTask {
    private Consumer<List<UBoat>> uboatConsumer;
    private Consumer<List<AgentEntry>> updateTable;

    private String clientName;

    public AlliesRefresherTask(String name, Consumer<List<UBoat>> uboatConsumer, Consumer<List<AgentEntry>> updateAgentsTable){
        this.clientName = name;
        this.uboatConsumer = uboatConsumer;
        this.updateTable = updateAgentsTable;
    }


    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.UBOAT_LIST_RESOURCE)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    List<UBoat> uboats = GSON_INSTANCE.fromJson(rawBody,new TypeToken<List<UBoat>>(){}.getType());
                    uboatConsumer.accept(uboats);
                }
            }
        });


        finalUrl = HttpUrl
                .parse(Constants.GET_USER_RESOURCE_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, clientName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    System.out.println(rawBody);
                    AllyTeam myTeam = GSON_INSTANCE.fromJson(rawBody, AllyTeam.class);
                    updateTable.accept(myTeam.getAgentList());
                }
            }
        });

    }
}
