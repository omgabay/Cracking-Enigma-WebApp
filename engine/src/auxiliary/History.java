package auxiliary;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class History implements Serializable {

    private List<Message>  messages;

    private String machineConfiguration;

    public History(String machineConfig){
        this.machineConfiguration = machineConfig;
        messages = new ArrayList<>();
    }

    public void add(Message m) {
      messages.add(m);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(machineConfiguration).append('\n');
        int i=1;
        for(Message m : messages){
            sb.append(i++).append(". ").append(m.toString()).append('\n');
        }
        return sb.toString();
    }



}
