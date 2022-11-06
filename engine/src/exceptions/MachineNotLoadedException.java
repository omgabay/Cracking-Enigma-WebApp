package exceptions;

public class MachineNotLoadedException extends EnigmaException{

    public MachineNotLoadedException(){
        super("machine.Enigma machine was not loaded. Make sure to load xml config file before any other operation.");
    }
}
