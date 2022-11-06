package auxiliary;

import machine.parts.Plugboard;
import machine.parts.Reflector;

import java.util.List;

/**
 * Interface for making queries and getting information about your enigma machine
 */
public interface MachineInfo {
    public List<Integer> getRotorIDs();
    public List<Character> getRotorPositions();

    public Reflector getReflector();


    public int getNumOfRotors();


    public Plugboard getPlugboard();
    public String getCurrentConfiguration();


    public int getMessageCount();
    @Override
    public String toString();


}
