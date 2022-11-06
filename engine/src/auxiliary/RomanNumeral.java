package auxiliary;

public enum RomanNumeral {
    Undefined(-1), I(1), II(2), III(3), IV(4), V(5);
    private final int value;
    public int getValue(){
        return value;
    }
    RomanNumeral(int num){
        this.value = num;
    }

    public static RomanNumeral getRoman(String s){
        try {
            return RomanNumeral.valueOf(s.toUpperCase());
        }catch(IllegalArgumentException iae){
            return Undefined;
        }
    }

    public static RomanNumeral getRomanFromInt(int num){
        switch (num){
            case 1 : return I;
            case 2 : return II;
            case 3 : return III;
            case 4 : return IV;
            case 5 : return V;
            default : return Undefined;
        }
    }

}
