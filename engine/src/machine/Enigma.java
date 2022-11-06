package machine;
import auxiliary.Alphabet;
import auxiliary.MachineInfo;
import auxiliary.Message;
import machine.parts.Reflector;
import machine.parts.Rotors;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public interface Enigma extends MachineInfo, Serializable {


    /**
     * @param text  input text the operator will give to the machine.Enigma machine
     * @return outputs the encrypted string processed by the machine.Enigma
     */
    Message processText(String text);

    /**
     * Reset machine to initial configurations - all rotors go to initial positions
     */
   void resetMachine();


    Iterator<List<Integer>> getRotorsIterator();


    List<Integer> getPositionsIndices();



    void swapReflector(Reflector rafi);


    Alphabet getAlphabet();

    void setRotors(Rotors rotors);

    List<Integer> setRotorPositions(List<Integer> positions);

    String toString();
}
