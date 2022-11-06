package users.DTO;

import java.util.List;

public class MachineSetupData {
    List<Integer> rotorIDs;
    List<Character> rotorPositions;
    String reflector;

    public List<Integer> getRotorIDs() {
        return rotorIDs;
    }

    public List<Character> getRotorPositions() {
        return rotorPositions;
    }

    public String getReflector() {
        return reflector;
    }

    public MachineSetupData(List<Integer> rotorIDs, List<Character> rotorPositions, String reflector) {
        this.rotorIDs = rotorIDs;
        this.rotorPositions = rotorPositions;
        this.reflector = reflector;
    }
}
