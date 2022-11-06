package machine.parts;
import auxiliary.Alphabet;
import auxiliary.RomanNumeral;
import exceptions.InvalidConfigurationException;
import jaxb.generated.CTEReflect;
import jaxb.generated.CTEReflector;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Reflector implements Serializable {
    private HashMap<Integer, Integer> reflectorMapping;

    private RomanNumeral id;
    private String idName;




    public Reflector(CTEReflector rafi) {
        this.idName = rafi.getId().toUpperCase();
        this.id = RomanNumeral.getRoman(idName);

        this.reflectorMapping = new HashMap<>();
        for (CTEReflect reflect : rafi.getCTEReflect()) {
            reflectorMapping.put(reflect.getInput()-1,reflect.getOutput()-1);
            reflectorMapping.put(reflect.getOutput()-1, reflect.getInput()-1);
        }
    }

    public Reflector(Reflector reflector) {
        this.reflectorMapping = new HashMap<>();
        this.reflectorMapping.putAll(reflector.reflectorMapping);
        this.id = reflector.id;
        this.idName = reflector.idName;
    }


    public int getReflection(int input){
        return this.reflectorMapping.get(input);
    }

    public void validateReflector(Alphabet abc) throws InvalidConfigurationException{
        if(id == RomanNumeral.Undefined){
            String msg = "Reflector with id " + idName + " is not a Roman number between I to V";
            throw new InvalidConfigurationException(msg);
        }


        // check validity of reflector mappings
        Set<Integer> keys = IntStream.range(1,abc.size()+1).boxed().collect(Collectors.toCollection(HashSet::new));
        for (Map.Entry<Integer, Integer> pair:reflectorMapping.entrySet()) {
            int a = pair.getKey()+1;
            int b = pair.getValue()+1;
            if (Objects.equals(a, b)){
                String msg = "Reflector " + this.idName + " has mapping from input to itself";
                throw new InvalidConfigurationException(msg);
            }
            if(keys.contains(a) && !keys.contains(b)){
                String msg = "Reflector " + this.idName + " has double mapping to a single output -> " + b;
                throw new InvalidConfigurationException(msg);
            } else if (keys.contains(b) && !keys.contains(a)) {
                String msg = "Reflector " + this.idName + " has double mapping to a single output -> " + a;
                throw new InvalidConfigurationException(msg);
            }
            keys.remove(a);
            keys.remove(b);
        }
        if(!keys.isEmpty()){
            int something = keys.iterator().next();
            String msg = "Reflector " + this.idName + " is missing mapping to output -> " + something;
            throw new InvalidConfigurationException(msg);
        }
    }

    public String toString(){
        return '<' + this.id.name() + '>';
    }

    public RomanNumeral getRoman(){ return this.id; }

    public int getReflectorID(){
        return this.id.getValue();
    }



}
