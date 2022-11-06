package machine.parts;
import auxiliary.Alphabet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Plugboard implements Serializable {
    private HashMap<Character,Character> plugboardMapper;
    private Alphabet abc;


    public Plugboard(Map<Character, Character> plugs, Alphabet alphabet) {
        this.abc = alphabet;
        this.plugboardMapper = new HashMap<>();
        plugboardMapper.putAll(plugs);
    }
    public Plugboard(Alphabet abc) {
        this.plugboardMapper = new HashMap<>();
        this.abc = abc;
    }

    public Plugboard(Plugboard pb) {
        this.plugboardMapper = new HashMap<>();
        this.plugboardMapper.putAll(pb.plugboardMapper);
        this.abc = pb.abc;
    }


    public boolean addToPlugboard(char c1, char c2){
        if(c1 == c2 || abc.getOrder(c1) == -1 || abc.getOrder(c2) == -1)
            return false;

        if(plugboardMapper.containsKey(c1) || plugboardMapper.containsKey(c2))
            return false;

        plugboardMapper.put(c1,c2);
        plugboardMapper.put(c2,c1);
        return true;
    }

    public char plugboardConverter(char input){
        if(plugboardMapper.containsKey(input))
            return plugboardMapper.get(input);
        return input;
    }


    public String toString(){
        if(plugboardMapper.isEmpty())
            return "";
        StringBuilder res = new StringBuilder();
        for (Map.Entry<Character,Character> entry:this.plugboardMapper.entrySet()) {
            char key = entry.getKey();
            char value = entry.getValue();
            if(key < value){
                if(res.length() != 0)
                    res.append(',');
                res.append(key).append('|').append(value);
            }
        }
        return '<' + res.toString() + '>';
    }


}
