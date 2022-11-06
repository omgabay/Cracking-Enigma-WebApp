package agentTableWithWorkers;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;

public class updateAgentTableTask extends TimerTask {
    private String uboatName;

    @Override
    public void run() {

    }

    public void requestForCandidates(){
        String finalUrl = HttpUrl
                .parse(Constants.GET_CANDIDATES_SOLUTIONS)
                .newBuilder()
                .addQueryParameter(Constants.UBOAT,uboatName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    }
                }

        );


    }



}
