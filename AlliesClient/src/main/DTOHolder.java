package main;

import users.User;

public class DTOHolder {

    private User user;

    private final static DTOHolder INSTANCE = new DTOHolder();

    private DTOHolder(){}

    public static DTOHolder getInstance(){
        return INSTANCE;
    }


    public void setUser(User u){
        this.user = u;
    }

    public User getUser(){
        return this.user;
    }

}
