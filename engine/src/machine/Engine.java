package machine;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import auxiliary.*;
import auxiliary.Dictionary;
import bruteforce.Decryption;
import bruteforce.Difficulty;
import auxiliary.Battlefield;
import com.google.gson.Gson;
import exceptions.EnigmaException;
import exceptions.InvalidConfigurationException;
import exceptions.ReflectorNotFound;
import java.io.File;
import java.io.InputStream;
import java.util.*;

import auxiliary.MachineInfo;
import jaxb.generated.*;
import machine.parts.Plugboard;
import machine.parts.Reflector;
import machine.parts.Rotor;
import exceptions.WrongFileFormatException;
import machine.parts.Rotors;

public class Engine implements IEngine {
    private EnigmaMachine enigmaMachine;
    private boolean isLoaded;
    private boolean isMachinePresent;

    private List<Rotor> rotorList;
    private List<Reflector> reflectorList;

    private final List<History> histories;
    private History currentHistory;

    // machine instance variables
    private Dictionary dictionary;
    private Alphabet alphabet;
    private int rotorsCount;


    // Ex3 data
    private Battlefield battlefield;


    public Engine(){
        this.rotorList = new ArrayList<>();
        this.reflectorList = new ArrayList<>(5);
        this.enigmaMachine = null;
        this.histories = new ArrayList<>();
        this.currentHistory = null;
        this.isLoaded = false;
        this.isMachinePresent = false;
    }




