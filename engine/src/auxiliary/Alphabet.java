package auxiliary;
import exceptions.EnigmaException;

import java.io.Serializable;
import java.util.*;


public class Alphabet implements Serializable {
    private final String abc;
    private final HashMap<Character, Integer> order;
    public Alphabet(String alphabet){
        abc = alphabet;
        order = new HashMap<>();
        int index = 0;
        for (char letter: abc.toCharArray()) {
            letter = Character.toUpperCase(letter);
            order.put(letter,index++);
        }
    }

    public int getOrder(char letter){
        letter = Character.toUpperCase(letter);
        if (!order.containsKey(letter)){
            throw new EnigmaException("letter is not in the alphabet '" + letter +"'");
        }
        return order.get(letter);
    }

    public char getLetter(int index){
        return abc.charAt(index);
    }

    public boolean isLetter(char c){
        return order.containsKey(Character.toUpperCase(c));
    }
    public char [] getABC(){ return abc.toCharArray();}

    public int size() {return abc.length();}

    @Override
    public String toString(){
        return abc;
    }


    public ListIterator<Character> iterator() {
        List<Character> chars = new ArrayList<>();
        for (char c : abc.toCharArray()) {
            chars.add(c);
        }
        return chars.listIterator();
    }

    public ListIterator<Character> iterator(int index){
        List<Character> chars = new ArrayList<>();
        for (char c : abc.toCharArray()) {
            chars.add(c);
        }
        return chars.listIterator(index);
    }
}
