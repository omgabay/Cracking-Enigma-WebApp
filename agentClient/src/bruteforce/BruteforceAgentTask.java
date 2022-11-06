package bruteforce;

import users.DTO.AgentProgressData;
import users.DTO.CandidateList;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import machine.Engine;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import users.AgentEntry;
import utils.Constants;
import utils.http.HttpClientUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import static utils.Constants.*;


/*
*   BruteForceTask sends the Bruteforce solutions to the server*
*
 */
public class BruteforceAgentTask extends Task<Boolean> {

    private final String agentName;
    private final String teamName;
    private final int tasksPerRequest;

    private final BlockingQueue<AgentTask> tasksQueue;
    private final BlockingQueue<AgentSolutionEntry> solutionsQueue;
    private final List<AgentWorker> bruteforceWorkers;
    private final BooleanProperty contestEnded;

    private Decryption dm;

    Consumer<AgentProgressData> updateAgentProgress;

    AgentProgressData myProgress;

    FileOutputStream fos;




    public BruteforceAgentTask(Engine engine, AgentEntry agentEntry, Consumer<AgentProgressData> updateUIWithProgress){
        this.agentName = agentEntry.getName();
        this.teamName = agentEntry.getTeamName();
        tasksPerRequest = agentEntry.getTasksToPull();
        myProgress = new AgentProgressData(agentName);

        contestEnded = new SimpleBooleanProperty(false);

        tasksQueue = new LinkedBlockingQueue<>();
        solutionsQueue = new LinkedBlockingQueue<>();
        updateAgentProgress = updateUIWithProgress;



        // check for valid workers parameter
        int workers = agentEntry.getWorkerCount();

        this.bruteforceWorkers = new ArrayList<>();
        for (int i = 0; i < workers; i++) {
            AgentWorker worker = new AgentWorker(engine,this.agentName,this.teamName,tasksQueue, this::addSolutions);
            bruteforceWorkers.add(worker);
        }
    }



    @Override
    protected Boolean call() throws Exception {
        this.createAgentTaskRequest();
        startWorkerThreads();
        while(!contestEnded.get()){
            myProgress.setPendingTasks(tasksQueue.size());
            if(tasksQueue.isEmpty()){
                if(this.solutionsQueue.size() > 0) {
                    sendAgentSolutions();
                    int completed = myProgress.getCompletedTasks() + this.tasksPerRequest;
                    myProgress.setCompletedTasks(completed);
                }
                // contact the Server's DM to get more tasks
                createAgentTaskRequest();
                Platform.runLater(()->
                            updateAgentProgress.accept(myProgress)
                        );

            }
            Thread.sleep(1000);
        }
        this.cancelWorkers();
        System.out.println("Agent " + agentName + " Bruteforce task is done!");
        return true;
    }




    private void startWorkerThreads() {
        int i=1;
        for (AgentWorker worker  : this.bruteforceWorkers) {
            Thread t = new Thread(worker);
            t.setDaemon(true);
            t.setName(this.agentName + i++);
            t.start();
        }
    }


    public void addSolutions(List<AgentSolutionEntry> solutions){
        this.solutionsQueue.addAll(solutions);
    }

    private void sendAgentSolutions() {
        List<AgentSolutionEntry> candidates = new ArrayList<>();
        solutionsQueue.drainTo(candidates);


        CandidateList cl = new CandidateList(this.agentName, this.teamName, candidates);
        String candidateJson = GSON_INSTANCE.toJson(cl);
        RequestBody formBody = new FormBody.Builder()
                .add(CANDIDATES, candidateJson)
                .build();
        Request request = new Request.Builder()
                .url(SEND_CANDIDTATE_URL)
                .post(formBody)
                .build();
        HttpClientUtil.postAsyncRequest(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) { }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if(response.isSuccessful()){
                       String rawBody = response.body().string();
                       try {
                           AgentSolutionEntry agentSolution = GSON_INSTANCE.fromJson(rawBody, AgentSolutionEntry.class);

                       }catch(JsonSyntaxException jse){
                           jse.printStackTrace();
                       }
                    }
            }
        });


    }

    private void createAgentTaskRequest() {
        String finalUrl = HttpUrl
                .parse(Constants.FETCH_AGENT_TASKS_URL)
                .newBuilder()
                .addQueryParameter(Constants.ALLY, this.teamName)
                .addQueryParameter(Constants.NUM_OF_TASKS, String.valueOf(this.tasksPerRequest))
                .build()
                .toString();
        // Sending BF task request to server
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Brute force agent task error in fetching new tasks");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String rawBody = response.body().string();
                    Type listType = new TypeToken<List<AgentTask>>(){}.getType();
                    List<AgentTask> taskList = GSON_INSTANCE.fromJson(rawBody,listType);
                    if(taskList != null){
                        tasksQueue.addAll(taskList);
                    }
                }
                response.close();
            }
        });
    }


    private void cancelWorkers() {
        for (AgentWorker worker : this.bruteforceWorkers) {
            worker.setCancelled(true);
        }
    }

}
