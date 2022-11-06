package users;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class User {

    public final String username;



    public enum ClientType {UBOAT, ALLY, AGENT, BATTLE};
    ClientType type;


    public User(String name){
        username = name;
    }

    public User(String name, ClientType type){
        username = name;
        this.type = type;
    }

    public String getName(){
        return username;
    }


    public ObservableValue<String> NameProperty() {
        return new SimpleStringProperty(this.username);
    }


    public void logout(){
        System.out.println("User " + username + " left contest");
    }

    public String toString(){
        return "Hello from server to " + username;
    }
}
