package utils;//package utils;
//
//import bruteforce.AgentSolutionEntry;
//import javafx.beans.property.BooleanProperty;
//import bruteforce.BruteForceAgentTask;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.HttpUrl;
//import okhttp3.Response;
//import org.jetbrains.annotations.NotNull;
//import users.AgentEntry;
//import utils.http.HttpClientUtil;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//import java.util.function.Consumer;
//
//public class AgentRefreshTask extends TimerTask {
//    private final BooleanProperty startBruteforceAttack;
//    private final String agentName;
//    private Consumer<List<AgentSolutionEntry>> solutionConsumer;
//    private Timer timer;
//
//    public AgentRefreshTask(String agent, Timer timer, BooleanProperty startBF){
//        startBruteforceAttack = startBF;
//        agentName = agent;
//        this.solutionConsumer = solutionConsumer;
//    }
//
//
//
//    @Override
//    public void run() {
//        boolean startBF = startBruteforceAttack.get();
//        if(!startBF){
//            checkIfAgentReadyRequest();
//        }else{
//
//            timer.cancel();
//            timer.purge();
//        }
//    }
//
//
//
//    public void checkIfAgentReadyRequest(){
//        // Send request to check ready status of agent
//        String finalUrl = HttpUrl
//                .parse(Constants.GET_USER_RESOURCE_PAGE)
//                .newBuilder()
//                .addQueryParameter(Constants.USERNAME, agentName)
//                .build()
//                .toString();
//        HttpClientUtil.runAsync(finalUrl, new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                if(response.isSuccessful()){
//                    String rawBody = response.body().string();
//                    AgentEntry agentEntry = Constants.GSON_INSTANCE.fromJson(rawBody, AgentEntry.class);
//                    if(agentEntry.isReady()){
//                        startBruteforceAttack.set(true);
//                    }
//                }
//            }
//        });
//    }
//}