    @Override
    public EngineResponse<MachineSettings> loadMachineFromXml(File xml) throws RuntimeException, JAXBException {
        String fileName =  xml.getName();
        int index = fileName.lastIndexOf('.');
        if(index < 0){
            throw new WrongFileFormatException(fileName);
        }else{
            String extension = fileName.substring(index);
            if(!extension.equals(".xml")){
                throw new WrongFileFormatException(fileName);
            }
        }


        JAXBContext jaxbContext = JAXBContext.newInstance(CTEEnigma.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        CTEEnigma cteEnigma =  (CTEEnigma) jaxbUnmarshaller.unmarshal(xml);

        this.loadCTEnigma(cteEnigma);
        isLoaded = true;
        isMachinePresent = false;
        MachineSettings settings = new MachineSettings(rotorsCount, rotorList.size(), reflectorList.size(), this.alphabet);
        return new EngineResponse<>(settings, true);
    }

    @Override
    public CTEEnigma loadMachineFromInputStream(InputStream inputStream) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(CTEEnigma.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        CTEEnigma cteEnigma =  (CTEEnigma) jaxbUnmarshaller.unmarshal(inputStream);
        this.loadCTEnigma(cteEnigma);
        return cteEnigma;
    }

    @Override
    public void loadFromJson(String json) {

        Gson gson = new Gson();
        CTEEnigma cteEnigma  = gson.fromJson(json, CTEEnigma.class);
        this.loadCTEnigma(cteEnigma);
    }



    public void loadCTEnigma(CTEEnigma cteEnigma){
        CTEMachine m = cteEnigma.getCTEMachine();
        this.alphabet = new Alphabet(m.getABC().trim());

        CTEDictionary dictionary = cteEnigma.getCTEDecipher().getCTEDictionary();
        this.dictionary = new Dictionary(dictionary, this.alphabet);


        if(alphabet.size() % 2 != 0)
        {
            throw new InvalidConfigurationException("\nInvalid configuration file, the machine alphabet has to have an even number of letters\n");
        }

        this.rotorsCount = m.getRotorsCount();

        // Creating new rotor list
        this.rotorList = new ArrayList<>();
        for (CTERotor rotor: m.getCTERotors().getCTERotor()) {
            rotorList.add(new Rotor(rotor, this.alphabet));
        }
        if(rotorsCount < 2 || rotorsCount > 99 || rotorsCount > rotorList.size()){
            String msg = "Invalid configuration, rotors-count has to be at least 2 and no more than the available rotors.";
            throw new InvalidConfigurationException(msg);
        }
        // Sorting rotorList by ID
        rotorList.sort(Comparator.comparingInt(Rotor::getID));

        this.reflectorList = new ArrayList<>(5);
        for(CTEReflector reflect : m.getCTEReflectors().getCTEReflector()){
            reflectorList.add(new Reflector(reflect));
        }

        this.battlefield = new Battlefield(cteEnigma.getCTEBattlefield());

        checkForErrors();

    }



    @Override
    public EngineResponse<MachineInfo> displayMachine() {

        return new EngineResponse<>(enigmaMachine, true);
    }



    @Override
    public Enigma setupMachine(List<Integer> rotorIDs, List<Character> rotorPositions, String reflectorID, Map<Character, Character> plugs) {


        // Getting Rotors and setting initial position for each rotor
        List<Rotor> machineRotors = new ArrayList<>();

        for (int id : rotorIDs) {
            machineRotors.add(new Rotor(rotorList.get(id - 1)));
        }

        // Setting Rotors at positions
        Iterator<Character> it = rotorPositions.iterator();
        for(Rotor rotor : machineRotors) {
            if(it.hasNext()){
                char positionLetter = it.next();
                rotor.setInitialPosition(positionLetter);
                rotor.setPosition(this.alphabet.getOrder(positionLetter));
            }
        }


        // Getting reflector by ID
        Reflector reflector = getReflectorByID(reflectorID).orElseThrow(ReflectorNotFound::new);

        // Creating instance of Plugboard
        if(plugs == null){
            plugs = new HashMap<>();
        }
        Plugboard pb = new Plugboard(plugs, alphabet);

        // creating Enigma machine instance
        this.enigmaMachine = new EnigmaMachine(machineRotors,reflector, pb, alphabet);

        // Creating History Object to show all machine operations
        if(currentHistory != null){
            this.histories.add(currentHistory);
        }
        this.currentHistory = new History(enigmaMachine.toString());
        this.isMachinePresent = true;
        return this.enigmaMachine;
    }

    @Override
    public Enigma setupMachineAtRandom() {
        // Creating instance of random
        Random r = new Random();

        Set<Integer> rotors = new HashSet<>();
        List<Rotor> machineRotors = new ArrayList<>();

        while(rotors.size() < this.rotorsCount) {
            int i = r.nextInt(rotorList.size());
            if (!rotors.contains(i)) {
                Rotor rotor = rotorList.get(i);
                machineRotors.add(rotor);
                rotors.add(i);
            }
        }

        // Set Rotors at Random Position
        for(Rotor rotor : machineRotors){
            int pos = r.nextInt(this.alphabet.size());
            rotor.setInitialPosition(alphabet.getLetter(pos));
            rotor.setPosition(pos);
        }

        // Pick Reflector at Random
        Reflector machineReflector = reflectorList.get(r.nextInt(this.reflectorList.size()));

        // Random Generate Plugboard
        Plugboard pb = new Plugboard(this.alphabet);
        for (char letter : alphabet.getABC()) {
            int probability = r.nextInt(100);
            if(probability > 100){
                char letter2  = alphabet.getLetter(r.nextInt(alphabet.size()));
                if(letter != letter2){
                    // addToPlugboard will not add the pair in case one of the letters is in use
                    pb.addToPlugboard(letter,letter2);
                }
            }
        }

       this.enigmaMachine = new EnigmaMachine(machineRotors, machineReflector, pb, alphabet);

        if(currentHistory != null){
            this.histories.add(currentHistory);
        }
        this.currentHistory = new History(enigmaMachine.toString());
        this.isMachinePresent = true;
        return enigmaMachine;
    }

    @Override
    public EngineResponse<Message> processText(String text) {
        text = text.toUpperCase();
        Message m = enigmaMachine.processText(text);
        currentHistory.add(m);
        return new EngineResponse<>(m,true);
    }

    @Override
    public EngineResponse<?> getMachineInfo() {
        return new EngineResponse<>(this.enigmaMachine,true);
    }

    @Override
    public void resetMachine() {
        this.enigmaMachine.resetMachine();
    }

    @Override
    public int getNumOfReflectors() {
        return this.reflectorList.size();
    }

    @Override
    public int getTotalAvailableRotors() {
        return this.rotorList.size();
    }

    public int getNumberOfMachineRotors(){
        return this.rotorsCount;
    }

    @Override
    public int getTotalAvailableReflectors() {
        return this.reflectorList.size();
    }

    @Override
    public List<Reflector> getReflectorsFromEngine() {
        return this.reflectorList;
    }

    @Override
    public List<Rotor> getRotorsFromEngine() {
        List<Rotor> res = new ArrayList<>();
        for (Rotor rotor : this.rotorList) {
            res.add(new Rotor(rotor));
        }
        return res;
    }

    @Override
    public Rotors getNewRotors(List<Integer> rotorIds) {
        Iterator<Rotor> iterator = this.rotorList.iterator();
        List<Rotor> newRotorList = new ArrayList<>();
        for (int id :  rotorIds) {
            Rotor rotor = iterator.next();
            if(rotor.getID() == id){
                newRotorList.add(rotor);
            }
        }
        if(newRotorList.size() != this.rotorsCount){
            return null;
        }
        return new Rotors(newRotorList);
    }

    @Override
    public Battlefield getBattlefield() {
        return this.battlefield;
    }




    @Override
    public EngineResponse<MachineSettings> getEnigmaSettings() {
        if(!this.isLoaded){
            return new EngineResponse<>(null,false);
        }

        MachineSettings ms = new MachineSettings(rotorsCount, rotorList.size(), reflectorList.size(), this.alphabet);
        return new EngineResponse<>(ms, true);
    }

    @Override
    public EngineResponse<?> showHistory() {
        if(currentHistory == null){
            return new EngineResponse<>(null,false);
        }

        // Copying History of all previous configurations
        List<History> res = new ArrayList<>(histories);

        // Adding History of current machine in engine
        res.add(currentHistory);
        return new EngineResponse<>(res,true);
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public boolean isMachinePresent() {
        return isMachinePresent;
    }



    @Override
    public Enigma getMachine() {
        if(this.enigmaMachine == null){
            throw new EnigmaException("Request for Enigma machine - but machine was not created!");
        }
        return new EnigmaMachine(this.enigmaMachine);

    }

    @Override
    public Dictionary getDictionary() {
        return this.dictionary;
    }

    @Override
    public Alphabet getMachineAlphabet() {
        return this.alphabet;
    }

    private void checkForErrors() throws EnigmaException{
        int listSize = rotorList.size();
        if(rotorList.get(0).getID() != 1 || rotorList.get(listSize-1).getID() != listSize){
            String seq = "(1," + listSize +")";
            throw new InvalidConfigurationException("Invalid/Missing rotor ID in sequence " + seq);
        }
        if(rotorsCount < 2){
            throw new InvalidConfigurationException("Number of rotors used should be at least 2.");
        } else if (rotorsCount > rotorList.size()) {
            throw new InvalidConfigurationException("Number of rotors used should be less than or equal to the total number of rotors.");
        }

        for (Rotor r : rotorList) {
            r.validateRotor(this.alphabet);
        }

        if(reflectorList.size() > 5 || reflectorList.isEmpty()){
            String msg = "The number of reflectors has to be no more than 5 and at least 1";
            throw new InvalidConfigurationException(msg);
        }


        EnumSet<RomanNumeral> romans = EnumSet.noneOf(RomanNumeral.class);
        for (Reflector ref : reflectorList){
            ref.validateReflector(alphabet);
            RomanNumeral rn = ref.getRoman();
            if(romans.contains(rn)){
                String msg = "Reflector " +rn.name() + " is duplicated!";
                throw new InvalidConfigurationException(msg);
            }
            romans.add(ref.getRoman());
        }

        // Check if roman numbers are in sequence
        int i = 1;
        for(RomanNumeral rn : romans){
            if(rn.getValue() != i){
                String msg = "Reflector " + RomanNumeral.getRomanFromInt(i) + " is missing!";
                throw new InvalidConfigurationException(msg);
            }
            i++;
        }

    }



    private Optional<Reflector> getReflectorByID(String roman){
        Optional<Reflector> result = Optional.empty();

        // Iterating over all reflectors and comparing ID to find match
        for(Reflector ref : this.reflectorList){
            if (roman.equals(ref.getRoman().toString()))
                return Optional.ofNullable(ref);
        }
        return result;
    }


    public Decryption getDecryptionManager(String encrypted , Difficulty difficulty, long missionSize) {
        Decryption decryptionManager = new Decryption(this,encrypted,difficulty,missionSize);
        return decryptionManager;
    }
}
