package candidateTable;

import bruteforce.AgentSolutionEntry;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
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

public class RefreshCandidateTableTask extends TimerTask {
    private final String uboatName;
    Consumer<List<AgentSolutionEntry>> solutionConsumer;

    private final IntegerProperty version;

    public RefreshCandidateTableTask(String clientName , Consumer<List<AgentSolutionEntry>> agentSolutionConsumer, IntegerProperty version) {
        this.solutionConsumer = agentSolutionConsumer;
        this.uboatName = clientName;
        this.version = version;
    }


    @Override
    public void run() {
        requestForCandidates(this.version);
    }

    public void requestForCandidates(IntegerProperty version){
        String finalUrl = HttpUrl
                .parse(Constants.GET_CANDIDATES_SOLUTIONS)
                .newBuilder()
                .addQueryParameter(Constants.UBOAT,uboatName)
                .addQueryParameter(Constants.CANDIDATES_VERSION,String.valueOf(version.get()))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                            try {
                                if (response.isSuccessful()) {
                                    String rawBody = response.body().string();
                                    Type listType = new TypeToken<List<AgentSolutionEntry>>(){}.getType();
                                    List<AgentSolutionEntry> candidateList = Constants.GSON_INSTANCE.fromJson(rawBody, listType);
                                    Platform.runLater(() -> {
                                        if (candidateList.size() > 0) {
                                            solutionConsumer.accept(candidateList);
                                        }
                                    });

                                }
                            }catch(RuntimeException exception){
                                exception.printStackTrace();
                            }



                        }
                }

        );


    }



}
