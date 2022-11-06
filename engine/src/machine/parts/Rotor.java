package machine.parts;
import auxiliary.Alphabet;
import exceptions.EnigmaException;
import exceptions.InvalidConfigurationException;
import jaxb.generated.CTEPositioning;
import jaxb.generated.CTERotor;

import java.io.Serializable;
import java.util.*;

public class Rotor implements Serializable , Iterator<Boolean> {
    private final int id;
    private int rotorPosition;
    private final int notch;

    private final int rotorSize;
    private final ArrayList<Character> rightColumn;
    private final ArrayList<Character> leftColumn;

    private final Map<Character,Integer> letterToRight;
    private final Map<Character, Integer> letterToLeft;

// Rotor Initial Setup Variables
    private char startPositionCharacter;
    private final int startPositionIndex;




    // Copy constructor for rotor
    public Rotor(Rotor rotor){
        this.id = rotor.id;
        this.notch = rotor.notch;
        this.rotorSize = rotor.rotorSize;


        this.rotorPosition = rotor.rotorPosition;

        //Copying positions
        this.startPositionCharacter = rotor.startPositionCharacter;
        this.startPositionIndex = rotor.startPositionIndex;



        // Copying left column
        this.leftColumn  = new ArrayList<>();
        for (char c : rotor.leftColumn) {
            this.leftColumn.add(c);
        }

        // Copying right column
        this.rightColumn = new ArrayList<>();
        for(char c : rotor.rightColumn){
            this.rightColumn.add(c);
        }

        // Copying map
        this.letterToLeft = new HashMap<>(rotor.letterToLeft);
        this.letterToRight = new HashMap<>(rotor.letterToRight);

    }
    public Rotor(CTERotor cteRotor, Alphabet abc) {
        this.notch = cteRotor.getNotch() - 1;
        this.id = cteRotor.getId();
        this.rotorSize = abc.size();

        // Task 2 - JavaFX Rotor Position changed to SimpleIntegerProperty
        this.rotorPosition= 0;

        this.startPositionCharacter = abc.getLetter(0);
        this.startPositionIndex = 0;
        leftColumn = new ArrayList<>();
        rightColumn = new ArrayList<>();
        letterToRight = new HashMap<>();
        letterToLeft = new HashMap<>();

        for (CTEPositioning pos : cteRotor.getCTEPositioning()) {
            char left = pos.getLeft().charAt(0);
            char right = pos.getRight().charAt(0);
            leftColumn.add(left);
            rightColumn.add(right);

        }
        for (char letter : leftColumn) {
            letterToRight.put(letter, rightColumn.indexOf(letter));
            letterToLeft.put(letter, leftColumn.indexOf(letter));
        }

    }



    public boolean Rotate(){
        int position = rotorPosition;
        position++;
        position %= rotorSize;

        // Updating Rotor Position Property  - Update Task 2 JavaFX
        rotorPosition = position;

        // Changing the rotor mapping columns
        leftColumn.add(leftColumn.remove(0));
        rightColumn.add(rightColumn.remove(0));
        return position == this.notch;
    }


    public int getID(){
        return this.id;
    }

    public void validateRotor(Alphabet abc) throws EnigmaException{

        // Check if notch is not out of bounds
        if(this.notch < 0 || this.notch >= rotorSize){
            String msg = "Rotor #"+this.id + ": Notch is not defined within valid range: (1," + rotorSize + ")";
            throw new InvalidConfigurationException(msg);
        }
        // Check for rotor double mapping two inputs that map to the same exit

        Set<Character> left = new HashSet<>();
        Set<Character> right = new HashSet<>();
        for (char letter :abc.getABC()) {
            left.add(letter);
            right.add(letter);
        }
        for (char letter : this.rightColumn) {
            if(!abc.isLetter(letter)){
                String msg = "Rotor #" + this.id + " Right column contains letter not in the alphabet (" + letter + ")";
                throw new InvalidConfigurationException(msg);
            }
            if(!right.contains(letter)){
                String msg = "Rotor #" + this.id + ": Rotor Right Column has extra mapping for the same letter (" + letter +")";
                throw new InvalidConfigurationException(msg);
            }
            right.remove(letter);
        }
        for (char letter : this.leftColumn) {
            if(!abc.isLetter(letter)){
                String msg = "Rotor #" + this.id + " Right column contains letter not in the alphabet (" + letter + ")";
                throw new InvalidConfigurationException(msg);
            }
            if(!left.contains(letter)){
                String msg = "Rotor #" + this.id + ": Rotor Left Column has extra mapping for the same letter (" + letter + ")";
                throw new InvalidConfigurationException(msg);
            }
            left.remove(letter);
        }
        if(!right.isEmpty()){
            char something = right.iterator().next();
            String msg = "Rotor #" + this.id + ": Right Column is missing mapping for letter " + something;
            throw new InvalidConfigurationException(msg);
        }
        if(!left.isEmpty()){
            char something = left.iterator().next();
            String msg = "Rotor #" + this.id + ": Left Column is missing mapping for letter " + something;
            throw new InvalidConfigurationException(msg);
        }


    }


    /**
     * @param input The function gets the input letter and returns the index on the wheel where we exit
     * @return index of the new output signal after passing through the rotor
     */
    public int getLeftToRightMapping(int input){
        char letter = leftColumn.get(input);
        int output = this.letterToRight.get(letter) - this.rotorPosition;
        if (output < 0){
            output = this.rotorSize + output;
        }
        return output;
    }

    public int getRightToLeftMapping(int input){
        char letter = rightColumn.get(input);   // gets the letter from the index in right column
        int output = this.letterToLeft.get(letter) - this.rotorPosition;
        if (output < 0){
            output = (this.rotorSize + output) % this.rotorSize;
        }
        return output;
    }


    public void setInitialPosition(char position) {
        this.startPositionCharacter = position;
    }

    public void setPosition(int position){
        while(this.rotorPosition != position){
            leftColumn.add(leftColumn.remove(0));
            rightColumn.add(rightColumn.remove(0));
            this.rotorPosition++;
            this.rotorPosition %= rotorSize;
        }
    }



    public int getDistanceFromNotch() {
        int position = rotorPosition;
        if(this.notch >= position) {
            return this.notch - position;
        }
        return (this.rotorSize - position) + 1 + this.notch;
    }

    public int getDistanceFromNotch(int position){
        if(this.notch >= position) {
            return this.notch - position;
        }
        return (this.rotorSize-position) + 1 + this.notch;
    }


    public String toString(){
        return this.startPositionCharacter +"("+getDistanceFromNotch(startPositionIndex)+")";
    }



    @Override
    public boolean hasNext() {
        int position = rotorPosition;
        return position != this.rotorSize -1;
    }

    @Override
    public Boolean next() {
        Rotate();
        return this.rotorPosition == 0;
    }

    public int getRotorPosition() {
        return this.rotorPosition;
    }
}
