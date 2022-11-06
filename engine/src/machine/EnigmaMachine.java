package machine;
import auxiliary.Alphabet;
import auxiliary.Message;
import machine.parts.Plugboard;
import machine.parts.Reflector;
import machine.parts.Rotor;
import machine.parts.Rotors;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnigmaMachine implements Enigma {

    private final Alphabet alphabet;
    private final Plugboard pb;
    private  Rotors rotors;
    private Reflector reflector;

    private int messageCount;


    public EnigmaMachine(List<Rotor> rotors, Reflector r, Plugboard plugs, Alphabet alphabet) {
        this.alphabet = alphabet;
        this.rotors = new Rotors(rotors);
        this.pb = plugs;
        this.reflector = r;
        this.messageCount = 0;
    }

    /**
     * @param machine Enigma Machine Copy constructor - creating new machine from the given one
     */
    public EnigmaMachine(EnigmaMachine machine) {
        // Copying Rotors
        this.rotors = new Rotors(machine.rotors);

        // shallow copy for reflector,plugboard and alphabet
        this.reflector = machine.reflector;
        this.pb = machine.pb;
        this.alphabet = machine.alphabet;
        this.messageCount = machine.messageCount;
    }


    @Override
    public Message processText(String text) {
        StringBuilder encodedMsg = new StringBuilder(text.length());
        String processed;
        char encoded;
        long start = System.nanoTime();
        for (char letter : text.toCharArray()) {
            letter = pb.plugboardConverter(letter);  // check in plugboard and swap letter if necessary
            int signal = rotors.forwardPass(letter, alphabet);
            //System.out.print("reflector:"+ abc.getLetter(signal) + "->");
            signal = reflector.getReflection(signal);
            //System.out.println(abc.getLetter(signal));
            letter = alphabet.getLetter(signal);
            int output = rotors.backwardPass(letter, alphabet);
            char outputLetter = alphabet.getLetter(output);
            encoded = pb.plugboardConverter(outputLetter);
            encodedMsg.append(encoded);
        }
        long finish = System.nanoTime();
        processed = encodedMsg.toString();
        this.messageCount++;
        return new Message(text, processed, finish - start);
    }

    @Override
    public void resetMachine() {
        this.rotors.resetRotors();
    }

    @Override

    // DELETE - FOR TESTING
    public Iterator<List<Integer>> getRotorsIterator() {
        return this.rotors;
    }

    @Override
    public List<Integer> getPositionsIndices() {
        return this.rotors.getPositionsIndices();
    }

    @Override
    public List<Integer> setRotorPositions(List<Integer> positions) {
        this.rotors.setRotorPositions(positions);
        return positions;
    }

    @Override
    public void swapReflector(Reflector rafi) {
        this.reflector = rafi;
    }

    @Override
    public Alphabet getAlphabet() {
        return this.alphabet;
    }

    @Override
    public void setRotors(Rotors newRotors) {
        this.rotors = newRotors;
    }


    @Override
    public List<Integer> getRotorIDs() {
        return rotors.getRotorIDs();
    }

    @Override
    public List<Character> getRotorPositions() {
        List<Character> positions = new ArrayList<>();
        for(int position : rotors.getPositionsIndices()){
            positions.add(alphabet.getLetter(position));
        }


        return positions;
    }

    public String toString() {
        // Prints the Machine Initial Setup in String format
        return rotors.toString() + reflector.toString() + pb.toString();
    }


    @Override
    public Reflector getReflector() {
        return new Reflector(this.reflector);
    }



    @Override
    public int getNumOfRotors() {
        return this.rotors.size();
    }


    @Override
    public Plugboard getPlugboard() {
        return new Plugboard(this.pb);
    }

    @Override
    public String getCurrentConfiguration() {
        StringBuilder sb = new StringBuilder();
        List<Integer> rotorIds = rotors.getRotorIDs();

        for(int i=0; i<rotorIds.size(); i++){
            if(i==0){
                sb.append("<").append(rotorIds.get(i));
            }else{
                sb.append(",").append(rotorIds.get(i));
            }
        }

        sb.append('>');


        Iterator<Integer> positions = rotors.getPositionsIndices().iterator();
        sb.append('<');
        while(positions.hasNext()) {
            sb.append(alphabet.getLetter(positions.next()));
        }
        sb.append('>');


        sb.append(reflector.toString());
        sb.append(pb.toString());
        return sb.toString();
    }

    @Override
    public int getMessageCount() {
        return messageCount;
    }

}
