package exceptions;

public class EnigmaException extends RuntimeException{
    public EnigmaException(String message){
        super(message);
    }
    public EnigmaException(){
        super();
    }
}
