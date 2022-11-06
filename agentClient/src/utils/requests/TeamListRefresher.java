package utils.requests;

import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.AllyTeam;
import users.User;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Configuration.GSON_INSTANCE;

public class TeamListRefresher extends TimerTask {

    Consumer<List<String>> teamsViewConsumer;

    public TeamListRefresher(Consumer<List<String>> teamsListConsumer){
        teamsViewConsumer = teamsListConsumer;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.USERS_LIST)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Something went wrong with request for the list of teams");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    List<User> users = GSON_INSTANCE.fromJson(rawBody, new TypeToken<List<User>>(){}.getType());
                    List<String>  teamNames = new ArrayList<>();
                    for (User user   :      users              ) {
                        if(user instanceof AllyTeam){
                            teamNames.add(user.getName());
                        }
                    }
                    teamsViewConsumer.accept(teamNames);
                }
            }
        });

    }
}
