package auxiliary;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

public class Message implements Serializable {
    private final String originalMsg;
    private final String processedMsg;
    private final long timeProcessed;


    public Message(String original, String processed , long time){
        originalMsg = original;
        processedMsg = processed;
        timeProcessed = time;
    }

    public String getProcessed(){
        return this.processedMsg;
    }

    public SimpleStringProperty processedProperty(){return new SimpleStringProperty(this.processedMsg);}
    public SimpleStringProperty originalProperty(){return new SimpleStringProperty(this.originalMsg);}

    public long getTimeProcessed() {
        return this.timeProcessed;
    }

    public SimpleIntegerProperty timeProcessedProperty() {
        return new SimpleIntegerProperty((int) timeProcessed);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.originalMsg);
        sb.append("-->");
        sb.append(this.processedMsg);
        sb.append(" ").append(this.timeProcessed).append(" nano-seconds");
        return sb.toString();
    }

}
