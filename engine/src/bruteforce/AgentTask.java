package bruteforce;
import java.util.List;

public class AgentTask {

    private final long ticks;        // how many rotor combination to iterate over
    private final  List<Integer> rotors;

    private final int reflector;
    private final List<Integer> positions;

    private String secretMessage;

    private final int jobSequenceNumber;


    /**
     * @param rotors the chosen rotors in specific arrangement
     * @param positions the position from which the agent will start to decrypt
     * @param reflector the reflector we are going to use
     * @param ticks  the number of positions the agent will check.  Agent will change the rotor position in sequence
     *
     */
    public AgentTask(String secret, List<Integer> rotors, List<Integer> positions , int reflector , long ticks, int jobNumber){
        this.secretMessage = secret;
        this.rotors = rotors;
        this.positions = positions;
        this.reflector = reflector;
        this.ticks = ticks;
        this.jobSequenceNumber = jobNumber;
    }
    public long getTicks(){
        return ticks;
    }

    public List<Integer> getRotorsIds(){
        return this.rotors;
    }

    public List<Integer> getPositions(){
        return this.positions;
    }

    public int getJobSequenceNumber(){return this.jobSequenceNumber;}


    public int getReflector() {
        return this.reflector;
    }

    public void setSecretMessage(String secret){
        this.secretMessage = secret;
    }

    public String getSecretMessage(){
        return this.secretMessage;
    }
}
