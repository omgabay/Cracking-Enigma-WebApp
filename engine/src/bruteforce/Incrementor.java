package bruteforce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Incrementor {

    private final int base;
    private final int digits;
    private long currentValue;

    protected final List<Integer> rotors;
    private int reflector;

    private final long MAX_VALUE;

    private static long JOB_SIZE;

    protected String messageToDecrypt;

    protected static int jobCount;
    public Incrementor(List<Integer> rotorIds, int alphabetSize, int reflectorId, String secretMessage) {
        this.base = alphabetSize;
        this.currentValue =  0;
        this.digits = rotorIds.size();
        this.rotors = rotorIds;
        this.reflector = reflectorId;
        Collections.reverse(rotorIds);

        this.messageToDecrypt = secretMessage;
        MAX_VALUE = (long) Math.pow(base,digits);
        JOB_SIZE = (long) Math.pow(base,digits-1);
        jobCount = 1;
    }


    public static void setJobSize(long TASK_SIZE){
        JOB_SIZE = TASK_SIZE;
    }




    public long getValue(){
        return this.currentValue;
    }


    public boolean finished(){
        return this.currentValue >= this.MAX_VALUE;
    }


    protected long getTaskDuration() {
        if(currentValue >= this.MAX_VALUE){
            return 0;
        }
        long sum = currentValue + JOB_SIZE;
        this.currentValue = sum;
        if( sum > this.MAX_VALUE){
            return this.MAX_VALUE - currentValue;
        }
        return JOB_SIZE;
    }



    // Converts Counter to list of integers in base (rotor-size)
    public List<Integer> getPositions(){
        LinkedList<Integer> positions = new LinkedList<>();
        long x = this.currentValue;

        for(int i=0; i<digits; i++){
            int remainder = (int) x % this.base;
            positions.addFirst(remainder);
            x = x / base;
        }
        return positions;
    }




    public List<AgentTask> getNewJobs(){
        List<AgentTask> result = new ArrayList<>();
        List<Integer> rotorPositions = this.getPositions();
        long taskDuration = this.getTaskDuration();
        // Incrementor is done and need to get off the list
        if(taskDuration == 0){
            return result;
        }
        result.add(new AgentTask(messageToDecrypt, this.rotors, rotorPositions, this.reflector, taskDuration, jobCount++));
        return result;

    }


    public long getMaxValue() {
        return this.MAX_VALUE;
    }
}
