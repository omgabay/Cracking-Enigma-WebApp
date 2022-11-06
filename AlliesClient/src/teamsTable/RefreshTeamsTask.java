package teamsTable;

import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AllyTeam;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;


public class RefreshTeamsTask extends TimerTask {
    String uboatName;
    Consumer<List<AllyTeam>> updateTable;

    public RefreshTeamsTask(String uboatName, Consumer<List<AllyTeam>> updateTeamsTable) {
        this.uboatName = uboatName;
        this.updateTable = updateTeamsTable;

    }

    @Override
    public void run() {
        getUBoatRequest();
    }

    public void getUBoatRequest() {
        String finalUrl = HttpUrl
                .parse(Constants.GET_USER_RESOURCE_PAGE)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, uboatName)
                .addQueryParameter(Constants.REQUEST_TYPE, Constants.BATTLE_TEAMS)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String rawBody = response.body().string();
                    Type listType = new TypeToken<List<AllyTeam>>() {
                    }.getType();
                    List<AllyTeam> allies = GSON_INSTANCE.fromJson(rawBody, listType);
                    if (allies != null && !allies.isEmpty()) {
                        Platform.runLater(() ->
                                updateTable.accept(allies)
                        );
                    }
                }
            }
        });
    }
}





