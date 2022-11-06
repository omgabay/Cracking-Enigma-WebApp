package machine;

import auxiliary.Alphabet;
import auxiliary.Dictionary;
import auxiliary.EngineResponse;
import auxiliary.Battlefield;
import jaxb.generated.CTEEnigma;
import machine.parts.Reflector;
import machine.parts.Rotor;
import machine.parts.Rotors;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IEngine extends Serializable {

    public EngineResponse<?> loadMachineFromXml(File xml) throws FileNotFoundException, JAXBException;

    public EngineResponse<?> displayMachine();

    public EngineResponse<?> showHistory();




    public Enigma setupMachine(List<Integer> rotorIDs, List<Character> rotorPositions, String reflectorID, Map<Character, Character> plugs);

    public Enigma setupMachineAtRandom();



    /**
     * @param text input text for the machine to be encrypted or decrypted
     * @return processed text
     */
    public EngineResponse<?> processText(String text);



    public EngineResponse<?>getMachineInfo();





    /**
     * Resets enigma machine to initial setup - all rotors are returned to initial positions.
     */
    public void resetMachine();



    
    public EngineResponse<?> getEnigmaSettings();


    public boolean isLoaded();
    public boolean isMachinePresent();


    public Enigma getMachine();

    Dictionary getDictionary();

    Alphabet getMachineAlphabet();


     int getNumOfReflectors();

     int getTotalAvailableRotors();

    int getNumberOfMachineRotors();

    int getTotalAvailableReflectors();


    public List<Reflector> getReflectorsFromEngine();

    List<Rotor> getRotorsFromEngine();


    Rotors getNewRotors(List<Integer> rotorIds);


    public Battlefield getBattlefield();



    public CTEEnigma loadMachineFromInputStream(InputStream is) throws JAXBException;
    public void loadFromJson(String json);








}
