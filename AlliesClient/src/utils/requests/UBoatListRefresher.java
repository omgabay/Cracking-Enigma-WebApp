package utils.requests;

import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.UBoat;
import utils.CommonConstants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.CommonConstants.GSON_INSTANCE;

public class UBoatListRefresher extends TimerTask {
    private Consumer<List<UBoat>> uboatListConsumer;
    public UBoatListRefresher(Consumer<List<UBoat>> uboatListConsumer){

        this.uboatListConsumer = uboatListConsumer;
        UBoat uboat;

    }
    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(CommonConstants.UBOAT_LIST_RESOURCE)
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
                    List<UBoat> uboats = GSON_INSTANCE.fromJson(rawBody, new TypeToken<List<UBoat>>(){}.getType());
                    uboatListConsumer.accept(uboats);
                }
            }
        });

    }
}
