package bruteforce;

import java.util.ArrayList;
import java.util.List;


public class AdvancedIncrementor extends Incrementor{
    private final int reflectorCount;
    public AdvancedIncrementor(List<Integer> rotors, int base, int reflectorCount, String secretMessage){
        super(rotors , base, 0, secretMessage);
        // Machine Specific variables

        this.reflectorCount = reflectorCount;
    }

    public List<Integer> getRotors() {
        return rotors;
    }


    public List<AgentTask> getNewJobs(){
        List<AgentTask> jobs = new ArrayList<>();
        long taskDuration = this.getTaskDuration();

        if(taskDuration == 0){
            return jobs;
        }

        List<Integer> positions = this.getPositions();
        for (int reflectorID = 0; reflectorID < this.reflectorCount; reflectorID++) {
            // swap reflectors
            jobs.add(new AgentTask(this.messageToDecrypt, this.rotors, positions , reflectorID , taskDuration, ++jobCount));

        }
        return jobs;
    }




}
