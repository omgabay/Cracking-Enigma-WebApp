package agentProgressTable;

import javafx.application.Platform;
import users.DTO.AgentProgressData;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;

public class ProgressTableRefresher extends TimerTask {
    String agentName;
    String uboatName;
    Consumer<List<AgentProgressData>> updateTable;

    public ProgressTableRefresher(String uboat, String agent, Consumer<List<AgentProgressData>> updateTableConsumer){
        this.agentName = agent;
        this.uboatName = uboat;
        updateTable = updateTableConsumer;
    }
    @Override
    public void run() {
        sendRequestForProgressData();
    }

    private void sendRequestForProgressData() {
        String finalUrl = HttpUrl
                .parse(Constants.GET_AGENT_PROGRESS_URL)
                .newBuilder()
                .addQueryParameter(Constants.ALLY, uboatName)
                .addQueryParameter(Constants.AGENT_PARAM, agentName)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.err.println("Something went wrong with agent-progress request");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String json = response.body().string();
                    Type listType = new TypeToken<List<AgentProgressData>>(){}.getType();
                    List<AgentProgressData> agentsProgress = GSON_INSTANCE.fromJson(json ,listType);
                    Platform.runLater(()->{
                        updateTable.accept(agentsProgress);
                    });

                }else{
                    System.out.println(response.body().string());
                }
                response.close();
            }
        });

    }
}
