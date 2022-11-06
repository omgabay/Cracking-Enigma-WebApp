package machine.parts;

import auxiliary.Alphabet;

import java.io.Serializable;
import java.util.*;

public class Rotors implements Serializable , Iterator<List<Integer>> {

    private final LinkedList<Rotor> rotorList;
    private final List<Integer> initialConfiguration;



    public Rotors(List<Rotor> rotorsList) {

        rotorList = new LinkedList<>(rotorsList);

        initialConfiguration = new ArrayList<>();
        for(Rotor r: rotorList) {
            initialConfiguration.add(r.getRotorPosition());
        }
    }

    public Rotors(Rotors rotors) {
        // DEEP Copy for Rotors
        this.rotorList = new LinkedList<>();
        for (Rotor r : rotors.rotorList) {
            rotorList.add(new Rotor(r));
        }

        this.initialConfiguration = new ArrayList<>();
        for(Rotor r: this.rotorList) {
            initialConfiguration.add(r.getRotorPosition());
        }
    }

    public void resetRotors(){
        Iterator<Integer> it = this.initialConfiguration.iterator();
        for (Rotor r: this.rotorList) {
            r.setPosition(it.next());
        }
    }



    public int forwardPass(char input, Alphabet abc){
        this.rotateFirstRotor();
        int index = abc.getOrder(input);
        char output = input;

        for (Rotor r : rotorList) {
             index = r.getRightToLeftMapping(index);
             output = abc.getLetter(index);
        }
        return abc.getOrder(output);
    }

    private void rotateFirstRotor() {
        Iterator<Rotor> it = this.rotorList.iterator();
        boolean keepRotate;
        do{
            keepRotate = it.next().Rotate();
        }while(it.hasNext() && keepRotate);
    }

    /**
     * @param input - the signal coming from the reflector
     * @return output signal after passing the rotors on the way back - reflector > rotors > plugboard > output
     */
    public int backwardPass(char input, Alphabet abc){

        int index = abc.getOrder(input);
        char output = input;
        Iterator<Rotor> it = rotorList.descendingIterator();

        while(it.hasNext()){

            Rotor r = it.next();
            index = r.getLeftToRightMapping(index);
            output = abc.getLetter(index);
        }
        return abc.getOrder(output);
    }




    public List<Integer> getPositionsIndices(){
        List<Integer> positions = new LinkedList<>();
        Iterator<Rotor> it = rotorList.descendingIterator();

        while(it.hasNext()){
            positions.add(it.next().getRotorPosition());
        }

        return positions;
    }

    public void setRotorPositions(List<Integer> positions) {
        Iterator<Rotor> it = this.rotorList.descendingIterator();
        Iterator<Integer> newPositions = positions.iterator();
        while(it.hasNext()){
            it.next().setPosition(newPositions.next());
        }

    }


    public int size() {
        return this.rotorList.size();
    }

    public List<Integer> getRotorIDs(){
        Iterator<Rotor> it = this.rotorList.descendingIterator();

        List<Integer> ids = new ArrayList<>();
        while(it.hasNext()){
            ids.add(it.next().getID());
        }
        return ids;
    }




    // Iterator related
    public Iterator<Rotor> getIterator() {
        return this.rotorList.iterator();
    }


    // Iterator functions
    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public List<Integer> next() {
        Iterator<Rotor> it = this.rotorList.iterator();
        Rotor rotor = it.next();
        boolean flag = true;
        while(it.hasNext() && flag){
            flag = rotor.next(); // keep rotating
            rotor = it.next(); // getting next rotor
        }
        return this.getPositionsIndices();
    }




    public String toString(){
        StringBuilder sb = new StringBuilder();

        // Printing Rotor Initial Setup -
        Iterator<Rotor> it  = rotorList.iterator();
        boolean first = true;
        while(it.hasNext()){
            if(first){
                sb.append('<');
                first = false;
            }else{
                sb.append(',');
            }
            sb.append(it.next().getID());
        }
        sb.append('>');
        it = rotorList.iterator();
        sb.append('<');
        while(it.hasNext()){
            sb.append(it.next().toString());
        }
        sb.append('>');
        return sb.toString();
    }


}
