package auxiliary;

public class MachineSettings{
    private final int numOfRotors;
    private final int rotorsCount;
    private final int numberOfReflectors;
    private final Alphabet abc;


    public MachineSettings(int rotorsCount, int nRotors, int nReflectors, Alphabet alphabet){
        this.rotorsCount = rotorsCount;
        numOfRotors = nRotors;
        numberOfReflectors = nReflectors;
        abc = alphabet;
    }
    public int getNumOfRotors(){
        return numOfRotors;
    }

    public int getRotorsCount() {
        return rotorsCount;
    }

    public int getNumberOfReflectors(){
        return this.numberOfReflectors;
    }

    public Alphabet getAlphabet() {return abc;}

    public RomanNumeral getMaximalReflector(){return RomanNumeral.getRomanFromInt(numberOfReflectors);}
}
